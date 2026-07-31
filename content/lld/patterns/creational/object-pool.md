---
title: Object Pool
short: Obj. Pool
order: 60
desc: Borrow, use, return — reuse expensive objects instead of creating and destroying them.
---

# Object Pool

<div class="definition"><span class="lbl">Definition</span>
The Object Pool Pattern manages a <strong>set (pool) of reusable objects</strong>. Instead of creating and destroying instances repeatedly, clients borrow a pre-created one from the pool and return it when finished.
</div>

> **Key idea** — Borrow an instance from the pool ➡ use it ➡ return it to the pool.

Reach for it when:

- Object creation is **expensive** in CPU or memory — a database connection means a TCP handshake plus authentication.
- The **same type** of object is needed over and over.
- You need to **cap** how many exist — a database will refuse connection 101.

Not a GoF pattern, but ubiquitous: HikariCP, JDBC connection pools, thread pools and buffer pools are all this.

## The problem

A perfectly reasonable-looking pool that is not a pool at all:

```java-sample dir="lld/patterns/creational/object-pool/problem" variant="bad" title="A pool anyone can duplicate" files="DBConnection.java,DBConnectionPoolManager.java,Client.java" run
```

Run it and watch the last few lines. `MAX_POOL_SIZE` is 6, the seventh borrow is correctly refused — and then a second client writes `new DBConnectionPoolManager()` and the ceiling evaporates.

**What goes wrong:**

- **The limit isn't a limit.** Two pools of 6 means up to 12 connections against a database configured for 6.
- **Duplicate bookkeeping.** Each pool has its own `freeConnections` / `inUseConnections`. Neither knows about the other's.
- **Memory leak.** Connections tracked by an orphaned pool are never reclaimed and never reused.
- **Unreliable by construction.** Correctness depends on every caller *remembering* not to instantiate it.

## The solution: Object Pool + Singleton

A pool is only a real limit if there is exactly one of it. This pattern is used **in conjunction with [Singleton](singleton.html)**, and it needs **thread safety** because callers race to acquire and release.

```java-sample dir="lld/patterns/creational/object-pool/solution" variant="good" title="Singleton pool, synchronized borrow and return" files="DBConnection.java,DBConnectionPoolManager.java,Client.java" run
```

Three changes, all load-bearing:

1. **Private constructor + `getInstance()`** — there is now exactly one pool, so `MAX_POOL_SIZE` means what it says.
2. **`volatile` on the instance field** — double-checked locking is only correct with it; see the [Singleton page](singleton.html#the-bug-in-double-checked-locking) for what breaks otherwise.
3. **`synchronized` on borrow and return** — without it, two threads can pass the `freeConnections.isEmpty()` check together and both take the same connection, or corrupt the lists outright.

## Participants

| Role | In this example | Responsibility |
| --- | --- | --- |
| **Object Pool** | `DBConnectionPoolManager` | Tracks free and in-use objects; enforces the ceiling |
| **Reusable Object** | `DBConnection` | The pooled resource |
| **Client** | `Client` | Borrows, uses, and — critically — returns |

## Advantages

- Removes the overhead of repeatedly creating and destroying resource-intensive objects.
- **Reduces latency** — a pre-initialised object is handed over immediately.
- **Prevents resource exhaustion** by capping how many can exist.

## Disadvantages

- **Resource leaks** if an object is never returned. This is the failure mode in practice: one code path that forgets to release, and the pool drains until every borrow blocks.
- **More memory** — the pool holds objects even while idle.
- **Thread safety is mandatory**, and that synchronisation is itself overhead.
- **Complexity** — pool sizing, eviction, health-checking and timeouts are all now your problem.

> **Warning** — The sample returns `null` when the pool is exhausted, which is the clearest way to show the ceiling. Production pools **block with a timeout** instead — `acquire(5, SECONDS)` — so a brief spike waits rather than failing. And borrowed objects must be returned in a `finally` block, or better, wrapped in try-with-resources so the language enforces it:
> ```java
> try (PooledConnection c = pool.borrow()) {   // AutoCloseable returns it
>     c.query("...");
> }
> ```

> **Tip** — Also worth handling in a real pool: **validate on borrow** (a connection idle for an hour may be dead), **reset state on return** (never hand the next caller an object carrying the last one's data), and **evict idle objects** so a quiet system doesn't hold resources it isn't using.

> **Summary** — Object Pool trades memory and complexity for latency and a hard resource ceiling. It only works as a Singleton — otherwise the limit is advisory — and it only works if every borrow is matched by a return, which is why the language-enforced version is worth the extra type.
