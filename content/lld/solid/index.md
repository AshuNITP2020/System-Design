---
title: SOLID — Overview
short: Intro
order: 0
desc: What SOLID is, the design rot it prevents, and a cheat sheet of all five principles.
---

# SOLID Principles

<p class="lede">Five object-oriented design principles for managing change. Not rules of syntax — no compiler enforces them — but heuristics for keeping the cost of the <em>next</em> change roughly constant instead of growing with the age of the codebase.</p>

The five were introduced by **Robert C. Martin ("Uncle Bob")** around 2000 in *Design Principles and Design Patterns*; the acronym itself was coined later by Michael Feathers.

Software that is never modified needs no design at all. The cost of software is dominated by everything that happens *after* the first release, and that is the problem SOLID exists to address.

## The four symptoms of rotting design

Every principle here is a countermeasure to one or more of these. When you are arguing about whether a refactor is worth it, this is the vocabulary to argue in:

| Symptom | What it looks like | Principles that fight it |
| --- | --- | --- |
| **Rigidity** | A small change forces a cascade of changes in dependent modules. | SRP, DIP |
| **Fragility** | A change breaks things in conceptually unrelated places. | SRP, OCP, LSP |
| **Immobility** | A useful piece of code can't be reused elsewhere because it drags half the system with it. | SRP, ISP, DIP |
| **Viscosity** | Doing the right thing is harder than doing the hack, so the hack wins. | OCP, DIP |

## Cheat sheet

| | Principle | One-line statement | Primary tool | Loudest smell |
| --- | --- | --- | --- | --- |
| **S** | [Single Responsibility](srp.html) | A class should have only *one reason to change*. | Extract class / split by actor | "…and…" in the class description; god classes |
| **O** | [Open / Closed](ocp.html) | Open for *extension*, closed for *modification*. | Interfaces + polymorphism (Strategy) | Editing a tested class for every new variant; `switch` on type |
| **L** | [Liskov Substitution](lsp.html) | Subtypes must be usable anywhere the supertype is, without surprises. | Correct hierarchy; prefer composition | Overrides that throw, return `null`, or do nothing |
| **I** | [Interface Segregation](isp.html) | No client should be forced to depend on methods it does not use. | Many small role interfaces | Fat interfaces; empty method bodies |
| **D** | [Dependency Inversion](dip.html) | Depend on abstractions, never on concretions. | Constructor injection against interfaces | `new ConcreteThing()` inside business logic; untestable classes |

## They are not five independent rules

The usual S→O→L→I→D teaching order is alphabetical convenience, not a dependency order. The real relationships:

- **SRP and ISP** are the same idea at two levels — SRP keeps a *class* focused, ISP keeps a *contract* focused.
- **ISP breaks lead to LSP breaks.** A fat interface forces implementers to stub methods they cannot support, and a stub that throws is exactly how substitutability dies.
- **LSP is the precondition for OCP.** OCP extends behaviour through polymorphism; that is only safe if every subtype is genuinely substitutable.
- **DIP is the mechanism for OCP.** You can only swap in a new implementation without editing a class if that class depends on an abstraction.

> **Key idea** — SRP and ISP tell you *where to draw the lines*. LSP tells you *whether the lines are honest*. DIP tells you *which way the arrows point*. OCP is the payoff.

[Putting it all together](together.html) works through a single example with all five applied at once, and [Applying SOLID without over-engineering](practice.html) covers the cost side — because every one of these principles can be, and routinely is, overdone.

## How each chapter is laid out

The five principle pages all follow the same shape, so they work as a lookup as well as a read-through:

1. **Definition** — the formal statement plus a plain-English restatement.
2. **Violation** — real code that breaks the principle, and exactly what's wrong with it.
3. **Refactoring** — the corrected code and what it bought.
4. **Detection & pitfalls** — how to spot it in review, and how the principle gets over-applied.

Every code block on those pages is editable, and every sample maps to a directory under `code/` that compiles and runs:

```bash
./run.sh --list                       # see everything available
./run.sh lld/solid/srp/violation      # compile and run one
```

The SRP and OCP chapters share a running example — an `Invoice` for a `Marker` — where the class produced by the SRP refactoring becomes the starting point for the OCP chapter.
