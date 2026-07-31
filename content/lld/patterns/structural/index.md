---
title: Structural Patterns
short: Intro
desc: Patterns about how objects are composed — wrapping, bridging, and building trees.
---

# Structural Patterns

<p class="lede">Creational patterns decide how objects come into existence. Structural patterns decide how they are wired together once they do.</p>

Almost all of them work by **composition** — one object holding a reference to another and delegating. What separates them is *why* the wrapper exists.

## The seven

| Pattern | Intent | Wraps |
| --- | --- | --- |
| [Adapter](adapter.html) | Make an incompatible interface fit the one the client expects | One object, **changing** its interface |
| [Bridge](bridge.html) | Split an abstraction from its implementation so both can vary | One hierarchy referencing another |
| [Composite](composite.html) | Treat individual objects and trees of objects uniformly | **Many** children, recursively |
| [Decorator](decorator.html) | Add behaviour at runtime without subclassing | One object, **adding** to it |
| [Facade](facade.html) | Provide one simple entry point to a complex subsystem | **Many** objects, simplifying |
| [Flyweight](flyweight.html) | Share common state across many similar objects to save memory | Nothing — it *splits* state instead |
| [Proxy](proxy.html) | Stand in for another object to control access to it | One object, **same** interface |

## The four that look alike

Adapter, Decorator, Proxy and Facade all put an object in front of another object. Told apart by intent:

| Pattern | Interface it exposes | Why it exists | How many objects behind it |
| --- | --- | --- | --- |
| **Adapter** | A *different* one | The existing interface is incompatible | One |
| **Decorator** | The *same* one | To add behaviour | One (but chainable) |
| **Proxy** | The *same* one | To control access to it | One |
| **Facade** | A *new, simpler* one | The subsystem is complicated | Many |

Two sharper cuts:

- **Decorator vs. Proxy** — identical structure. A decorator *adds* behaviour the original does not have; a proxy *guards* behaviour it already has. A decorator is usually stacked; a proxy usually isn't.
- **Adapter vs. Facade** — an adapter fixes something *incompatible*; a facade simplifies something *complicated*. Adapter changes the shape, Facade reduces the surface.

## Composite and Decorator are cousins

Both are recursive — an object implementing an interface while also holding something of that interface. The difference is arity: **Composite holds many children and forms a tree; Decorator holds exactly one and forms a chain.** That single distinction is why one models a filesystem and the other models pizza toppings.

## Bridge stands apart

Bridge is the odd one out: not a wrapper at all, but a deliberate split of one hierarchy into two that can grow independently. It is the answer to combinatorial class explosion — *m* × *n* combinations collapsing into *m* + *n* classes.

> **Tip** — When choosing between these, describe the problem in a sentence without naming a pattern. "I need this library's output in the units my code expects" is Adapter. "I need to add optional extras to an order" is Decorator. "I need to check permissions before this runs" is Proxy. "I need one method that does the eight steps in the right order" is Facade. The sentence usually picks the pattern for you.
