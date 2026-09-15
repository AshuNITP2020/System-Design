# Stage 7 — Delivery semantics: making the duplicates go away, as far as they can

**Plumbing:** back to a single broker. Replication was stage 6 and would only add noise here.

```bash
./kafkalab/infra/up.sh single
```

**New to this material?** Read [EXPLAINER.md](EXPLAINER.md) first. It explains, in plain language
and with no code, where duplicates come from, why there are three separate sources of them, and
what "exactly-once" does and does not mean. This README is the instruction sheet; that one is the
reasoning.

---

## Where this stage comes from

Every earlier stage created duplicates and you let them stand.

In stage 1 a retried HTTP call charged a customer twice, and you had no way to tell a retry from a
new order. In stage 3 you derived the delivery-guarantee taxonomy yourself by asking whether to
commit your read position before or after processing: before loses records on a crash, after
repeats them, and there is no third option. In stage 6 you fixed data loss — and the fix was
retrying, which is precisely how the same record gets stored twice.

So this stage has a debt to pay. The question is no longer "how do I avoid losing writes" but
"how do I stop the same thing happening twice", and the honest answer has a boundary in it that
most descriptions of Kafka skip over.

## The one sentence this stage exists to make true

> **Exactly-once is not a property you switch on. It is a property of a specific closed loop —
> consume from Kafka, produce to Kafka — and the moment your side effect leaves that loop, it
> becomes your own database's problem again.**

You will prove that by making duplicates disappear twice, in two completely different ways, and
then by finding the case where neither of them helps.

## What we cover

### 1. There are three separate sources of duplicates, and they need three separate fixes

This is the part that makes the topic confusing, because people learn one fix and assume it covers
everything.

The **producer** duplicates when it retries a send whose acknowledgement was lost. The record
reached the log; the confirmation did not come back in time; the client resends it. Your code sent
one order and the topic holds two.

The **consumer** duplicates when it crashes after doing its work but before recording that it did.
On restart it reads the same records again and does the work a second time. Nothing the producer
does can prevent this, because the producer is not involved.

The **application** duplicates when a human clicks "Place order" twice, or an upstream service
retries your HTTP endpoint. Neither Kafka mechanism sees this at all: those are two genuinely
different records from Kafka's point of view.

### 2. The idempotent producer, and exactly what it covers

Switching on `enable.idempotence` makes the client attach two things to every batch: a **producer
id** that the broker assigns, and a **sequence number** that counts up per partition. The broker
remembers the last sequence it accepted for each producer and partition, and silently discards
anything it has already seen. So a retry of a batch that did land is recognised and dropped.

Notice the limits built into that description. It works per producer session and per partition. It
does not survive you restarting the producer and deliberately sending the same order again, because
that is a new producer id and, as far as Kafka can tell, a new record. It is a fix for *retry*
duplicates, and nothing else.

It also, quietly, fixes ordering. Without idempotence, several requests in flight plus retries
means a resent batch can be written after a batch you sent later. With idempotence on, the broker
uses those sequence numbers to keep them in order, up to five in flight.

### 3. Transactions, and the narrow thing they actually do

A transactional producer takes a `transactional.id` and can group several sends into an
all-or-nothing unit. The important and under-advertised part is `sendOffsetsToTransaction`: the
consumer's read position is written **by the producer, inside the transaction**, together with the
output records.

That is what closes the read-process-write loop. Either the outputs exist and the input is marked
as read, or neither happened. You cannot end up in the state where you produced the output and then
crashed before committing the offset, which is exactly the state that causes reprocessing.

The `transactional.id` has to stay the same across restarts, because that is how a restarted
instance is recognised as the same logical writer, and how a transaction the previous instance left
dangling gets fenced off and rolled back.

### 4. `read_committed`, and where aborted records actually go

An aborted transaction does not erase anything. The records were written to the log and they stay
there. What changes is that consumers reading with `isolation.level=read_committed` stop at the
**last stable offset** and filter out anything belonging to a transaction that did not commit.

The stage makes you read the same topic both ways and see two different answers over identical
bytes on disk. This is the correct mental model, and it explains a real operational surprise: a
topic full of aborted transactions still consumes disk, and your consumers still have to skip past
those records.

### 5. The boundary — where Kafka stops and your database begins

Then the last exercise, which is the one worth remembering. Your consumer charges a credit card. No
Kafka transaction can roll back a credit card. Kafka's guarantees end at the edge of Kafka.

What actually protects the customer is an **idempotency key**: before doing the work, you check a
store to see whether this order has already been handled, and afterwards you record that it has.
Every payment provider you will integrate with works this way and calls it exactly that.

And that store has a gap. The charge and the mark are two separate writes, so a crash between them
still charges twice. The gap is small, and making it zero requires both writes to happen in a
single transaction of the same database — which is a thing you can build, and a thing Kafka cannot
give you. Knowing precisely where that line sits is more useful than the phrase "exactly-once".

---

## What is expected from you

Four TODOs, in three files.

**TODO(1)** in `OrderProducer.producerProps` — the producer config, including the idempotence
switch that this stage turns on and off. Around eight lines.

**TODO(2)** in `AtLeastOnceConsumer.consumeLoop` — poll, charge, mark, commit. The commit placement
is the at-least-once decision you already know from stage 3, now with a real side effect behind it.
Around fifteen lines.

**TODO(3a)** and **TODO(3b)** in `DedupeStore` — the idempotency store itself. Three lines of code
and one genuinely hard question in the javadoc about the order of operations.

**TODO(4)** in `TransactionalProcessor.processLoop` — the transaction lifecycle: `initTransactions`
once, then begin, send, `sendOffsetsToTransaction`, and commit or abort. Around twenty lines, and
the only new API in the stage.

Everything else is written for you: the charge ledger, the duplicate audit, the deliberate crash,
the offset map, and the demo scripts.

## API notes

**The transactional loop** — the shape, without the details you have to decide:

```java
producer.initTransactions();                       // once, before the loop
while (true) {
    var records = consumer.poll(Duration.ofSeconds(1));
    producer.beginTransaction();
    // sends...
    producer.sendOffsetsToTransaction(offsets, consumer.groupMetadata());
    producer.commitTransaction();                  // or abortTransaction()
}
```

`consumer.groupMetadata()` is how the producer proves to the broker which group's offsets it is
allowed to write. `offsetsOf(records)` is provided and returns the offset **after** the last record
of each partition, because a committed offset means "the next record I want", not "the last one I
read".

**Things that silently break a transaction:** leaving `enable.auto.commit` on, calling
`consumer.commitSync()` anywhere in the loop, or generating a new `transactional.id` on each start.
All three compile and run, and all three give you back the duplicates you were trying to remove.

## Run

Three demos, in this order. Each one leaves the next one a problem to solve.

```bash
./kafkalab/stage7-delivery-semantics/demo.sh dupes
```

```bash
./kafkalab/stage7-delivery-semantics/demo.sh crash
```

```bash
./kafkalab/stage7-delivery-semantics/demo.sh tx
```

By hand:

```bash
./gradlew :kafkalab:stage7-delivery-semantics:run -Pidem=false -Ptimeout=25
```

```bash
./gradlew :kafkalab:stage7-delivery-semantics:audit -Ptopic=orders -Pisolation=read_committed
```

```bash
./gradlew :kafkalab:stage7-delivery-semantics:runConsumer -Pcrash=20 -Pdedupe=true
```

```bash
./gradlew :kafkalab:stage7-delivery-semantics:runProcessor -Pabort=3
```

## The experiments

### A — manufacture duplicates, then remove them

Run the `dupes` demo. It produces 500 orders with a `request.timeout.ms` of 3 milliseconds, which
is short enough that the client regularly gives up on a reply the broker is about to send. Every
one of those timeouts becomes a retry, and every retry that duplicates a record the broker already
wrote leaves an extra copy in the log.

The number is tuned to a broker on localhost, which answers in a millisecond or two — measured
here, 25ms never fired once, and 3ms manufactured 58 extra copies out of 500. If your first run
shows zero extra copies, the timeout is not beating your broker's reply time: lower it
(`TIMEOUT=2 ./kafkalab/stage7-delivery-semantics/demo.sh dupes`). This sensitivity is itself the
lesson — a duplicate needs the reply to lose the race, so the same code duplicates or not
depending on nothing but latency.

Your loop sends each order id exactly once, so every duplicate the audit finds was manufactured by
the retry machinery rather than by your code. Then the same run with `enable.idempotence=true`
should show zero extra copies, with the retries still happening. Nothing was slowed down and no
retry was removed — the broker simply refused to write a sequence number it had already written.

If you want the ordering lesson too, set `max.in.flight.requests.per.connection` to 5 with
idempotence off and check whether the keys come back in the order you sent them.

### B — the duplicate the producer setting cannot touch

Run the `crash` demo. It charges twenty customers and then kills the process before the offset is
committed, exactly the way a real crash would. On restart the consumer has no committed position,
re-reads from the beginning, and charges those twenty customers a second time.

The producer's idempotence setting is on for that entire run and makes no difference whatsoever,
because the producer is not the one duplicating. This is the moment where "we enabled exactly-once"
turns out to have meant nothing at all.

Then the demo does it again with your idempotency store in the way, and the second run skips the
work it has already done.

### C — the gap that remains

Look at the order of operations in your own `consumeLoop`. You charge, then you mark. Ask what
happens if the process dies between those two lines, and satisfy yourself that the answer is a
double charge — rarer than before, but still possible.

Then work out what it would take to make it impossible. The answer is not a Kafka setting. This is
the exercise of the stage; do not skip it.

### D — transactions and the two views of one topic

Run the `tx` demo. A processor reads `orders`, writes `orders-validated`, and deliberately aborts
every second transaction. Afterwards, read the output topic twice, once with each isolation level,
and compare the counts.

Predict first: after an aborted batch, are those input records read again? Are the outputs you
already sent for them still in the log? Which of the two readers can see them?

### E — break it on purpose

Add a `consumer.commitSync()` inside the transactional loop and watch the guarantee quietly stop
being a guarantee. Or start the processor twice with the same `transactional.id` and watch the
older instance get fenced out with `ProducerFencedException` — that fencing is the mechanism that
makes a restart safe.

## Done when you can answer

- [ ] A record appears twice in a topic. Give three different mechanisms that could have put it
      there, and which fix addresses each.
- [ ] `enable.idempotence=true` is on and a customer is charged twice. Explain how, without
      contradicting the setting.
- [ ] What does `sendOffsetsToTransaction` do that committing the consumer separately does not?
- [ ] Where do the records of an aborted transaction physically live, and who can see them?
- [ ] You have a consumer that writes to Postgres. What is the smallest change that makes it
      effectively exactly-once, and why is it not a Kafka feature?
- [ ] When is at-most-once the right choice? Name a real case.

**Status: implemented and measured** (2026-09-13). All four TODOs closed. The measured results,
one line per demo:

*dupes* — 500 orders at `request.timeout.ms=3` with idempotence **off**: 558 records in the topic,
58 extra copies, all manufactured by retries, and the producer reported zero errors throughout.
Same run with idempotence **on**: exactly 500, zero extras.

*crash* — 20 charges, then `halt(9)` before the commit. The restart double-charged **10**
customers, not 20 — the first batch of 10 had already been committed, so only the uncommitted
batch replayed. The unit of repetition is the uncommitted batch, which is precisely what the
`commitSync()` placement dictates. With the dedupe store on: `applied 20, skipped 10`, forty
distinct customers, zero double charges.

*tx* — 60 inputs, aborting every 2nd transaction: `read_uncommitted` sees all 60 output records,
`read_committed` sees 23. Same bytes on disk, two answers; the 37 invisible ones belong to
transactions that never committed.

One subtlety the tx numbers reveal, worth keeping: after an abort, the consumer does **not**
re-read those inputs in the same session. Its position advances in memory as it polls, commit or
no commit — the uncommitted offset only matters on the *next* start, which reads from the last
committed position. So aborted work is retried after a restart, not on the next poll. The crash
demo showed the same thing from the other side: replay is a restart phenomenon.

## Notes cross-reference

- `delivery-guarantees.html` — the taxonomy you derived in stage 3, now with the machinery
- `producers.html` — idempotence, retries, in-flight requests

## Next

Stage 8: retention, compaction and operations. Your log has been growing without limit for seven
stages. You will make old data expire, turn a keyed topic into a table with compaction, and learn
to read consumer lag — then create lag deliberately and discover why adding partitions to fix it is
not free.
