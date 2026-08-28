# Stage 0 — The monolith

**Plumbing:** direct method calls, same thread, same process.

This is the honest baseline. No queue, no broker, no JSON over the wire. An order arrives and the
same thread charges the card, reserves stock, sends the email and records analytics before
replying. Most real systems start here, and for a single-consumer app this is the *correct*
design — reaching for Kafka now would be the mistake your notes warn about.

---

## Purpose of this stage

To establish the honest baseline, and to prove — with numbers, not argument — that calling four
things from one call stack **welds them together**. Every later stage is a response to something
you measure here.

**You are here to discover:** that a single HTTP status code cannot describe four independent
outcomes, and that when the response says "failed", the customer may already have been charged.

## What is expected from you

| # | Task | Where | Size |
| - | ---- | ----- | ---- |
| 1 | Call `placeOrder`, respond 200/500 | `Monolith.java` TODO(1) | ~6 lines |
| 2 | Loop `SIDE_EFFECTS`, call `apply` — **naive, no try/catch** | `Monolith.java` TODO(2) | 3 lines |
| 3 | *After* running the experiments: catch per side effect, **log**, continue | `Monolith.java` TODO(3) | ~5 lines |

Then run **Experiments A, B and C** below, in order, and write down the numbers.

Do TODO(2) the dumb way first. If you jump straight to TODO(3) you will never see a 500, and
Experiment B — the whole point of the stage — produces nothing.

## Done when you can answer

- [ ] Why is the caller's latency the **sum** and not the max?
- [ ] For an order that returned HTTP 500, **was the customer charged?** How do you know from the
      output rather than from reading the code?
- [ ] Why is replay impossible here — what specifically is missing?
- [ ] Name the sentence you *cannot* satisfy in this design. (It's about two different failure
      policies.)

**Status: complete.** Measured 422ms against a 420ms sum; 7/20 orders returned 500 at 403ms vs
424ms, and that 21ms gap proved payment and inventory had already run.

## Run

From the repo root (`System Design/`). No separate build step — `run` compiles `common` and this
stage first, every time.

```bash
./gradlew -q --console=plain :kafkalab:stage0-monolith:run
```

Wait for `stage0 monolith on http://localhost:8080`. That terminal is now your server console —
it's where the `[payment] applied order-1` and `<-- DUPLICATE` lines appear. `Ctrl-C` stops it.

Then in another terminal:

```bash
./kafkalab/scripts/send-order.sh 8080 order-1
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
./kafkalab/scripts/send-order.sh 8080 order-1
```

Look at the `time_total` curl reports. Now: payment is 120ms, inventory 80ms, email 200ms,
analytics 20ms.

- What did you predict? What did you measure?
- The customer is waiting on `analytics`. Does the customer care about analytics?
- Reorder `SIDE_EFFECTS` to put analytics first. Does total latency change? Why not?

### Experiment B — one failure poisons the whole order

`email` fails 25% of the time. Send 20 orders:

```bash
./kafkalab/scripts/burst.sh 20 8080 1
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
