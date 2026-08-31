# 🏗️ System Design Learning

Notes, diagrams, and — mostly — runnable code, as I work through how large-scale systems are
actually built.

---

## 🚀 Start here: **[kafkalab](kafkalab/)** — learning Kafka by rebuilding it

The main thing in this repo. Instead of reading about Kafka, I built the same small system **six
times**, in the order the industry built them, and measured where each one broke.

One order comes in. Four things must happen: charge the card (120ms), reserve stock (80ms), send an
email (200ms, fails 25% of the time), record analytics (20ms). The business logic never changes.
**Only the pipe between producer and consumers changes.**

| # | Stage | What it is | What breaks |
| - | ----- | ---------- | ----------- |
| 0 | [monolith](kafkalab/stage0-monolith/) | four method calls in a loop | 422ms latency; 7 of 20 orders returned HTTP 500 **after the card was charged** |
| 1 | [HTTP fan-out](kafkalab/stage1-http-fanout/) | producer POSTs to four services | killed one consumer, producer said 200, event gone forever |
| 2 | [DB queue](kafkalab/stage2-db-queue/) | a table, workers poll it | producer 12× faster, events durable — but 4 consumers × 12 orders gave **36 of an expected 48** |
| 3 | [your own log](kafkalab/stage3-my-own-log/) | append-only file + one offset per consumer | **48 of 48, every run.** Replay works. ~40 lines |
| 4 | [real Kafka](kafkalab/stage4-kafka-single/) | KRaft broker, 3 partitions | `Log.java` and `Offsets.java` delete entirely |
| 5–8 | — | partitions, replication, exactly-once, retention | not built yet |

Every stage has a `demo.sh` that starts everything, runs the experiment, prints the numbers, and
tears itself down:

```bash
./kafkalab/stage3-my-own-log/demo.sh
```

**Where to look:**

- **[kafkalab/README.md](kafkalab/README.md)** — the full stage map and how to run things
- **[kafkalab/docs/STAGES.md](kafkalab/docs/STAGES.md)** — what each stage teaches, and the wall it ends on
- **[kafkalab/infra/](kafkalab/infra/)** — docker-compose Kafka clusters plus two live viewers (a web console and a terminal `LEADER / REPLICAS / ISR` table)

Each stage's README states its **purpose**, **what's expected of you**, and the **questions you
should be able to answer** before moving on.

---

## 📁 Repository Structure

```
kafkalab/            Learning Kafka by rebuilding it  ← the main event
  common/              shared domain: the order event, the four simulated consumers
  stage0..stage4/      one module per attempt, each with a README and demo.sh
  infra/               docker-compose clusters + live viewers
  docs/                stage map

src/                 LLD practice (Spring Boot): splitWise, tictactoe
```

Gradle multi-project. The `kafkalab` subprojects deliberately do **not** inherit the root's Spring
Boot starters — each stage's classpath is part of what that stage is teaching.

```bash
./gradlew build
```

---

## 📚 Topics Covered

- Basics of System Design
- Scalability, Availability, and Reliability
- Load Balancing and Caching
- Databases and Storage (SQL vs NoSQL)
- CAP Theorem and Consistency Models
- Asynchronous Processing (Message Queues, Pub/Sub) — **see [kafkalab](kafkalab/)**
- Microservices and Monoliths
- Design Patterns and Principles
- API Design (REST & GraphQL)
