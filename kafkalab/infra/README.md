# infra — the instrument panel

Two ways to watch the cluster live, plus the CLI. **This directory is tooling, not an exercise** —
it's written for you, like `common/`. Nothing here has TODOs.

## Start / stop

```bash
./kafkalab/infra/up.sh
```

| Command | What you get |
| ------- | ------------ |
| `./kafkalab/infra/up.sh` | 1 broker, KRaft, no ZooKeeper — stages 4, 5, 7, 8 |
| `./kafkalab/infra/up.sh cluster` | 3 brokers, RF=3 capable — stage 6 |
| `./kafkalab/infra/down.sh` | stop, keep data |
| `./kafkalab/infra/down.sh --wipe` | stop, delete every topic/offset/segment |

Only one mode runs at a time — they share container names and host ports, and `up.sh` tears the
other one down for you. Your Java clients always use `bootstrap.servers=localhost:9092`.

## Viewer 1 — the web console

<http://localhost:8081> (8081, because your own `order-service` owns 8080)

Best for: browsing actual message bytes, seeing keys, hand-creating topics, watching group
membership shift during a rebalance. Go to **Topics → orders → Partitions** for the leader/ISR
table, and **Consumers** for live lag.

## Viewer 2 — the terminal view

```bash
./kafkalab/infra/watch.sh
```

Refreshes every 2s: live brokers, the KRaft quorum with per-node metadata lag, every partition's
`LEADER / REPLICAS / ISR` with an under-replicated flag, and consumer-group lag.

Keep this in a split next to your app. It's the better of the two viewers for the thing you most
need to see, because when you run `docker stop kafkalab-kafka2` in another terminal you watch the
ISR column physically go from `1,2,3` to `1,3` and a leader jump to another broker.

**On speed:** each Kafka CLI call starts a JVM inside the container and costs ~2.5s, so a refresh
lands around 5–6s no matter what `INTERVAL` you set. The script makes only two calls per cycle
(all topics in one, all groups in one) and caches the quorum for 5 cycles to keep it that low. The
footer prints the measured time, so you're never guessing whether you're looking at stale data.

### The failover walkthrough (verified — this is the real output)

With `./kafkalab/infra/up.sh cluster` and an RF=3 topic, `watch.sh` shows healthy state, then run
`docker stop kafkalab-kafka2` in another terminal. Within ~10s:

```
-- brokers (live) ---------------------------------
   kafkalab-kafka1  Up About a minute  9092->9092/tcp
   kafkalab-kafka3  Up About a minute  9094->9094/tcp     <- kafka2 gone

-- KRaft metadata quorum --------------------------
   node 1    logEndOffset 229   lag 0     Leader
   node 2    logEndOffset 167   lag 62    Follower        <- dead node falling behind
   node 3    logEndOffset 229   lag 0     Follower

-- partitions -------------------------------------
   TOPIC     P   LEADER   REPLICAS   ISR     HEALTH
   orders    0   3        3,1,2      3,1     UNDER-REPLICATED 2/3
   orders    1   1        1,2,3      1,3     UNDER-REPLICATED 2/3
   orders    2   3        2,3,1      3,1     UNDER-REPLICATED 2/3   <- leader was 2, now 3
```

Three things to notice, and the third is the one people miss:

1. **`REPLICAS` never changes, `ISR` does.** Replicas is the *assignment* — a static decision made
   at topic-creation time. ISR is the *live* subset currently caught up enough to be trusted. That
   distinction is the whole of `replication-and-isr.html` in two columns.
2. **Partition 2's leader moved from broker 2 to broker 3** with no action from you. That's
   automatic failover, and it's why `acks=all` writes survive a broker loss.
3. Now `docker start kafkalab-kafka2` and wait ~20s. The ISR refills to all three — but **the
   leaders do not move back.** Partition 2 stays led by broker 3. Kafka doesn't rebalance
   leadership just because a broker returned, so after a few incidents your leaders pile onto
   whichever brokers happened to survive, and that broker does all the work. Fixing it is a
   deliberate act:
   ```bash
   ./kafkalab/infra/kcli.sh kafka-leader-election.sh --election-type PREFERRED --all-topic-partitions
   ```
   Leader skew after a rolling restart is one of the most common real-world Kafka operational
   surprises, and you just caused and fixed it on purpose.

## The CLI

`kcli.sh` runs any Kafka tool inside the broker container, so you never install Kafka locally.
`--bootstrap-server` is added for you.

```bash
./kafkalab/infra/kcli.sh kafka-topics.sh --create --topic orders --partitions 3 --replication-factor 1
./kafkalab/infra/kcli.sh kafka-topics.sh --describe --topic orders
./kafkalab/infra/kcli.sh kafka-consumer-groups.sh --describe --all-groups
./kafkalab/infra/kcli.sh kafka-console-consumer.sh --topic orders --from-beginning --property print.key=true
```

Run it with no arguments to list every available tool.

## Deliberate config choices

Three settings in the compose files are pedagogical, not defaults you'd copy to production:

| Setting | Value | Why |
| ------- | ----- | --- |
| `auto.create.topics.enable` | `false` | forces you to state a partition count out loud, every time |
| `group.initial.rebalance.delay.ms` | `0` | rebalances fire instantly, so stage 5 is observable |
| `unclean.leader.election.enable` | `false` | stage 6 flips this on purpose to watch committed data vanish |

## Orientation exercise — do this now, before stage 1

You're at stage 0, so there is nothing of yours in here yet. But five minutes of poking at an
empty cluster makes stage 4 much less abstract. This teaches you nothing about *why* Kafka is
needed — that's what stages 0–3 are for — so it doesn't spoil anything.

```bash
./kafkalab/infra/up.sh
./kafkalab/infra/kcli.sh kafka-topics.sh --create --topic orders --partitions 3 --replication-factor 1
./kafkalab/infra/kcli.sh kafka-topics.sh --describe --topic orders
```

Open <http://localhost:8081> and find your three partitions. Then:

1. Produce a few records **with keys** and see which partition each lands in:
   ```bash
   ./kafkalab/infra/kcli.sh kafka-console-producer.sh --topic orders \
     --property parse.key=true --property key.separator=:
   ```
   Type these four, then Ctrl-D:
   ```
   user-1:first
   user-4:hello
   user-8:hi
   user-1:second
   ```
   Read them back showing key and partition:
   ```bash
   ./kafkalab/infra/kcli.sh kafka-console-consumer.sh --topic orders --from-beginning \
     --timeout-ms 8000 --property print.key=true --property print.partition=true
   ```
   You get `user-8`→P0, `user-4`→P1, and **both `user-1` records on P2**. Same key, same
   partition, always — that is the entire mechanism behind "ordering is guaranteed per partition".

   Those three keys are chosen deliberately, because most nearby ones don't split: `user-1`,
   `user-2` and `user-3` all hash to partition 2 on a 3-partition topic. Try them and see. That's
   not a bug, it's `murmur2(key) % partitions` being lumpy at small N — and it's a preview of the
   hot-partition problem you'll deliberately cause in stage 5.
2. Read them back twice, as two different groups:
   ```bash
   ./kafkalab/infra/kcli.sh kafka-console-consumer.sh --topic orders --from-beginning --group g1
   ./kafkalab/infra/kcli.sh kafka-console-consumer.sh --topic orders --from-beginning --group g2
   ```
   Both see everything. Reading did not consume. Then re-run `g1` — it sees nothing new, because
   it committed its offsets. Two independent readers, one copy of the data.
3. `./kafkalab/infra/kcli.sh kafka-topics.sh --describe --topic __consumer_offsets` — the offsets you just
   committed are stored in a Kafka topic. The log stores its own bookkeeping in a log.
4. Try `--replication-factor 3` on the single-broker cluster. It refuses. You cannot have three
   copies on one machine — obvious when stated, and it's exactly the wall stage 3 walks you into.

Then close it, run `./kafkalab/infra/down.sh`, and go back to stage 0. **Seeing the finished machine is not
the same as knowing why each part is there** — that's still stages 1 through 3.

## Notes cross-reference

- one container doing broker+controller, no ZooKeeper → `kraft-vs-zookeeper.html`
- the LEADER/REPLICAS/ISR table → `replication-and-isr.html`
- same key → same partition → `topics-and-partitions.html`
- group lag → `consumers-and-groups.html`, `operations-and-tuning.html`
