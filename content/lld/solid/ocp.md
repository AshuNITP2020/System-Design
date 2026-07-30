---
title: Open/Closed Principle
short: OCP
order: 20
desc: Software should be open for extension but closed for modification — add behaviour without editing tested code.
---

# O — Open/Closed Principle

<div class="definition"><span class="lbl">Definition</span>
A software entity should be <strong>open for extension but closed for modification</strong>.
</div>

New functionality should be added by *extending* the system — through inheritance, composition or interfaces — rather than by editing code that already exists. Existing code is already tested and running in production; modifying it adds risk and forces you to re-test behaviour that was previously known to work.

> **Note** — This chapter picks up the `InvoiceDao` class produced by the [SRP refactoring](srp.html) and shows what happens when a second way of saving is requested.

## Violation

The starting point is fine — one responsibility, one way to save:

```java-sample dir="lld/solid/ocp/violation" title="Where we started, after SRP" files="InvoiceDaoOld.java"
```

Then "also save to a file" arrives, and the existing class gets edited in place:

```java-sample dir="lld/solid/ocp/violation" variant="bad" title="Editing a tested class to add a variant" files="InvoiceDao.java,Demo.java" run
```

### What is wrong

- **Modification required** — every new save target (`saveToMongoDB()`, `saveToS3()`, …) forces an edit to the existing `InvoiceDao`.
- **Risk of breaking working code** — the class is already deployed in production; each edit can introduce a regression in `saveToDB()`.
- **Testing burden** — all existing save operations must be re-tested whenever a new one is added.
- **Ripple effect** — any subclass of `InvoiceDao` must also be revisited.

The class grows one method per requirement, forever, and every one of those requirements is a fresh chance to break the others.

## Refactoring

Extract the varying behaviour into an interface, and make each variant its own class:

```java-sample dir="lld/solid/ocp/refactored" variant="good" title="Interface + polymorphism" files="InvoiceDao.java,DatabaseInvoiceDao.java,FileInvoiceDao.java,Demo.java" run
```

Adding MongoDB support is now a *new file* — `class MongoInvoiceDao implements InvoiceDao` — and not a single existing line changes.

### What it bought

- **Reduced risk** — existing tested code stays unchanged.
- **Better maintainability** — new features cannot break existing functionality.
- **Improved flexibility** — new behaviours are added without touching existing code.
- **Enhanced testability** — each extension is tested independently.
- **Supports polymorphism** — behaviour is selected dynamically through the interface.

## Detection

### The loudest smell: switching on type

Whenever a conditional branches on *what kind of thing* something is, and that conditional grows with each new kind, OCP is being violated:

```java variant="bad" title="Type-switching — the classic OCP smell"
// Every new payment type edits this method - and usually three others like it
public void pay(String type, double amount) {
    if (type.equals("CARD")) {
        // ...
    } else if (type.equals("UPI")) {
        // ...
    } else if (type.equals("WALLET")) {
        // ...
    }
    // ... and again, and again
}
```

The fix has the same shape as the DAO refactoring above: a `PaymentMethod` interface with one implementation per type. That is the **Strategy pattern**, which is OCP in its purest form.

Other indicators:

- A file that shows up in every sprint's diff but whose *purpose* never changes.
- A `switch` over an enum that must be updated in several places whenever the enum grows.
- Subclasses that override a method just to add another `if` to it.

> **Pitfall** — You cannot be open to everything. Abstraction is not free: every interface adds indirection, and an abstraction guessed wrong is worse than no abstraction at all. OCP means choosing the **axis of change** you actually expect — "we will keep adding storage targets" — and being open along *that* axis only. Building extension points for changes that never arrive is *speculative generality*, and it is a genuine code smell.

> **Tip** — Write the concrete version first. The **second** variant is the signal to extract the abstraction, because by then you know the real axis of variation. "Refactor on the second, design for the third."

> **Summary** — OCP produces architectures that are adaptable and resilient, able to evolve without compromising existing functionality. It emphasises abstraction and well-defined interfaces that satisfy today's requirements while anticipating tomorrow's — increasing robustness, longevity and scalability, and reducing the risk of introducing defects during change.
