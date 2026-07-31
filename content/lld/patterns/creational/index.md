---
title: Creational Patterns
short: Intro
desc: Patterns about how objects get created — and why that decision belongs somewhere other than the caller.
---

# Creational Patterns

<p class="lede">Every creational pattern exists to take the <code>new</code> keyword out of code that shouldn't care which concrete class it gets.</p>

The moment a class writes `new PostgresRepository()`, it is welded to that type: you cannot substitute a fake in a test, cannot add a second implementation without editing it, and cannot vary behaviour at runtime. Creational patterns move that decision somewhere it can be changed — which is [Dependency Inversion](../../solid/dip.html) doing its work.

## The six covered here

| Pattern | Use it when | Key mechanism |
| --- | --- | --- |
| [Factory Method](factory.html) | One product, several variants; the caller shouldn't pick the class | A subclass per product, each overriding one creation method |
| [Abstract Factory](abstract-factory.html) | Several products that must come from the same *family* | An interface with one creation method per product type |
| [Builder](builder.html) | One object with many optional parts | Chained setters, then a `build()` |
| [Prototype](prototype.html) | Creation is expensive and you already have a similar object | The object clones itself |
| [Singleton](singleton.html) | Exactly one instance must exist | Private constructor + static accessor |
| [Object Pool](object-pool.html) | Objects are costly and reused constantly | Borrow → use → return, against a fixed-size pool |

Object Pool is not one of the original 23 GoF patterns, but it belongs here: it is a creational concern, and in practice it is everywhere — every JDBC connection pool and thread pool is this pattern.

## How they relate

- **Factory Method → Abstract Factory.** Abstract Factory is Factory Method repeated: several creation methods on one interface, so a whole family is chosen together.
- **Abstract Factory vs. Builder.** Abstract Factory picks a *family* up front and returns products immediately. Builder assembles *one* product over several steps. Family selection vs. step-by-step assembly.
- **Singleton + Object Pool.** A pool is only a real limit if there is exactly one of it — Object Pool almost always contains a Singleton, and the [Object Pool page](object-pool.html) shows precisely what breaks without it.
- **Prototype vs. the rest.** Every other pattern here builds from scratch. Prototype starts from an object that already exists.

> **Tip** — The most common mistake is reaching for Abstract Factory when Factory Method would do. Count the products: **one** product with variants is Factory Method. **Several** products that must be consistent with each other — an economy interior must never end up in a luxury exterior — is Abstract Factory.
