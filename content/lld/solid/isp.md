---
title: Interface Segregation Principle
short: ISP
order: 40
desc: Clients should not be forced to depend on interfaces they don't use — prefer small role interfaces.
---

# I — Interface Segregation Principle

<div class="definition"><span class="lbl">Definition</span>
Clients should <strong>not be forced to depend on interfaces they don't use</strong>. Instead of one large interface with many methods, prefer <strong>multiple smaller, focused interfaces</strong>.
</div>

In short: an interface should never force a client to implement functions it does not need. ISP is the [Single Responsibility Principle](srp.html) applied to *contracts* rather than to classes.

## Violation — the fat interface

```java-sample dir="lld/solid/isp/violation" verdict="✗ Violates ISP" variant="bad" title="One interface, five unrelated jobs" files="RestaurantEmployee.java,Waiter.java,ViolationDemo.java" run
```

### What is wrong

- Classes are forced to implement methods they do not support.
- The result is an `AssertionError` at runtime — a compile-time problem deferred to production.
- The class becomes bloated with empty or error-throwing methods.
- It violates the principle that clients shouldn't depend on unused interfaces — and simultaneously breaks **[LSP](lsp.html)**, since a `Waiter` is not substitutable for a `RestaurantEmployee`.
- Every method added to the fat interface forces an edit in *every* implementer — an **[OCP](ocp.html)** violation too.

That last point is worth sitting with: one bad interface breaks three principles at once. Fat interfaces are among the highest-leverage things to fix in a codebase.

## Refactoring — role interfaces

```java-sample dir="lld/solid/isp/refactored" verdict="✓ Follows ISP" variant="good" title="One interface per role" files="ChefTasks.java,WaiterTasks.java,MaintenanceTasks.java,Chef.java,Waiter.java,Manager.java,SolutionDemo.java" run
```

Note `Manager` — roles **compose**. An employee who genuinely performs two jobs implements two interfaces, and only then. Segregating the interface doesn't prevent a class from doing several things; it prevents a class from being *forced* to.

### What it bought

- **No bloated classes** — each class implements only the interfaces it actually uses.
- **No forced dependencies** on irrelevant functionality.
- **Cleaner, more maintainable design** — intent is visible in the type itself.
- **Composable roles** — `class Manager implements ChefTasks, MaintenanceTasks`.
- **Smaller blast radius** — adding `reStockGroceries()` to `MaintenanceTasks` doesn't touch `Chef` or `Waiter`.

## Detection

| Indicator | What it tells you |
| --- | --- |
| **Low cohesion** | The interface's methods have no reason to change together — it's several contracts fused into one. |
| **Large / "fat" interfaces** | More than a handful of methods is a prompt to check whether every implementer needs all of them. |
| **Empty or throwing methods** | The clearest possible signal: an implementer was forced into a contract it cannot honour. |
| **Painful tests** | A mock must stub ten methods so a test can exercise one. |
| **Recompilation ripple** | Adding one method to the interface breaks every implementer in the codebase. |

> **Pitfall** — Java 8 `default` methods are a band-aid, not a fix. They let you add a method to an interface without breaking implementers, and that's genuinely useful for evolving a published API. But using them to give unsupported operations a "do nothing" or "throw" body only *hides* the ISP violation — the client still depends on a method that doesn't apply to it. Segregate the interface instead.

> **Tip** — Interfaces belong to the client. The right interface size is decided by the *consumer*, not the implementer. If a module only ever calls `save()`, give it an interface with exactly `save()`. One class implementing several such role interfaces is normal and good — that's the shape you're aiming for.

> **Warning** — Don't mechanically split one interface per method. Twelve tiny types nobody can name is its own kind of unreadable. Segregate along real **client roles** — the groups of methods that are actually used together.

> **Summary** — ISP keeps classes focused by ensuring they implement only the methods they need. Look for low cohesion, fat interfaces, empty methods and awkward testing to spot violations early, then refactor toward a modular, flexible design.
