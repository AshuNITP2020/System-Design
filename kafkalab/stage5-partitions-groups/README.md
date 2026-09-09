# Stage 5 — Partitions and consumer groups

**Plumbing:** the same Kafka topic, but now with *several consumers in one group*.

Stage 4 ran exactly one consumer per group, so you never found out what a group is actually for.
This is the stage you **watch** rather than read.

---

## Purpose of this stage

Two independent knobs get confused constantly: **partitions** (how the topic is split) and **group
members** (how many processes share the reading). This stage makes the relationship between them
visible, and it is a hard one:

**A partition is owned by exactly one member of a group. So partition count is the ceiling on
consumer parallelism.** Three partitions means at most three members do work, no matter how many
you start.

**You are here to discover:** that the 4th member of a group on a 3-partition topic gets *nothing*
and stays idle forever — and that this is not waste, because it takes over the moment another
member dies.

## What is expected from you

Two small TODOs. Everything else is copied from stage 4.

| # | Where | Size |
| - | ----- | ---- |
| 1 | `OrderService.partitionKey` — a `switch` returning the key | ~6 lines |
| 2 | `GroupMember` — subscribe with a `ConsumerRebalanceListener` | ~10 lines |

TODO(2) is the only new API in the stage. The real work is running the experiments and reading
the output.

## API notes — everything you need

There are **no annotations**. `@KafkaListener` belongs to Spring Kafka, a different library; plain
`kafka-clients` is all method calls. Nothing is hidden.

**Subscribing with a listener** — the two-argument form of `subscribe`:

```java
consumer.subscribe(List.of(TOPIC), new ConsumerRebalanceListener() {
    @Override public void onPartitionsRevoked(Collection<TopicPartition> parts) { }
    @Override public void onPartitionsAssigned(Collection<TopicPartition> parts) { }
});
```

`@Override` is a plain Java annotation, not a Kafka one — it just tells the compiler you meant to
implement the interface method.

| Thing | What it is |
| --- | --- |
| `ConsumerRebalanceListener` | an interface with exactly two methods, both shown above |
| `onPartitionsRevoked` | called **before** a new assignment. Your partitions are being taken away |
| `onPartitionsAssigned` | called **after**. `parts` is what you now own — **possibly empty** |
| `TopicPartition` | a (topic, partition) pair. `.partition()` → `int`, `.topic()` → `String` |
| `fmt(parts)` | provided in `GroupMember` — renders `p0 p1 p2`, or `(nothing)` |

**On the producer side**, the key is the only thing that controls placement:

```java
new ProducerRecord<>(topic, key, value)   // key may be null
```

Kafka computes `murmur2(keyBytes) % partitionCount` internally. You never call it.

## Run

```bash
./kafkalab/stage5-partitions-groups/demo.sh
```

Adds members to one group, one at a time, printing the assignment after each change. Then kills
one so you can watch the partitions move.

By hand — producer in one terminal:

```bash
./gradlew :kafkalab:stage5-partitions-groups:run -Pkey=user
```

and a member per terminal, same group, different id:

```bash
./gradlew :kafkalab:stage5-partitions-groups:runMember -Pgroup=payment -Pid=A
```

## The experiments

### A — one member, then two, then three

Start members one at a time against a 3-partition topic and read the listener output.

One member owns `p0 p1 p2`. Add a second and the first is **REVOKED everything** before being
given back a subset — that full stop is what people mean by a "stop-the-world" rebalance. Three
members means one partition each: maximum useful parallelism.

### B — the fourth member

Add a fourth. It gets `(nothing)` and will never get anything while the other three live.

Now kill one of the working members. The idle one picks up the freed partition within seconds. It
was a **hot spare**, not waste.

### C — the key decides everything

Run the producer four ways and compare where 20 orders land:

| `-Pkey=` | Ordering you get | Spread |
| --- | --- | --- |
| `user` | per user | uneven — `murmur2 % 3` is lumpy at small N |
| `order` | none (every key unique) | most even |
| `fixed` | **total, across the whole topic** | **all on one partition** |
| `none` | none | Kafka's sticky partitioner batches, then moves |

`fixed` is the one to actually run. Perfect ordering, and two thirds of your consumers idle. That
is the trade, and it has no clever solution: **ordering and spread pull against each other.**

### D — more partitions than you need

Recreate the topic with 1 partition and run the demo again (`PARTS=1`). Now only one member can
ever work. Then try 6. Note you can *add* partitions to a topic later but never remove them — and
adding them changes which partition a key maps to, which breaks the ordering guarantee for
existing keys.

## Done when you can answer

- [ ] Two consumers with the **same** `group.id` versus **different** ones — what's the difference?
- [ ] Why does the 4th member get nothing? What would you change to give it work?
- [ ] What is revoked from whom during a rebalance, and why does everyone stop?
- [ ] You need strict ordering for a given customer. Which key, and what does it cost you?
- [ ] Consumer lag is growing. When does adding partitions help, and when does it not?

**Status: implemented.** Both TODOs closed — `partitionKey` and the rebalance listener.

## Notes cross-reference

- `topics-and-partitions.html` — keys, partitions, per-partition ordering
- `consumers-and-groups.html` — `group.id`, assignment
- `rebalancing.html` — the protocol you're about to watch

## Next

Stage 6: replication. Three brokers, `RF=3`, and killing one to watch the ISR shrink from `1,2,3`
to `1,3` while the data survives. The infra is already built — `./kafkalab/infra/up.sh cluster`.
