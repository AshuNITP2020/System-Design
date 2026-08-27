# The nine stages

Full spec so you can see the whole arc. Only stages 0–1 exist as code right now; each later module
gets scaffolded when you reach it, so you never read the answer to a problem you haven't felt yet.

Notes root: `~/Documents/lld-hld-notes/site/hld/kafka/`

**Live viewers** (`infra/README.md`): `./kafkalab/infra/up.sh` then <http://localhost:8081> for the web
console, `./kafkalab/infra/watch.sh` for the terminal LEADER/REPLICAS/ISR table. Stages 0–3 have no broker,
so there is nothing in them to view — the viewers start earning their keep at stage 4, and become
the main event at stages 5 and 6.

---

## Stage 0 — Monolith  *(scaffolded)*

Direct method calls in one process.

**Discovers:** latency is additive · one failure poisons the whole order · new consumer = edit the
producer · no history, so no replay.

---

## Stage 1 — HTTP fan-out  *(scaffolded)*

Producer POSTs to four services.

**Fixes:** independent deploy/scale/crash per consumer.
**Discovers:** a consumer that's down loses the event *forever* · retries create duplicates you
cannot detect · the subscriber list lives in the producer · a slow consumer back-pressures the
producer because there is nowhere to buffer.

---

## Stage 2 — The database queue

A SQLite table between producer and consumers. Producer INSERTs, workers poll, claim, process,
mark done. Everyone reaches for this at some point in their career.

**Fixes:** Experiment B — the event survives a consumer restart. Real, genuine progress.

**Discovers, in order:**
1. **Delete-on-consume kills replay.** You delete the row when done, so analytics can never
   reprocess. Keep the rows instead → now `status` is a single column shared by four consumers,
   which doesn't work. Add `payment_done`, `inventory_done`… → schema changes per consumer, which
   is the stage 1 coupling problem wearing a hat.
2. **Two workers, one row.** Run two payment workers without locking → same order charged twice.
   Add `SELECT ... FOR UPDATE` / a `locked_by` + `locked_until` claim → now you're writing a lease
   protocol, and a worker that dies mid-process holds the lease until it expires.
3. **Polling is a bad trade.** Poll every 10ms → hammering the DB. Every second → 1s latency floor.
   There's no setting that's both cheap and fast.
4. **The DB is now the bottleneck.** Every event is a write, an update, and N reads with row locks.
   Contention is on the hot tail of the table — the exact rows every worker wants.
5. **Ordering is gone.** Two workers pull rows 5 and 6 concurrently; row 6 finishes first. Two
   updates for the same SKU applied out of order. Fixing this means one worker per queue, and now
   you have no parallelism at all.

**Exit realization:** you want per-consumer read positions instead of per-row state, and append-only
writes instead of update-in-place row locking. Both of those are *the log*.

**Notes:** `the-log.html`, `consumers-and-groups.html` (offsets vs. per-message acks)

---

## Stage 3 — Build your own log  ← **the payoff stage**

Write an append-only file. You implement:

- `long append(byte[] record)` → returns an offset. Length-prefix your records.
- `List<Record> read(long fromOffset, int max)`
- per-consumer-group offset files, committed separately from processing

**Fixes:** replay (rewind analytics to offset 0 and watch a week reprocess) · four consumers with
four independent positions and zero shared state · sequential appends, so no lock contention ·
ordering preserved because there is exactly one writer.

**Then you break it on purpose:**
1. **Commit before or after processing?** Before → crash loses the record (at-most-once). After →
   crash reprocesses it (at-least-once). There is no third option. You have now *derived* the
   delivery-guarantee taxonomy rather than memorized it.
2. **Torn writes.** `kill -9` mid-append leaves a half record. Your reader crashes on startup
   forever. Fix: length prefix + CRC + truncate-to-last-valid-record on recovery. This is real
   Kafka's log recovery, and you'll have written it.
3. **One file, one writer, one disk.** Throughput ceiling. Split into N files by key hash → you
   just invented **partitions**, and immediately discover that ordering is now only guaranteed
   *within* a partition. That sentence from your notes will finally mean something.
4. **The file is on one machine.** The disk dies; the log dies. Copy to a follower → who decides
   when a write is "safe"? How far behind can a follower be and still count? You just invented
   **ISR** and **acks**.
5. **Where does the offset live?** In a file next to the log → the consumer must run on the same
   box. In its own topic → you just invented `__consumer_offsets`.
6. **Retention.** The disk fills. Delete oldest → what about a consumer still reading there?

**Exit realization:** you have written a single-node, unreplicated, crash-unsafe Kafka. Stage 4 is
where you stop writing it and start using it — and every config name will already be an answer to
a problem you personally hit.

**Notes:** `the-log.html`, `storage-internals.html`, `topics-and-partitions.html`

---

## Stage 4 — Real Kafka, one broker (KRaft)

Docker compose, single broker, no ZooKeeper. Same four consumers, now `KafkaConsumer`.

**Watch for:** `OrderService.SUBSCRIBERS` deletes down to one topic name · your stage 3 offset
file becomes `enable.auto.commit` and `commitSync()` · `auto.offset.reset=earliest` is your replay ·
`poll()` is not a poll loop over a DB, and understanding why is worth an hour.

**Do this:** diff your stage 3 code against your stage 4 code, class by class. That diff is the
list of things Kafka does for you.

**Viewer:** first stage where it's worth having open. Watch `orders` fill up in the console while
`./kafkalab/infra/watch.sh` shows your four groups' offsets advancing independently — the visual proof of
what you hand-built in stage 3.

**Notes:** `architecture.html`, `hands-on.html`, `kraft-vs-zookeeper.html`

---

## Stage 5 — Partitions, keys, consumer groups

Topic with 3 partitions. Key by `orderId`, then re-key by `userId`, and observe the difference.

**Experiments:** run 2 payment consumers in one group, watch the assignment · start a 3rd, watch
the rebalance stop-the-world · start a 4th with 3 partitions, watch it sit idle forever · key
everything to one value, watch one partition go hot · `assign()` vs `subscribe()`.

**Viewer:** essential here. Every one of those experiments is *a thing you watch happen* rather
than a thing you infer from logs. Console → Consumers shows which member owns which partition, and
it visibly reshuffles the moment you start or kill a consumer. The idle 4th consumer with 3
partitions is a one-line row in that table, and seeing it is worth more than the sentence
"you cannot have more consumers than partitions."

**Notes:** `topics-and-partitions.html`, `consumers-and-groups.html`, `rebalancing.html`

---

## Stage 6 — Replication, ISR, acks

3 brokers, RF=3 — `./kafkalab/infra/up.sh cluster`. Then `acks=0|1|all` × `min.insync.replicas=1|2`.

**Experiments:** `docker kill` the leader mid-produce and count lost messages per acks setting ·
stop two brokers with `min.insync.replicas=2` and watch producers refuse to write (availability
traded for durability, by *your* config choice) · `unclean.leader.election.enable=true` and watch
committed data vanish.

**Viewer:** the whole stage is a viewer stage. Keep `./kafkalab/infra/watch.sh` open in one split and run
`docker stop kafkalab-kafka2` in another. The ISR column goes `1,2,3` → `1,3`, the row turns yellow
as UNDER-REPLICATED, and any partition kafka2 led gets a new leader within seconds. Start it again
and watch the follower catch up and rejoin the ISR. That ~20 seconds of text output *is*
`replication-and-isr.html`.

**Notes:** `replication-and-isr.html`, `producers.html`

---

## Stage 7 — Delivery semantics

Make the duplicates from stage 1 Experiment C and stage 3 problem #1 go away — as far as they can.

`enable.idempotence` · `transactional.id` + `sendOffsetsToTransaction` · `read_committed` ·
then write a consumer-side dedupe store and find the boundary where Kafka's EOS stops and your
database's idempotency key starts.

**Notes:** `delivery-guarantees.html`

---

## Stage 8 — Retention, compaction, operations

`retention.ms` and watch old segments vanish · `cleanup.policy=compact` on a keyed topic and watch
it become a table · `kafka-consumer-groups --describe` and read lag · deliberately create lag by
slowing a consumer, then fix it by adding partitions and see why that's not free.

**Notes:** `retention-and-compaction.html`, `operations-and-tuning.html`, `cheatsheet.html`

---

## How to use this document

Don't read ahead more than one stage. The list of problems under each stage is the answer key —
reading it before you've run the experiment turns discovery into trivia, which is exactly the
failure mode you started this project to escape.
