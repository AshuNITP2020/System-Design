# Stage 0 — The monolith

**Plumbing:** direct method calls, same thread, same process.

This is the honest baseline. No queue, no broker, no JSON over the wire. An order arrives and the
same thread charges the card, reserves stock, sends the email and records analytics before
replying. Most real systems start here, and for a single-consumer app this is the *correct*
design — reaching for Kafka now would be the mistake your notes warn about.

## Build

```bash
mvn -q install -DskipTests
```

(`install`, not `compile` — `exec:java` below resolves `common` as a jar from your local Maven
repo. Re-run it whenever you change `common/`; for changes to `Monolith.java` alone, `exec:java`
recompiles the module itself.)

## Run

```bash
mvn -q -pl stage0-monolith exec:java
```

Then in another terminal:

```bash
./scripts/send-order.sh 8080 order-1
```

## Your job

Three TODOs in `src/main/java/kafkalab/stage0/Monolith.java`. Implement the obvious version —
resist making it good. Stage 0 is supposed to hurt in specific ways, and you want to *see* the
hurt, not design around it.

## The experiments

Run these in order. Write your answers down somewhere; stage 4 is much more satisfying if you can
compare it against what you actually observed rather than what you vaguely remember.

### Experiment A — latency is additive

```bash
./scripts/send-order.sh 8080 order-1
```

Look at the `time_total` curl reports. Now: payment is 120ms, inventory 80ms, email 200ms,
analytics 20ms.

- What did you predict? What did you measure?
- The customer is waiting on `analytics`. Does the customer care about analytics?
- Reorder `SIDE_EFFECTS` to put analytics first. Does total latency change? Why not?

### Experiment B — one failure poisons the whole order

`email` fails 25% of the time. Send 20 orders:

```bash
./scripts/burst.sh 20 8080 1
```

Count the 500s. For each failed order, ask the hard question: **payment ran before email.** So:

- Did the customer get charged on an order that returned HTTP 500?
- What will the client do when it sees a 500? (Hint: retry. What does a retry do to `payment`?)
- Hit `GET /stats` and look for `DUPLICATE` lines in the server log if you retried.

You have just met **at-least-once vs at-most-once** with real money on the line — the thing
`delivery-guarantees.html` describes abstractly. Note that Kafka does not make this problem go
away either; it gives you tools (idempotence, transactions) that stage 7 covers.

### Experiment C — the N×M pain, felt once

Add a 5th consumer: `SimulatedService.of("fraud", 150, 0.0)` in `SIDE_EFFECTS`.

- How many files did you edit? Whose deploy is required to ship a fraud detector?
- Now imagine `fraud` is owned by another team, on another release cadence, in another language.
- Now imagine 12 consumers instead of 5, and the latency budget from Experiment A.

That's the left panel of the diagram in your `index.html`. You just built it.

## Exit questions

Move on when you can answer all four without looking anything up:

1. Why is the caller's latency the **sum** and not the **max**? What would you change to make it
   the max, and what new problem does that create?
2. Order `order-42` returned HTTP 500. What is the actual state of the system? Can you tell from
   the response?
3. Analytics wants to reprocess last week. Why is that impossible here — what specifically is
   missing?
4. Name the one thing all three experiments have in common. (It's the reason stage 1 exists, and
   stage 1 will only fix *part* of it.)

## Where this maps in your notes

- The N×M problem and why Kafka exists → `hld/kafka/index.html`
- At-least-once / at-most-once → `hld/kafka/delivery-guarantees.html`

## Next

Stage 1 splits the four consumers into four separate HTTP services. Fixes: independent deploys,
independent languages, independent scaling. Does **not** fix: what happens when one is down.
