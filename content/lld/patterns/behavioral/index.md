---
title: Behavioral Patterns
short: Intro
desc: Placeholder — the third GoF category, not yet written up.
---

# Behavioral Patterns

<p class="lede">Not written up yet. This section is scaffolded and ready — these notes cover <a href="../creational/index.html">Creational</a> and <a href="../structural/index.html">Structural</a> so far.</p>

Where creational patterns are about *how objects are made* and structural patterns about *how they're composed*, behavioral patterns are about **how objects communicate and divide responsibility** at runtime.

## The eleven GoF behavioral patterns

| Pattern | Intent | Already referenced in these notes |
| --- | --- | --- |
| **Strategy** | Swap an algorithm at runtime | Yes — [OCP](../../solid/ocp.html) and [Bridge](../structural/bridge.html) both compare against it |
| **Observer** | Notify dependents when state changes | Yes — [SOLID patterns table](../../solid/together.html) |
| **Template Method** | Fixed skeleton, overridable steps | Yes — used inside [Abstract Factory](../creational/abstract-factory.html) |
| **Chain of Responsibility** | Pass a request along a chain until one handles it | |
| **Command** | Turn a request into an object — undo, queue, log | |
| **State** | Change behaviour when internal state changes | |
| **Iterator** | Traverse a collection without exposing its structure | |
| **Mediator** | Centralise communication between objects | |
| **Memento** | Capture and restore state without breaking encapsulation | |
| **Visitor** | Add operations to a type hierarchy without editing it | |
| **Interpreter** | Evaluate sentences in a grammar | Partly — [Composite's expression tree](../structural/composite.html#a-second-example-expression-trees) |

Three of them are already load-bearing in the existing notes, which is worth knowing before writing them up: Strategy is the standard OCP refactoring, Template Method is what `produceCompleteVehicle()` is, and the arithmetic expression tree is Interpreter built on Composite.

## Adding the first one

```bash
$EDITOR content/lld/patterns/behavioral/strategy.md
mkdir -p code/lld/patterns/behavioral/strategy
npm run build
```

Front matter to match the rest of the section:

```text
---
title: Strategy
short: Strategy
order: 10
desc: Swap an algorithm at runtime behind a common interface.
---
```

The existing pattern pages follow a consistent shape worth keeping: **Definition → Problem → Solution (runnable sample) → Participants table → the comparison that most often causes confusion → Summary.**
