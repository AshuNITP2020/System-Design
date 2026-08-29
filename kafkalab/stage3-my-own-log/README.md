# Stage 3 — Build your own log

**Plumbing:** one append-only file, plus one number per consumer.

This is the stage everything has been building toward. You write ~40 lines and stage 2's entire
list of problems disappears at once — not because you fixed them, but because you removed the thing
that caused them.

---

## Purpose of this stage

To discover that a **log** is not a fancier queue — it's a different data model, and it's the one
that actually fits the problem. Then to break your own log until you have independently derived
segments, indexes, partitions, replication, and the at-least-once / at-most-once taxonomy.

**You are here to discover:** that "reading does not consume" is the whole trick. Stage 2 stored
per-consumer progress *inside the shared row*, so consumers fought. Move that progress *to the
consumer* and the contention has nowhere to live.

By the end of this stage you will have written a bad Kafka. Stage 4 is where you stop writing it
and start using it — and every config name will already be an answer to a problem you hit yourself.

## What is expected from you

| # | Task | Where | Size |
| - | ---- | ----- | ---- |
| 1 | `append` — length-prefix a record onto the file | `Log.java` TODO(1) | ~8 lines |
| 2 | `readFrom` — scan the file, rebuild records | `Log.java` TODO(2) | ~20 lines |
| 3 | Process the batch, **commit before or after** | `Consumer.java` TODO(3) | ~10 lines + a decision |

`Offsets.java` and `OrderService.java` are written for you. Storing a number in a file isn't the
lesson; *when you write it* is.

### The file format

```
[4-byte length][payload bytes][4-byte length][payload bytes]...
     int            UTF-8          int            UTF-8
```

`DataOutputStream.writeInt` / `DataInputStream.readInt` handle the 4 bytes, big-endian. The length
prefix is what makes the file parseable at all — and wall #5 is the second reason it exists.

**An offset is the index of a record.** First append is offset 0, second is 1. Not a byte position.

## Run

```bash
./kafkalab/stage3-my-own-log/demo.sh
```

Or interactively — producer:

```bash
./gradlew :kafkalab:stage3-my-own-log:run
```

and a consumer per group:

```bash
./gradlew :kafkalab:stage3-my-own-log:runConsumer -Pgroup=payment
```

Rewind one group to the beginning without touching the others:

```bash
./gradlew :kafkalab:stage3-my-own-log:runConsumer -Pgroup=analytics -Pfrom=0
```

`GET /log` shows the log size and every group's offset and lag.

## The walls, in order

### 1. The payoff — run all four consumers

**48 of 48, deterministically, every run.** Stage 2 gave you 36–39 with payment silently skipping
four customers. Nothing here is shared, so there is nothing to race over.

### 2. Replay

`-Pfrom=0` on analytics. It reprocesses history while the other three carry on undisturbed. In
stage 2 this meant `UPDATE`-ing rows that three other consumers were actively reading.

Also try adding a **fifth** consumer group. Cost: start a process. No schema change, no roster, no
producer edit. Compare to stage 1's `SUBSCRIBERS` list and stage 2's `ALTER TABLE`.

### 3. Commit before, or after?

`kill -9` the consumer mid-batch and prove which you built.

| | crash behaviour | name |
| --- | --- | --- |
| commit **before** processing | batch lost | **at-most-once** |
| commit **after** processing | batch reprocessed (`DUPLICATE`) | **at-least-once** |

There is no third option. Exactly-once needs the processing and the offset commit to be one atomic
operation — yours live in a file and a service that know nothing about each other.

### 4. The O(n) read

Every poll rescans from byte 0. Append 50,000 records and watch a consumer at offset 49,000 crawl.
Real Kafka splits the log into **segments** with a sparse **`.index`** mapping offset → byte
position. You just derived both.

### 5. Torn writes

`kill -9` the producer mid-append, or chop a few bytes off the file by hand. Your reader now dies
on startup **forever** — `readInt()` returns a length running past end-of-file. Real logs add a
**CRC** and recover by truncating back to the last complete record. Try writing that recovery.

### 6. One file, one writer, one disk

Throughput has a ceiling you cannot spread. Split by `hash(key) % N` and you have invented
**partitions** — and lost total ordering, keeping it only *within* each file. Then ask what happens
when the disk dies: you have invented **replication**, and the question of how far behind a copy
may be while still counting is **ISR**.

## Done when you can answer

- [ ] Why does the log give 48/48 where the queue gave 36/48? Name the structural difference.
- [ ] Why can't you have exactly-once with these two lines of code, no matter how you order them?
- [ ] What does adding a sixth consumer group cost here vs. stage 1 vs. stage 2?
- [ ] Your read is O(n). What two structures does Kafka add, and which problem does each solve?
- [ ] You split the file by key hash. What did you gain, and what exact guarantee did you lose?

**Status: not started.** Three TODOs open.

## Notes cross-reference

- `the-log.html` — the whole page is this stage
- `storage-internals.html` — segments and indexes (wall #4)
- `delivery-guarantees.html` — at-most / at-least once (wall #3)
- `topics-and-partitions.html` — partitions and per-partition ordering (wall #6)

## Next

Stage 4: real Kafka, one broker, KRaft. Diff your `Log.java` against `KafkaProducer` and your
`Consumer.java` against `KafkaConsumer`. That diff is the list of things Kafka does for you — and
you'll recognise every item, because you hit them all here.
