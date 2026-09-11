# Stage 6 — Replication, ISR, and `acks`

**Plumbing:** three brokers, `RF=3`. Same topic, same records. The new thing is that a machine
can *die* and the system is supposed to survive it.

```bash
./kafkalab/infra/up.sh cluster
```

**New to this material?** Read [EXPLAINER.md](EXPLAINER.md) first. It explains the whole problem
from the beginning in plain language, with no code and no shorthand — what a leader is, why a copy
of your data is not the same as an up-to-date copy, and a step-by-step timeline of how an
acknowledged order gets destroyed. This README is the instruction sheet; that one is the reasoning.

---

## Where this stage comes from

Stage 3, problem #4, in your own words: *"the file is on one machine. The disk dies; the log
dies. Copy it to a follower — but who decides when a write is safe, and how far behind can a
follower be and still count?"*

You invented the questions. This stage is where Kafka's answers get names:

| Your stage 3 question | Kafka's answer |
| --- | --- |
| who has a copy? | **replicas** — the broker list for a partition |
| who is *caught up enough* to count? | **ISR**, the in-sync replica set |
| when is a write "safe"? | **`acks`** — the producer's definition of done |
| how many copies must exist before I accept a write? | **`min.insync.replicas`** — the broker's veto |
| who takes over when the leader dies? | **leader election**, from the ISR |

Stages 4 and 5 both ran a single broker, so `RF` was stuck at 1 and none of this could be
observed. Now it can.

## The one sentence this stage exists to make true

> **Durability and availability are not properties of Kafka. They are a choice you configure,
> and you cannot have the maximum of both.**

You will prove that by making the *same* cluster lose data and then refuse writes, changing
nothing but two config values.

---

## What we cover

### 1. The partition is the unit of replication, not the topic

`RF=3` on a 3-partition topic means nine copies, arranged so each broker leads one partition and
follows two. `kafka-topics.sh --describe` prints `Leader / Replicas / Isr` per partition — three
different lists that people routinely conflate.

- **Replicas** — the assigned set. Static; it does not change when a broker dies.
- **ISR** — the subset currently caught up. Shrinks and grows on its own.
- **Leader** — one member of the ISR. All reads and writes for that partition go through it;
  followers only replicate. (Follower fetching exists, but it is a rack-locality optimisation,
  not load balancing.)

### 2. What "in sync" actually means

Not "identical". A follower is in the ISR while it has fetched from the leader within
`replica.lag.time.max.ms` (30s default). So the ISR is a *liveness* test, not an equality test —
which is exactly why an ISR member can still be missing the last few records, and why unclean
leader election can lose data.

### 3. `acks` — three different definitions of "written"

| `acks` | The producer waits for | Loses data when |
| --- | --- | --- |
| `0` | nothing. The record hits the socket buffer and the callback fires | anything at all goes wrong, including the broker being *already dead* |
| `1` | the leader's local log | the leader dies before a follower replicates that record |
| `all` | every member of the current ISR | the ISR had shrunk to 1 and that one dies — this is what `min.insync.replicas` is for |

Note the trap in row 3: `acks=all` alone does **not** mean "three copies". It means "everyone
currently in the ISR", and the ISR can legally be a single broker.

### 4. `min.insync.replicas` — the other half of `acks=all`

A *broker/topic* config, not a producer one. With `min.insync.replicas=2`, a partition whose ISR
has fallen to 1 rejects writes with `NotEnoughReplicasException` instead of accepting them into a
single copy. That rejection **is the feature**: the cluster chose to be unavailable rather than
to accept a write it might lose.

`acks=all` + `min.insync.replicas=2` on `RF=3` is the standard durable configuration, and it is
worth being able to say why that specific triple: it survives one broker loss with no data loss
and no downtime, and it tells you loudly when a second one goes.

### 4b. Why no consumer ever sees the records you lose

There is exactly one place where replication reaches the read side, and it explains a result that
would otherwise look strange.

Kafka does not let a consumer read a record until that record has been copied to every broker in
the partition's current in-sync set. The offset up to which reading is permitted is called the
**high water mark**, and it moves forward only as replication completes. No consumer setting lets
you read past it.

So when you lose records at `acks=1`, you are not losing records that somebody had already read.
Those records sat on the leader's disk, never replicated, below the high water mark, and therefore
invisible to every reader in the cluster. Then the leader died, a follower that had never received
them was promoted, and they ceased to exist. The failure is quieter than "a consumer saw something
that later vanished", and worse: the producer was told the write succeeded, and nothing downstream
was ever going to be given the record.

This is also why stage 6 has no `payment` / `inventory` / `email` / `analytics` consumers in it.
Replication is a question about the write path only, and the read side was already covered — four
independent groups in stage 4, several members sharing one group in stage 5. The single reader
here, `LossAudit`, is an auditor rather than a participant: no `group.id`, no committed offsets,
`assign()` instead of `subscribe()`, so it can run repeatedly without disturbing the experiment.

### 5. Leader election and the unclean option

When a leader dies the controller promotes an ISR member. If the ISR is *empty* — every replica
is down — you choose:

- `unclean.leader.election.enable=false` (the compose default): the partition stays offline until
  a valid replica returns. Consistency over availability.
- `true`: an out-of-sync replica becomes leader, and every record it never received is
  **silently gone** — records the producer was told were committed. This is the most dangerous
  switch in Kafka and you should watch it eat acknowledged data exactly once, on purpose.

### 6. Measuring loss instead of believing a table

The experiment harness in this module writes down every key the producer was *told* was
acknowledged, then reads the topic back afterwards and diffs. "Acked but absent" is data loss you
can count, which is a very different experience from reading that `acks=1` is "weaker".

---

## What is expected from you

Three TODOs. All of them are small; the stage is a watching stage, like stage 5.

| # | Where | Size | What it is |
| - | ----- | ---- | ---------- |
| 1 | `OrderProducer.producerProps` | ~8 lines | turn the `acks` / retry knobs into config |
| 2 | `OrderProducer` send callback | ~8 lines | record what was acknowledged, count what failed |
| 3 | `LossAudit.report` | ~10 lines | the set difference that turns a hunch into a number |

## API notes — everything you need

**Producer config** (all constants on `ProducerConfig`):

```java
props.put(ProducerConfig.ACKS_CONFIG, "all");            // "0" | "1" | "all" — a STRING
props.put(ProducerConfig.RETRIES_CONFIG, 0);
props.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, 5000);
props.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 2000);
props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, false);
```

`enable.idempotence` defaults to **true** in kafka-clients 3.x+, and it silently forces
`acks=all` and `retries=Integer.MAX_VALUE`. If you leave it on, `-Packs=1` is a lie and your
measurements are meaningless. Turning it off is part of TODO(1) — you turn it back on deliberately
in stage 7, which is that feature's own stage.

The two timeouts are not independent, and picking round numbers for both will stop the producer
from starting at all with `delivery.timeout.ms should be equal to or larger than linger.ms +
request.timeout.ms`. Three settings are involved. `request.timeout.ms` is how long to wait for a
reply to **one** attempt. `linger.ms` is how long the producer pauses after you call `send`, hoping
to batch a few records into the same request — it defaults to 5ms in Kafka 4.0, not 0. And
`delivery.timeout.ms` is the **total** budget for a record, from `send` until your callback fires,
covering the linger, the attempt, and every retry. Kafka insists the total can fit at least one
whole attempt, so `5000` and `5000` is rejected: 5 + 5000 is more than 5000. Give the total room —
either shorten the per-attempt wait or lengthen the budget, and know which one you chose and why.

**The async send with a callback** — this is the shape you want, not `.get()`:

```java
producer.send(record, (RecordMetadata md, Exception e) -> {
    if (e != null) { /* the send failed and you know it */ }
    else           { /* md.partition(), md.offset() */ }
});
```

`.get()` would make every send synchronous and the run would take minutes; more importantly, a
broker dying mid-flight is only interesting when there are records in flight. Note the callback
runs on the producer's I/O thread, so anything it touches must be thread-safe (`AtomicInteger`,
a synchronized writer — both provided).

With `acks=0` the callback fires with `e == null` and `md.offset() == -1`. The producer never
asked, so it has nothing to report. Worth pausing on: "acked" under `acks=0` means "written to a
socket".

**Reading a topic to its end** is provided in `LossAudit.drainTopic()` — `assign` + `endOffsets` +
`seekToBeginning`, no group, no commits. It is plumbing, not the subject.

## Run

The whole stage in one command:

```bash
./kafkalab/stage6-replication/demo.sh
```

Knobs:

```bash
ACKS=0   ./kafkalab/stage6-replication/demo.sh          # measure loss with acks=0
ACKS=1   ./kafkalab/stage6-replication/demo.sh          # ...and acks=1
ACKS=all ./kafkalab/stage6-replication/demo.sh          # ...and acks=all
MIN_ISR=1 ACKS=all ./kafkalab/stage6-replication/demo.sh
./kafkalab/stage6-replication/demo.sh refuse            # kill TWO brokers, watch writes rejected
```

By hand:

```bash
./gradlew :kafkalab:stage6-replication:run -Packs=all -Pcount=2000
./gradlew :kafkalab:stage6-replication:audit
```

**Keep `./kafkalab/infra/watch.sh` open in a split for all of it.** This stage is 80% watching
that ISR column.

## The experiments

### A — read the layout before you break it

```bash
./kafkalab/infra/kcli.sh kafka-topics.sh --describe --topic orders
```

Three partitions, `Replicas: 1,2,3`, `Isr: 1,2,3`, a different leader each. Say out loud what
each of the three lists means before continuing. Then `docker stop kafkalab-kafka2` and describe
again: `Replicas` is unchanged, `Isr` is now `1,3`, and any partition kafka2 led has a new leader.
`docker start kafkalab-kafka2` and watch it rejoin — the catch-up is visible in `watch.sh`.

### B — count the loss, per `acks`

Run the demo three times, `ACKS=0`, `1`, `all`, with `min.insync.replicas=2`. Same script, same
kill, same record count. Fill this in yourself:

| `acks` | attempted | acked | send errors | present after | **lost** |
| --- | --- | --- | --- | --- | --- |
| `0` | 2000 | 2000 | 0 | 1969 | **31** |
| `1` | 2000 | 1999 | 1 | 1999 | 0 |
| `all` | 2000 | 1994 | 6 | 1994 | 0 |

Measured on 2026-09-10, one broker lost to a graceful `docker stop` mid-produce.

Read the **send errors** column, not just the loss column. As the guarantee strengthens the number
of *reported* failures goes up, and that is the real product of this setting: `acks` does not make
failure go away, it converts silent loss into an error your code is given the chance to handle.
Six failures you know about beat thirty-one you do not. The six at `acks=all` were records the
leader had written but the followers had not yet confirmed — at `acks=1` those same records would
have been counted as successes.

**If `acks=1` loses nothing, that is a real result and not a broken experiment.** `docker stop`
sends a polite termination signal, and Kafka answers it with a *controlled shutdown*: the broker
hands leadership of its partitions to a follower that is already fully caught up, and only then
exits. That handover is exactly the thing that would have lost your records, so a graceful stop
usually loses none. Which is worth knowing in its own right — a planned restart of a Kafka broker
is genuinely safe, even at `acks=1`.

To see the loss you have to deny the handover, and produce fast enough that records are still
un-replicated at the instant the leader dies:

```bash
HARD=1 PAUSE=0 COUNT=300000 ACKS=1 ./kafkalab/stage6-replication/demo.sh
```

The large count is not decoration. Two conditions have to hold at the same moment the broker dies:
the producer must still be running, and it must be pushing records faster than the followers can
copy them. At `PAUSE=0` the default 2000 records are gone in under a second, so the run is over
before anything is killed and the audit dutifully reports zero of everything — a passing result
that measured nothing. If you see `zero lost` together with `zero send errors` after a SIGKILL,
that is the signature of this mistake, not of a durable configuration.

`HARD=1` uses `docker kill`, which is an immediate SIGKILL with no chance to hand anything over.
Now the controller picks a new leader from whoever is left, that broker's log may end a few records
early, and when the killed broker returns it truncates its own log to match. Those records were
**acknowledged**. Your callback said success, your HTTP handler would have returned 200, and the
data is gone.

Run the same hard kill at `acks=all` and the loss should return to zero, because the ack was never
given until the followers had it. That pair of runs — same violence, different setting — is the
cleanest version of the whole stage.

`acks=0` is worth running twice — once with the kill, and once against a broker that was already
down before you started. Both produce a happy log.

### C — availability as the price of durability

```bash
./kafkalab/stage6-replication/demo.sh refuse
```

`min.insync.replicas=2`, two of three brokers stopped. The surviving broker is healthy, has the
data, and **refuses to accept writes** — `NotEnoughReplicasException`. Nothing is broken. The
cluster is doing precisely what you configured.

Now re-run with `MIN_ISR=1`: the writes go through. You just traded a durability guarantee for an
availability one, in one integer, and both answers are defensible. That trade is the stage.

### D — unclean leader election eats committed data

The dangerous one; do it once, deliberately.

1. `min.insync.replicas=1`, `acks=1`, produce a burst.
2. Stop the *followers* of a partition, keep producing to the leader alone.
3. Stop the leader too. Start a follower first, with
   `unclean.leader.election.enable=true` on the topic.
4. The out-of-sync follower becomes leader. Audit: acknowledged records are gone, and the log end
   offset went *backwards*.

Nothing warns you. This is why that flag is false by default and why "we turned it on to get the
partition back online" is a sentence with consequences.

### E — the KRaft quorum is separate

`watch.sh` shows the metadata quorum above the partition table. Stop one broker: quorum 2/3,
cluster fine. Stop two: no quorum, and now the *controller* is down — topic creation and leader
election stop even though a broker is still running. Data-plane replication and metadata
replication are two different quorums, and only one of them is `RF`.

## Done when you can answer

- [ ] `Replicas: 1,2,3` and `Isr: 1,3` — what exactly is broker 2 missing, and what brings it back?
- [ ] `acks=all` with `min.insync.replicas=1` on `RF=3`: how many copies before the ack? Best case
      and worst case.
- [ ] A producer got a successful callback and the record no longer exists. Give two distinct ways.
- [ ] Why is `enable.idempotence=true` (the default) enough to make an `acks=1` benchmark invalid?
- [ ] You need zero data loss and you can tolerate 30s of write downtime a year. Which three
      settings, and what happens on the second broker failure?
- [ ] A partition is offline with an empty ISR. What are your two options and what does each cost?

## Notes cross-reference

- `replication-and-isr.html` — ISR mechanics, leader election, unclean elections
- `producers.html` — `acks`, retries, `delivery.timeout.ms`, idempotence
- `kraft-vs-zookeeper.html` — the metadata quorum you watch in experiment E

## Next

Stage 7: delivery semantics. You will have just measured *loss*; stage 7 goes after the other
failure — the **duplicates** you first met in stage 1 experiment C and again in stage 3 problem 1 —
with `enable.idempotence`, transactions, and the point past which Kafka stops helping and your
database's idempotency key takes over.
