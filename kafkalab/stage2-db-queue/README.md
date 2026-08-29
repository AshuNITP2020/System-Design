# Stage 2 — The database queue

**Plumbing:** the producer `INSERT`s a row; workers poll the table and process what they find.

This is the design almost everyone reaches for after being burned by stage 1, and it genuinely
fixes the thing that hurt: **the event survives.** Kill a worker, restart it an hour later, and the
work still gets done — because the order is a row on disk, not bytes on a dead socket.

It also gives you the first two things stages 0 and 1 could not express: **pull** instead of push,
and an **interval**.

Then it breaks in five instructive ways.

---

## Purpose of this stage

To fix the thing that actually hurt in stage 1 — **the event now survives** — and then to
discover that a *queue* is structurally the wrong shape for the job. A queue distributes one
message to one consumer. You need all four to see everything. No tuning fixes that.

**You are here to discover:** that every problem in this stage comes from storing per-row,
per-consumer state in the shared thing — and that flipping it around (immutable rows, one number
per consumer) makes all five problems vanish at once. That number is an **offset**. Those rows are
a **log**.

## What is expected from you

| # | Task | Where | Size |
| - | ---- | ----- | ---- |
| 1 | One `INSERT`, then **measure the latency** | `OrderService.enqueue` TODO(1) | ~8 lines |
| 2 | The `SELECT` — **has the five-rung ladder** | `Worker.claim` TODO(1) | one rung at a time |
| 3 | `UPDATE … SET status='DONE'` | `Worker.markDone` TODO(2) | ~5 lines |
| 4 | Decide what happens to a row whose processing threw | `Worker` TODO(3) | a decision, then ~3 lines |

Start at rung 1 with the naive single-`status`-column query and **run only ONE worker.** Confirm it
works and that events survive with no worker running at all, *before* you break it.

| Rung | | Reveals |
| - | --- | --- |
| 1 | one worker, one `status` column | it works, and it's durable — real progress |
| 2 | run all four workers | **the wall** — non-deterministic partial delivery; measured 36–39 of an expected 48 |
| 3 | per-consumer columns | works, but a 5th consumer needs an `ALTER TABLE` + coordinated deploy |
| 4 | two workers of the *same* consumer | double-charge → you hand-write a lease protocol |
| 5 | someone asks for a replay | mutating shared state others are reading; impossible if you deleted rows |

## Done when you can answer

- [ ] Why does a single `status` column give you **distribution** when you wanted **fan-out**?
- [ ] What does shipping a fifth consumer cost at rung 3, and how is that the same problem as
      stage 1's `SUBSCRIBERS` list?
- [ ] What poll interval is both cheap and low-latency? (Trick question — say why.)
- [ ] All five problems trace to one design choice. Name it in a sentence.
- [ ] If rows were immutable and each consumer kept a single number — which problems survive?

**Status: not started.** Three TODOs open.

## Run

```bash
./kafkalab/stage2-db-queue/demo.sh
```

Resets the database, starts the producer, sends orders, shows the queue filling with `NEW` rows
while **zero** consumers have run, then starts the workers and shows the rows draining. Tears
everything down afterwards. Only works once your TODOs are done.

Interactively — producer in one terminal:

```bash
./gradlew :kafkalab:stage2-db-queue:run
```

and a worker in another (one per consumer):

```bash
./gradlew :kafkalab:stage2-db-queue:runWorker -Pworker=payment
```

`-Pworker`, not `-Pname` — `name` collides with Gradle's `Project.name`, the same trap as stage 1.
Add `-Ppoll=10` or `-Ppoll=2000` to change the polling interval for rung 4.

## Your job

Three TODOs.

| Where | What |
| --- | --- |
| `OrderService.enqueue` | one `INSERT`. Then **measure the latency** — it's the headline number of this stage |
| `Worker.claim` | the `SELECT`. Has the **five-rung ladder** in its javadoc |
| `Worker` failure branch | TODO(3): what to do with a row whose processing threw |

Start at rung 1 — the naive single-`status`-column query — and **run only one worker**. Confirm it
works before you break it.

The database file is `data/queue.db`, gitignored. When you change the schema, delete it so it gets
recreated.

## The experiments

### Experiment A — the number that justifies the whole stage

Send one order and look at the producer's latency via `/stats`.

Stage 0: **420ms.** Stage 1: **420ms + four round trips.** Stage 2: one local disk write.

You have decoupled the customer's checkout from every consumer's speed. Adding a fifth consumer
now costs the customer *nothing* — the first time that's been true.

### Experiment B — durability, the thing stage 1 couldn't do

Send 10 orders with **no workers running at all**:

```bash
curl -s localhost:8080/queue
```

Ten `NEW` rows. Nothing has been processed and nothing has been lost. Now start the workers and
watch them drain. In stage 1 those ten events would be gone.

### Experiment C — the wall (rung 2)

Start all four workers against the single `status` column, send 12 orders, and count the
applications. You want **48** (4 workers × 12 orders). Two measured runs:

| | payment | inventory | email | analytics | total |
| --- | --- | --- | --- | --- | --- |
| queue pre-filled | 10 | 10 | 7 | 12 | **39** |
| orders trickling in | 8 | 11 | 8 | 9 | **36** |

Neither 48 (fan-out) nor 12 (distribution). **A race.** On the second run payment charged 8 of 12
customers — four were silently never charged, and which four was decided by thread scheduling.

Two distinct faults, worth naming separately:

1. **One shared `status` column** cannot express "payment is done but email isn't."
2. **Claiming isn't atomic.** A row stays `NEW` for the *entire processing duration* — the gap
   between your `SELECT` and your `markDone`, 20–200ms — while every worker polls every 200ms. So
   several workers grab the same row.

Fixing only #2 gives you a *correct queue*: each order delivered once, to one consumer. But you
never wanted a queue — you want all four to see everything, and no amount of locking provides that.
A queue is structurally the wrong shape, and tuning cannot fix a shape.

### Experiment D — the double charge (rung 4)

Run two `payment` workers. Both `SELECT` the same rows before either `UPDATE`s. Watch for
`DUPLICATE` in the console — that's a card charged twice.

Then fix it with an atomic claim and notice you're now hand-writing a lease protocol, complete with
the question of what happens when a worker dies holding one.

### Experiment E — ask for a replay

Analytics found a bug and wants to reprocess the last hour. Work out exactly what you'd have to
run. Then ask what happens if you'd been deleting completed rows to stop the table growing forever.

## Exit questions

1. Why does a single `status` column give you distribution when you wanted fan-out?
2. Rung 3 fixed it with per-consumer columns. What does shipping a fifth consumer now cost, and
   how is that the same problem as stage 1's `SUBSCRIBERS` list?
3. What poll interval is both cheap and low-latency? (Trick question — say why.)
4. Every one of the five problems traces to one design choice. Name it in a sentence.
5. If a row were **immutable** and each consumer kept a **single number** instead of writing state
   back into the shared row — which problems survive?

## Notes cross-reference

- reading doesn't consume, and why that's the whole point → `the-log.html`
- offsets vs per-message acknowledgement → `consumers-and-groups.html`

## Next

Stage 3: you write the log. Append-only file, one number per consumer, and the five problems above
disappear together. Then you break *that* — torn writes, one machine, one disk — and every wall you
hit has a name in your notes.
