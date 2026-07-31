---
title: Design Patterns — Overview
short: Intro
desc: The three categories of GoF patterns, what each solves, and how to pick one.
---

# Design Patterns

<p class="lede">Named, reusable solutions to problems that keep recurring in object-oriented design. A pattern is not a library you import — it is a shape your code takes, and its real value is that another engineer recognises the shape on sight.</p>

The canonical catalogue comes from *Design Patterns: Elements of Reusable Object-Oriented Software* (1994) by Gamma, Helm, Johnson and Vlissides — the **Gang of Four**. They grouped 23 patterns into three categories by what the pattern is *about*.

## The three categories

| Category | Concerned with | Question it answers |
| --- | --- | --- |
| **[Creational](creational/index.html)** | Object creation | *How does this object get made?* |
| **[Structural](structural/index.html)** | Object composition | *How do these objects fit together?* |
| **[Behavioral](behavioral/index.html)** | Object interaction | *How do these objects talk and divide responsibility?* |

## What's covered here

| | Pattern | One-line intent |
| --- | --- | --- |
| **C** | [Factory Method](creational/factory.html) | Let a subclass decide which concrete class to instantiate. |
| **C** | [Abstract Factory](creational/abstract-factory.html) | Create whole *families* of related objects that must match. |
| **C** | [Builder](creational/builder.html) | Assemble a complex object step by step. |
| **C** | [Prototype](creational/prototype.html) | Create new objects by cloning an existing one. |
| **C** | [Singleton](creational/singleton.html) | Guarantee exactly one instance, globally reachable. |
| **C** | [Object Pool](creational/object-pool.html) | Reuse a fixed set of expensive objects instead of recreating them. |
| **S** | [Adapter](structural/adapter.html) | Make an incompatible interface fit the one the client expects. |
| **S** | [Bridge](structural/bridge.html) | Split abstraction from implementation so both can vary. |
| **S** | [Composite](structural/composite.html) | Treat individual objects and trees of objects uniformly. |
| **S** | [Decorator](structural/decorator.html) | Add behaviour by wrapping, at runtime, without subclassing. |
| **S** | [Facade](structural/facade.html) | Put one simple door in front of a complicated subsystem. |
| **S** | [Flyweight](structural/flyweight.html) | Share the common part of many similar objects to save memory. |
| **S** | [Proxy](structural/proxy.html) | Stand in for another object to control access to it. |

Behavioral patterns are [not yet written up](behavioral/index.html) — that section lists what's coming.

## Patterns are SOLID, applied

Nearly every pattern here is a principle from [SOLID](../solid/index.html) crystallised into a specific shape. Reading them that way is far more useful than memorising UML:

| Pattern | The principle it is enforcing |
| --- | --- |
| Factory Method, Abstract Factory | **DIP** — the client depends on a product interface, never on `new ConcreteThing()` |
| Decorator, Bridge | **OCP** — new behaviour arrives as a new class, not an edit |
| Composite | **LSP** — leaf and composite are genuinely substitutable, so the client needs no `instanceof` |
| Facade | **ISP** — the client gets a small surface instead of the whole subsystem |
| Adapter | **DIP + LSP** — a foreign type is made to satisfy your abstraction honestly |
| Builder | **SRP** — construction logic moves out of the product class |

## Choosing one

Most confusion between patterns is about **intent**, not structure — several look nearly identical in a class diagram. The distinctions that actually come up:

| If you're deciding between | The question that separates them |
| --- | --- |
| Factory Method vs. Abstract Factory | One product with variants, or a *family* of products that must match? |
| Builder vs. Abstract Factory | Building one complex object step by step, or picking a family up front? |
| Builder vs. Decorator | Assembling an object once, or layering behaviour onto a finished one? |
| Adapter vs. Facade | Fixing an *incompatible* interface, or simplifying a *complicated* one? |
| Facade vs. Proxy | Fronting *many* objects for convenience, or *one* object to control access? |
| Proxy vs. Decorator | Controlling access to the same behaviour, or adding new behaviour? |
| Bridge vs. Strategy | Two hierarchies evolving independently, or swapping an algorithm at runtime? |
| Composite vs. Decorator | A tree of many children, or a chain wrapping exactly one? |

Each pattern page ends with the comparison that most often trips people up on that specific pattern.

> **Warning** — A pattern applied because it was recognised, rather than because the problem demanded it, is a liability. Every one of these adds indirection: more types, more files, more hops to follow one call. The right time to reach for a pattern is when you already feel the pain it removes — the growing `switch`, the constructor with nine arguments, the class that will not stay closed. Not before.

## How the code works

Every pattern page has editable Java, and every sample compiles and runs:

```bash
./run.sh --list                                          # everything available
./run.sh lld/patterns/structural/decorator               # run one
./run.sh lld/patterns/creational/builder/problem         # the "before" version
```

Patterns with a *problem* and a *solution* directory show the naive version first — the class explosion, the telescoping constructor, the `instanceof` chain — because the pain is the argument for the pattern.
