---
title: Singleton
short: Singleton
order: 50
desc: Exactly one instance — and the four ways to get there, including the double-checked locking bug.
---

# Singleton

<div class="definition"><span class="lbl">Definition</span>
The Singleton Pattern is used when <strong>only ONE instance</strong> of a class may exist. The class guarantees this itself, however many clients ask for it.
</div>

Useful when exactly one object must coordinate actions across a system — a connection pool, a configuration registry, a cache.

Two mechanics are common to every version below:

- **A private constructor** — nothing outside the class can call `new`.
- **A static accessor** — `getInstance()` is the only way in.

## The four implementations

```java-sample dir="lld/patterns/creational/singleton" title="All four, side by side" files="DBConnectionEager.java,DBConnectionLazy.java,DBConnectionThreadSafe.java,DBConnectionDoubleLocking.java,SingletonDemo.java" run
```

| | Approach | Thread-safe | Cost | Verdict |
| --- | --- | --- | --- | --- |
| **1** | **Eager** — created at class load | Yes (the JVM guarantees it) | Instance exists even if never used | Fine when the object is cheap and certainly needed |
| **2** | **Lazy** — created on first call | **No** | None | Broken under concurrency |
| **3** | **Synchronized** — `synchronized getInstance()` | Yes | Every call takes a lock, forever | Correct but wasteful |
| **4** | **Double-checked locking** | Yes — *only with `volatile`* | Lock taken once | The industry default |

**Eager** is simplest: `static final` guarantees one instance, assigned once, and class initialisation is thread-safe by definition. The only objection is that it's built whether or not anyone uses it.

**Lazy** defers the cost, but two threads can both pass the `if (instance == null)` check and each construct an object. You now have two "singletons".

**Synchronized** fixes that with a lock on the whole method — but the race only exists on the *first* call. Every call after that pays for a lock that can no longer be contended. With 100 threads calling `getInstance()`, 99 queue behind the first for no reason.

**Double-checked locking** synchronizes only when the instance is still null. After creation, calls take the fast path and never enter the lock. This is the version you'll see in production code — and as written above, it is still subtly broken.

## The bug in double-checked locking

The problem hides in a single line:

```java variant="bad" title="One statement, three machine-level steps"
connectionObj = new DBConnectionDoubleCheckedLockIssue(5567);
```

That is not one operation. It is three:

1. **Allocate** memory for the object
2. **Initialise** it — run the constructor, set `portNumber`
3. **Assign** the reference to `connectionObj`

```java-sample dir="lld/patterns/creational/singleton" variant="bad" title="Double-checked locking, still broken" files="DBConnectionDoubleCheckedLockIssue.java"
```

### Issue 1 — instruction reordering

The JVM is permitted to reorder steps 2 and 3 for performance. If it assigns the reference **before** running the constructor, then:

- **T1** is inside the synchronized block, has assigned `connectionObj`, but has not yet set `portNumber`.
- **T2** hits the outer `if (connectionObj == null)`. It is **not** null — so T2 skips the lock entirely and returns the object.
- T2 now uses a **partially constructed** object whose `portNumber` is still `0`.

Nothing throws. The object simply has wrong data, in a window a few nanoseconds wide, on some runs, on some hardware.

### Issue 2 — CPU caching

On a multicore CPU each core has its own L1 cache, and cores do not synchronise them instantly. T1 builds the instance and writes it to *its* cache. T2, running on another core, reads a stale `null`, enters the block, and builds a **second** instance. Two singletons again — the exact bug the lock was supposed to prevent.

## The fix: `volatile`

```java-sample dir="lld/patterns/creational/singleton" variant="good" title="The corrected version" files="DBConnectionDoubleCheckedLockFix.java"
```

One keyword closes both holes:

| `volatile` guarantee | What it does | Fixes |
| --- | --- | --- |
| **Memory visibility** | Reads and writes go straight to main memory; a write is immediately visible to every thread | Issue 2 (caching) |
| **Ordering** | Establishes a *happens-before* edge — a memory barrier the compiler and CPU may not reorder across | Issue 1 (reordering) |

The reference cannot be published before the object is fully built, and once it is published every thread sees it.

> **Tip** — In Java there is a way to get lazy initialisation, thread safety and zero locking with no `volatile` at all — the **initialisation-on-demand holder**. A private static nested class is not loaded until first referenced, and class initialisation is already thread-safe by JVM guarantee:
> ```java
> public class DBConnection {
>     private DBConnection() {}
>     private static class Holder { static final DBConnection INSTANCE = new DBConnection(); }
>     public static DBConnection getInstance() { return Holder.INSTANCE; }
> }
> ```
> `Effective Java` recommends a single-element `enum` as the simplest correct singleton, since it also defends against reflection and serialisation attacks. Double-checked locking remains worth knowing because it is asked about constantly and because it teaches the memory model.

> **Warning** — Singleton is the most overused pattern in the catalogue, and many engineers consider it an anti-pattern. It is global mutable state: it hides dependencies (nothing in a class's signature reveals it uses one), makes unit testing hard (you cannot substitute a fake, and state leaks between tests), and creates contention. Prefer having a DI container manage a single instance — you get "one instance" *and* the ability to inject a different one in a test. Use a hand-rolled Singleton when you genuinely control a unique resource, as [Object Pool](object-pool.html) does.

> **Summary** — Four implementations, each fixing the previous one's flaw: eager wastes, lazy races, synchronized is slow, double-checked locking is fast but needs `volatile` to be correct. The `volatile` requirement is not a detail — without it the pattern fails in two distinct ways, both intermittent and both nearly impossible to reproduce.
