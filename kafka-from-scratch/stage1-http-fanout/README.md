# Stage 1 — HTTP fan-out

**Plumbing:** the producer POSTs the event to four independent consumer services.

The microservice refactor everyone does. It fixes genuine problems from stage 0 — each consumer is
its own process now, with its own deploy, its own language, its own scaling, its own crash. It
also introduces a problem stage 0 didn't have: **the network**.

## Run

```bash
./stage1-http-fanout/run-all.sh
```

That starts payment:9001, inventory:9002, email:9003, analytics:9004 and order-service:8080.
Ctrl-C stops everything.

## Your job

`ConsumerService.java` TODO(1) — receive and apply. Ten lines.
`OrderService.java` TODO(2) — the fan-out, and it has a **five-rung ladder** in its javadoc.
Climb the rungs one at a time and run the experiments between each. Do not jump to rung 5.

## The experiments

### Experiment A — happy path, then look at the clock

```bash
./scripts/send-order.sh 8080 order-1
```

With a sequential loop you're back to additive latency, now *plus* four HTTP round trips. Move to
rung 4 (parallel) and measure again. Real gain — keep it.

### Experiment B — kill a consumer (the important one)

With everything running, kill inventory and send an order:

```bash
kill $(lsof -ti:9002)
./scripts/send-order.sh 8080 order-lost
```

Now restart inventory and ask it what it knows:

```bash
mvn -q -pl stage1-http-fanout exec:java \
  -Dexec.mainClass=kafkalab.stage1.ConsumerService -Dexec.args="inventory 9002"
curl -s localhost:9002/stats
```

**`order-lost` is not there and never will be.** Stock was never reserved. Nobody is going to
notice, because nothing in the system remembers that the delivery was owed. Say that out loud —
it's the single most important observation in the first four stages.

Then ask: where *should* that event have been sitting while inventory was down? Whatever you just
imagined, you imagined a log.

### Experiment C — retries buy you duplicates

Climb to rung 3 (retries). Set email's failure rate to 0.5 in `SimulatedService.email()` and fire
20 orders:

```bash
./scripts/burst.sh 20 8080 5
```

Watch the email service's console for `<-- DUPLICATE` lines. Then reason about the nastier case:
a request that *times out on the producer but succeeded on the consumer*. From where the producer
is standing, a timeout and a failure are the same bytes. There is no fix. You can only make the
consumer tolerate it — which is called idempotency, and stage 7 makes you build it.

### Experiment D — the config mesh

Add a fifth consumer, `fraud` on 9005, and get it receiving orders.

- Which files changed? Whose repo? Whose deploy?
- Now: the fraud team wants **last month's** orders to train on. What do you tell them?
- Now: fraud is slow (2s) and falls behind. Where does the backlog go? (Trick question — there is
  nowhere for it to go. The producer either blocks or drops.)

## Exit questions

1. Experiment B lost an event permanently. Name every component that would have had to change for
   the event to survive. (You are describing a broker.)
2. Why can't the producer distinguish "consumer never got it" from "consumer got it and the ack
   was lost"? Is this a bug in your code or a property of the universe?
3. Rung 4 gave you max-instead-of-sum latency but made the HTTP status meaningless. Why?
4. A slow consumer in stage 1 pushes back on the producer. In Kafka it doesn't. What structural
   difference makes that true?
5. What do subscriber-list-in-producer (TODO(0)) and "fraud wants last month's data" have in
   common? One thing fixes both.

## Where this maps in your notes

- N×M → N+M, and buffering / back-pressure absorption → `hld/kafka/index.html`
- Why a queue's delete-on-read is not enough → `hld/kafka/the-log.html`

## Next

Stage 2: put a durable buffer between them so Experiment B can't lose data. You'll reach for the
tool everyone reaches for — a database table — and discover exactly why it isn't a log.
