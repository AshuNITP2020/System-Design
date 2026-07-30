---
title: Applying SOLID Without Over-Engineering
short: Practice
order: 70
desc: The cost side — when not to apply a principle, a review checklist, and interview answers.
---

# Applying SOLID Without Over-Engineering

SOLID is frequently misapplied, usually by people who have just learned it. Every principle costs something, and the cost is paid whether or not the benefit ever materialises.

## When *not* to apply a principle

| Over-application | What it looks like | Better judgement |
| --- | --- | --- |
| **SRP taken too far** | Forty classes with one method each; you must open six files to follow one flow. | Split by *reason to change*, not by method count. Cohesion is a virtue too. |
| **OCP taken too far** | Interfaces, factories and config for variation points that never varied. | Wait for the second real variant. Speculative generality is a smell. |
| **LSP taken too far** | Deep hierarchies engineered so everything substitutes everything. | Prefer composition. Most "is-a" relationships are better as "has-a". |
| **ISP taken too far** | An interface per method; twelve tiny types nobody can name. | Segregate along real client roles, not mechanically per method. |
| **DIP taken too far** | Every class has a matching `*Impl` with exactly one implementation, forever. | Invert across meaningful boundaries — I/O, external systems, policy vs. mechanism. |

> **Warning** — The governing trade-off: SOLID buys **flexibility** and pays in **indirection**. Indirection is a real cost — more files, more names, more hops to read one behaviour. Apply a principle when the change it protects against is *likely*, and keep **YAGNI**, **KISS** and **DRY** on the other side of the scale. A simple, readable violation often beats an abstract, correct maze.

There's a useful asymmetry worth remembering: **duplication is cheaper to fix than a wrong abstraction**. If you duplicate and later regret it, you extract — a mechanical, low-risk refactor. If you abstract wrongly and later regret it, every caller has been written against the wrong shape, and unwinding it touches all of them.

## Code-review checklist

Run through this on a diff, not on a whole codebase — the point is to catch things while they're cheap.

**SRP**
- Can I describe this class in one sentence without "and"?
- Would two different teams ever need to edit it?
- Does the constructor take an unrelated grab-bag of dependencies?

**OCP**
- Does adding the next variant require editing this file, or just adding one?
- Is there a conditional on a type that grows with each new case?

**LSP**
- Does any override throw, no-op, or return `null` where the parent doesn't?
- Would the base type's tests pass unchanged against every subtype?
- Is there an `instanceof` or downcast in client code?

**ISP**
- Does every implementer genuinely need every method of this interface?
- How many methods must a test double stub in order to exercise one call?

**DIP**
- Does any domain class `new` up infrastructure, or import a driver/framework?
- Can this class be unit-tested with no database, network or clock?

**All**
- Is the abstraction justified by a change that is actually likely, or is it speculation?

## Interview quick answers

| Question | A tight answer |
| --- | --- |
| Define SRP in one line. | A class should have one reason to change — it should be responsible to a single actor. |
| How can code be closed for modification yet still extended? | By depending on an abstraction: new behaviour arrives as a new implementation, so no existing, tested line is edited. |
| Give the classic LSP violation. | `Square extends Rectangle`, or any override that throws `UnsupportedOperationException` — the subtype breaks a promise the base type made. |
| Is ISP just SRP? | Same idea, different target: SRP constrains a class's responsibilities, ISP constrains what a contract forces on its clients. |
| Difference between DIP and DI? | DIP is the principle (depend on abstractions); DI is a technique for supplying those dependencies; IoC is the broader pattern where a framework drives the flow. |
| Which principle do you use most? | SRP and DIP — together they're what make code testable, and everything else follows from being able to refactor safely. |
| When would you knowingly break SOLID? | When the abstraction is speculative. A small, obvious, duplicated piece of code beats a wrong abstraction — refactor when the second real variant appears. |

## The whole thing on one page

| | Principle | Ask yourself | If the answer is bad |
| --- | --- | --- | --- |
| **S** | Single Responsibility | How many reasons does this class have to change? | Extract a class |
| **O** | Open/Closed | Must I edit tested code to add a variant? | Extract an interface; add an implementation |
| **L** | Liskov Substitution | Can any subtype stand in for the base with no surprises? | Fix the hierarchy or use composition |
| **I** | Interface Segregation | Does any implementer get methods it cannot honour? | Split into role interfaces |
| **D** | Dependency Inversion | Does policy depend on detail? | Inject an abstraction via the constructor |

> **Tip** — SOLID is not a checklist to satisfy — it's a vocabulary for talking about *where change will hurt*. The goal isn't a codebase that scores well against five rules; it's a codebase where the next change is cheap, safe and local. Where a principle serves that goal, apply it. Where it doesn't, say so out loud and move on.

## Going deeper

- **Robert C. Martin** — *Design Principles and Design Patterns* (2000), *Clean Code*, *Clean Architecture*
- **Barbara Liskov & Jeannette Wing** — *A Behavioral Notion of Subtyping* (1994), the formal basis of LSP
- **Martin Fowler** — *Refactoring* (Extract Class, Replace Conditional with Polymorphism); his articles on Role Interfaces and Inversion of Control
- **Shrayansh Jain (Concept && Coding)** — the Low Level Design series these notes follow, including the dedicated LSP walkthrough
