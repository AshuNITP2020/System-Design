---
title: High Level Design
short: HLD
order: 0
desc: Placeholder for high-level / system design notes.
---

# High Level Design

<p class="lede">Nothing here yet — this section is scaffolded and ready for the first topic.</p>

## Planned

- Scalability fundamentals — vertical vs. horizontal, load balancing, statelessness
- Caching — where to cache, eviction policies, invalidation, cache stampede
- Databases — SQL vs. NoSQL, indexing, replication, sharding, partitioning
- Consistency — CAP, PACELC, eventual consistency, consensus
- Messaging — queues vs. logs, delivery guarantees, back-pressure, idempotency
- Communication — REST, gRPC, GraphQL, WebSockets, long polling
- Rate limiting, idempotency keys, retries and circuit breakers
- Observability — metrics, logs, traces, SLOs
- Case studies — URL shortener, rate limiter, news feed, chat, notification service

## Adding the first topic

```bash
mkdir -p content/hld/fundamentals
echo '{ "label": "Fundamentals", "order": 10 }' > content/hld/fundamentals/_group.json
$EDITOR content/hld/fundamentals/caching.md
npm run build
```

HLD notes are mostly prose and diagrams rather than runnable code, so `java-sample` blocks won't come up much here. Plain fenced blocks still work for config, protocol snippets and back-of-the-envelope maths:

````text
```text title="Back of the envelope"
1M DAU × 10 reads/day = 10M reads/day ≈ 116 reads/sec avg, ~350/sec peak
```
````

For diagrams, keep them as SVG or PNG in `assets/` and reference them with normal markdown image syntax.
