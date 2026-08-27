# kafka-from-scratch

Learning Kafka by **earning** it — build the same system nine times, each time hitting the wall
that the next stage exists to fix. By stage 3 you will have accidentally written a bad Kafka.
By stage 4 you will know exactly which of your problems each real Kafka feature solves.

Companion theory: `~/Documents/lld-hld-notes/site/hld/kafka/index.html`

## The one domain, all nine stages

A shop places an order. Four downstream things must happen:

| Consumer    | Simulated cost | Why it's here |
| ----------- | -------------- | ------------- |
| `payment`   | 120 ms         | must not be lost, must not run twice |
| `inventory` | 80 ms          | must not be lost, order matters per SKU |
| `email`     | 200 ms, flaky  | slow and allowed to fail |
| `analytics` | 20 ms          | cares about *history*, wants to replay |

Every stage delivers the same four side effects. Only the *plumbing between* the producer and
the consumers changes. That is the whole point: you are studying the pipe, not the payload.

## The path

| # | Stage | Plumbing | The wall you hit at the end |
| - | ----- | -------- | --------------------------- |
| 0 | `stage0-monolith` | direct method calls | latency adds up; one failure kills the order; new consumer = edit producer |
| 1 | `stage1-http-fanout` | producer POSTs to 4 services | consumer down = event gone forever; retries create duplicates; N×M wiring |
| 2 | `stage2-db-queue` | SQLite table as a queue | consuming deletes → no replay; 2 workers double-process; per-consumer state explodes |
| 3 | `stage3-my-own-log` | **you write an append-only log with offsets** | single writer, torn writes, one machine, offset-commit races |
| 4 | `stage4-kafka-single` | real Kafka, 1 broker, KRaft | one broker is still one machine |
| 5 | `stage5-partitions-groups` | keys, partitions, consumer groups | rebalance storms, hot partitions, ordering vs parallelism |
| 6 | `stage6-replication` | 3 brokers, ISR, `acks`, `min.insync.replicas` | durability vs availability is a *choice* you configure |
| 7 | `stage7-delivery-semantics` | idempotent producer, transactions | exactly-once has a boundary — find it |
| 8 | `stage8-retention-compaction` | retention, compaction, consumer lag | operating it is its own skill |

Stages 0–3 are the important ones. **Do not skip to stage 4.** The value of this repo is that
you feel each problem before you're handed the solution.

Only the stages you've reached exist as modules. Ask for the next one when you get there.

## Run it

Gradle subprojects of the repo root — run `./gradlew` from `System Design/`, not from here.
No install step: `project(':kafkalab:common')` is a project dependency, so a change to `common/`
is picked up on the next build automatically.

```bash
./gradlew build
```

Each stage has its own `README.md` with: the goal, what to build, the **experiment to run**,
and the questions you should be able to answer before moving on. Start here:

    stage0-monolith/README.md

## Seeing the cluster live

Two viewers, both in `infra/`, both written for you — a viewer is an instrument, not an exercise.

```bash
./kafkalab/infra/up.sh
```

- **web console** → <http://localhost:8081> — topics, partitions, message bytes, keys, group
  membership, lag
- **terminal view** → `./kafkalab/infra/watch.sh` — brokers, KRaft quorum, and a live
  `LEADER / REPLICAS / ISR` table that flags under-replicated partitions
- **cli** → `./kafkalab/infra/kcli.sh kafka-topics.sh --describe --topic orders`

Stages 0–3 have no broker in them, so there is nothing to look at until stage 4. But
`infra/README.md` ends with a five-minute **orientation exercise** on an empty cluster — create a
topic, produce two records with the same key, watch them land in the same partition. Safe to do
now; it doesn't spoil any stage's wall.

## Layout

`kafkalab/` is a set of Gradle subprojects inside the `System_LLD` root build. Every command in
these READMEs is run from the **repo root** (`System Design/`), where `gradlew` lives.

    build.gradle    Java 21 toolchain + shared config for every stage below
    common/       domain record, JSON, simulated downstream services, latency metrics  (written for you)
    infra/        docker-compose clusters + the two live viewers                       (written for you)
    stage0..N/    one subproject per stage  (skeletons with TODOs — you fill these in)
    scripts/      send-order.sh, burst.sh
    docs/STAGES.md  full stage-by-stage spec: objectives, experiments, exit questions

Each stage is its own subproject with its own dependencies on purpose: stage 4's `kafka-clients`
must not be on stage 0's classpath, or the class-by-class diff between stages stops meaning
anything. Register the next stage in the root `settings.gradle` when you reach it.
