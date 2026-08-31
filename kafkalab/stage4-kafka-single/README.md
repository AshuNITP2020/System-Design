# Stage 4 — Real Kafka

**Plumbing:** a Kafka broker. `Log.java` and `Offsets.java` are deleted, not ported.

You spent stage 3 building an append-only log with per-consumer offsets. This stage throws it away
and uses a real one. The domain, the four consumers and the behaviour are unchanged — only the
provider of the log changes.

---

## Purpose of this stage

There are no walls here. Stage 4 is the **payoff**: everything you struggled to build gets handed
to you, and because you built it yourself first, every config name is the answer to a problem you
personally had.

**You are here to discover:** nothing new about *why* — you already know why. You're here to map
what you wrote onto what Kafka provides, so the vocabulary stops being jargon.

The one genuinely structural change: in stage 3 the log was **a file on your machine**. Here it is
**a server you talk to over TCP**. That is what lets consumers live elsewhere, and what makes
replication possible at all — you cannot copy a local file to another machine and keep it correct.

## What is expected from you

**No TODOs.** The code is written. Your job is to read the diff and run the experiments.

| # | Task |
| - | ---- |
| 1 | Read `OrderService.java` and `Consumer.java` side by side with your stage 3 `Log.java` / `Offsets.java` |
| 2 | Run the demo, watch partitions get chosen by key |
| 3 | Open the console and the terminal viewer while it runs |
| 4 | Answer the questions below |

## The mapping — this is the stage

| Your stage 3 | Stage 4 |
| --- | --- |
| `Log.append()` + 4-byte length prefix + O(n) offset scan + torn-write guard | `producer.send(record)` |
| `Log.readFrom(pos, 10)` + `Thread.sleep(200)` | `consumer.poll(Duration)` — a **long poll** |
| `Offsets.position()` / `Offsets.commit()` | `group.id` + `commitSync()` |
| `data/payment.offset` (4 files) | `__consumer_offsets` (an internal topic) |
| `data/orders.log` (one file) | topic `orders`, **3 partitions** |
| wall #6: split the file by `hash(key) % N` | `--partitions 3` + the key on `ProducerRecord` |

**What did not change:** at-least-once vs at-most-once. It moved to wherever you put
`commitSync()`. Stage 7 is where that gets attacked properly.

## Run

```bash
./kafkalab/stage4-kafka-single/demo.sh
```

Starts the broker if needed, creates the topic, produces 12 keyed orders, runs four consumer
groups, prints real lag from Kafka's CLI, then resets one group and replays it.

Interactively — producer:

```bash
./gradlew :kafkalab:stage4-kafka-single:run
```

one consumer group per terminal:

```bash
./gradlew :kafkalab:stage4-kafka-single:runConsumer -Pgroup=payment
```

Watch it live at <http://localhost:8081>, or:

```bash
./kafkalab/infra/watch.sh
```

## The experiments

### A — the key picks the partition

```
user-1 -> {"partition":2,"offset":0}
user-2 -> {"partition":2,"offset":1}
user-4 -> {"partition":1,"offset":0}
```

Same key always lands on the same partition — `murmur2(key) % partitionCount`. That is what makes
per-key ordering possible, and it's stage 3's wall #6 solved for you.

Try keying by `orderId` instead of `userId` in `OrderService`. Every order gets a unique key, so
they spread evenly — and a single user's orders are no longer ordered relative to each other. That
trade is the whole of partition-key design.

### B — four groups, one topic

```bash
./kafkalab/infra/kcli.sh kafka-consumer-groups.sh --describe --all-groups
```

Every group sits independently at the end of all three partitions. Four values of one config key
replaced your four `.offset` files.

### C — replay, the real way

A group's offsets can only be reset while it has **no active members** — stop the consumer first.

```bash
./kafkalab/infra/kcli.sh kafka-consumer-groups.sh --group analytics --topic orders --reset-offsets --to-earliest --execute
```

### D — `auto.offset.reset`

Change it to `latest` in `Consumer.java`, then start a brand-new group name. It sees nothing that
happened before it started. `earliest` is your stage 3 "a group that never committed starts at 0".

## Done when you can answer

- [ ] Which two of your stage 3 files ceased to exist, and what replaced each?
- [ ] Why does `poll()` dissolve stage 2's "no interval is both cheap and fast" problem?
- [ ] What decides which partition a record lands in?
- [ ] Two consumers with the **same** `group.id` vs **different** ones — what's the difference?
- [ ] Where does Kafka store consumer offsets, and why is that funny?
- [ ] What problem from stage 3 is still completely unsolved here?

**Status: complete.** Verified: 3 partitions with keys routing correctly, four groups each at the
end of all partitions, 12 records replayed after an offset reset.

## Known rough edge

`demo.sh` creates `orders` with `--replication-factor 1`, because stage 4 was written for the
single-broker cluster. If you are running the 3-broker cluster, your order data has **no
redundancy** while Kafka's own `__consumer_offsets` has RF=3. Bumping it to 3 is a one-word change
and is exactly what stage 6 is about.

## Notes cross-reference

- `architecture.html` — brokers, the controller
- `kraft-vs-zookeeper.html` — why there's no ZooKeeper container
- `topics-and-partitions.html` — keys, partitions, ordering
- `consumers-and-groups.html` — `group.id`, offsets, lag

## Next

Stage 5: two consumers in **one** group splitting the partitions, a rebalance watched live, and the
4th consumer that sits idle forever because there are only 3 partitions.
