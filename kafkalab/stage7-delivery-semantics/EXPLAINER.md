# Stage 7, explained slowly — what problem are we actually solving?

---

## 1. The problem, stated without any Kafka words

You run a shop. A customer places one order, and your software charges their card once. That is the
entire requirement, and it is surprisingly hard to guarantee in a system made of separate machines
that can fail at any instant.

Here is why, in one scene. Your software sends a message to another machine and waits for a reply
saying "received". The reply does not arrive. You now have to decide what to do, and you are missing
the one piece of information that would make the decision easy: **you cannot tell the difference
between "it never arrived" and "it arrived, and the reply got lost on the way back"**.

If you assume it never arrived and resend, and you were wrong, the work happens twice. If you
assume it did arrive and stay quiet, and you were wrong, the work never happens at all.

That is not a Kafka problem. It is a property of sending messages over an unreliable network, and
no amount of clever protocol removes it. Every distributed system in the world lives with one of
two consequences, and this is where the two familiar phrases come from:

**At-most-once** means you never retry. Nothing is ever done twice, and some things are never done
at all. **At-least-once** means you always retry until you are sure. Nothing is ever missed, and
some things are done twice.

You derived exactly this in stage 3, when you asked whether to record your read position before or
after processing a record. Before means a crash loses it; after means a crash repeats it. You
noticed then that there was no third option. There still is not. What this stage adds is the set of
techniques that make at-least-once *behave* as if nothing were repeated — and an honest account of
where those techniques stop working.

## 2. Three different things all called "a duplicate"

Most of the confusion around this topic comes from people learning one fix and assuming it covers
all three of these. It does not. They happen in different places and need different answers.

**The producer duplicate.** Your code sends one order. The record reaches the broker's log. The
acknowledgement is lost on the way back. Your producer, seeing no reply, sends it again. The topic
now holds two copies of an order your code sent once. This is a *retry* duplicate, and it is
created entirely inside the client library — your code never asked for it.

**The consumer duplicate.** Your consumer reads an order, charges the card, and then crashes before
recording that it has read that far. When it restarts, its recorded position is still behind, so it
reads the same order again and charges the card again. Nothing the producer does can prevent this.
The producer is not involved at all; there is one record in the topic, and it was processed twice.

**The application duplicate.** A customer double-clicks the order button. Or an upstream service
retries its call to your API. Two genuinely separate records arrive, and to Kafka they are two
different orders that merely happen to describe the same intention. No Kafka mechanism can help
here, because from Kafka's point of view there is nothing wrong.

Hold on to that list. Almost every "we turned on exactly-once and still got duplicates" story is
someone who fixed the first and was being hurt by the second or third.

## 3. The first fix: the idempotent producer

The word **idempotent** means an operation you can perform repeatedly with the same end result as
performing it once. Turning off a light switch is idempotent; adding one to a number is not.

Kafka's idempotent producer works like a numbered ticket system. When you switch it on, the client
asks the broker for a **producer id**, and then stamps every batch it sends with that id plus a
**sequence number** that counts upwards separately for each partition. The broker keeps track of
the highest sequence number it has accepted from each producer for each partition. If a batch
arrives with a sequence it has already written, it recognises the resend, throws it away, and
replies as though it had succeeded.

So the retry still happens — nothing is slowed down or suppressed — but the second copy never
reaches the log. This is the fix for source number one, and it is very cheap. In modern Kafka it is
on by default, which is why stage 6 made you switch it *off* to measure anything honestly.

Now notice everything it does not do. The numbering belongs to one producer session: restart your
application and it gets a new producer id, so sending the same order again is a brand-new record
with no relationship to the old one. It knows nothing about consumers. And it certainly knows
nothing about a customer clicking twice. It solves exactly one of the three sources.

## 4. The second fix: transactions, and what they really cover

The second mechanism is more powerful and much more specific than its reputation suggests.

The scenario it was built for is a **processor**: something that reads records from one topic, does
a transformation, and writes results to another topic. That job has a nasty failure mode. You write
the output, and then you crash before recording how far you had read. On restart you read those
same inputs again and write the outputs again, and now the downstream topic has duplicates that no
producer setting can catch, because they came from two genuinely separate processing runs.

A Kafka transaction fixes this by making three things happen as a single unit: the output records,
and the consumer's new read position, are committed together or not at all. The critical piece is
that the read position is written **by the producer, inside the transaction**, rather than by the
consumer separately. There is no window in between for a crash to land in.

Two details make it work in practice. The producer is given a **transactional id** that must stay
the same every time the application restarts, because that is how the broker recognises a restarted
instance as the same logical writer, fences out the older one, and rolls back whatever it had left
half-finished. And a consumer reading the results must be told to ignore uncommitted work, which is
what `read_committed` does.

Say the covered scenario back to yourself precisely: **from Kafka, to Kafka**. Records come out of
one topic and go into another. That is a real and common shape — it is what stream-processing
frameworks are built on — and it is genuinely exactly-once. It is also the only shape that is.

## 5. What "aborted" means, which is not what most people picture

When a transaction is abandoned, nothing is erased. The records were already written into the log
files and they stay exactly where they are.

What changes is what readers are allowed to see. A consumer configured with `read_committed` stops
at a boundary called the **last stable offset** and filters out records belonging to transactions
that never committed. A consumer configured with `read_uncommitted` — which is the default — sees
them all.

The stage makes you read the same topic both ways and get two different answers from identical
bytes on disk. That is the correct picture, and it explains an operational surprise worth knowing:
a topic that has accumulated many aborted transactions is still holding all of that data, still
using that disk, and your consumers are still reading past it.

## 6. The boundary, which is the actual lesson of the stage

Now the case that neither mechanism touches, and the reason this stage exists.

Your consumer reads an order and charges a credit card. The charge is not a Kafka record. It is an
HTTP call to a payment company, and once it has happened it has happened. There is no Kafka
transaction that can roll back a credit card, and there never will be, because the payment company
has never heard of your transaction.

So when your consumer crashes after charging and before committing its position, the restart reads
that order again — and every Kafka feature in this stage is powerless. The idempotent producer is
irrelevant, because nobody produced anything. The transaction is irrelevant, because the side effect
was not inside it.

The only thing that helps is a record, kept by you, saying "order 1500 has already been handled".
Before doing the work, you check it. After doing the work, you write to it. This is called an
**idempotency key**, and every payment provider you will ever integrate with requires one, for
exactly this reason. It is also what you build in this stage, in about twenty lines.

And it has a gap, which you should look straight at rather than around. The charge and the mark are
two separate writes. If the process dies between them, the charge happened and nothing recorded it,
so the restart charges again. You have made the double charge much rarer, not impossible.

Closing that gap completely requires the side effect and the mark to be **one atomic write**. If
your side effect is a row in your own database, you can put the insert and the idempotency-key
record in a single database transaction, and then it is genuinely exactly-once — because both
halves succeed or neither does. If your side effect is an external API call, you cannot, and the
best available answer is to send the payment provider an idempotency key of your own and let *them*
deduplicate on their side.

That is the boundary. Kafka gives you exactly-once inside Kafka. Everything past that edge is your
database's problem, and the phrase "exactly-once delivery" is misleading enough that it is worth
being able to say precisely which half of the problem you are talking about.

## 7. So what is solved at the end of stage 7

You can stop retry duplicates from ever reaching the log, with a single setting. You can make a
read-process-write pipeline atomic, so a crash mid-pipeline leaves no trace and no repetition. You
can read a topic in a way that hides work that was abandoned. And you can protect an external side
effect with an idempotency key, while knowing exactly how large the remaining window is and what it
would take to close it.

What remains, and it is genuinely unsolvable at this layer: two clicks from a customer are two
orders. If you want those deduplicated, that is a business rule about what makes two orders "the
same", and only your application knows the answer.

---

## Quick recap of the vocabulary

- **Idempotent** — safe to do more than once; the end result is the same as doing it once.
- **At-most-once** — never retry. Nothing repeats, some things are lost.
- **At-least-once** — always retry. Nothing is lost, some things repeat.
- **Exactly-once** — neither lost nor repeated. Achievable inside Kafka, and outside it only with
  an idempotency key plus an atomic write.
- **Producer id and sequence number** — the numbered-ticket scheme the broker uses to recognise and
  discard a retried batch.
- **`enable.idempotence`** — the switch that turns that scheme on. Default true in modern clients.
- **`transactional.id`** — the stable name that identifies a logical writer across restarts, so the
  broker can fence out an old instance and roll back its unfinished work.
- **`sendOffsetsToTransaction`** — writes the consumer's read position inside the transaction, which
  is the line that makes read-process-write exactly-once.
- **`read_committed` / `read_uncommitted`** — whether a consumer is shown records from transactions
  that never committed.
- **Last stable offset** — the point a `read_committed` consumer stops at.
- **Idempotency key** — your own record that a given unit of work has already been done. The only
  thing that protects a side effect outside Kafka.

## Where to go next

The instructions, the experiments and the exit questions are in [README.md](README.md), in the same
folder. The matching page in your own notes is `delivery-guarantees.html`.
