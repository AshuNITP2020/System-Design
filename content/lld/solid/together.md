---
title: Putting It All Together
short: Together
order: 60
desc: How the five principles reinforce each other, plus one example with all five applied at once.
---

# Putting It All Together

Each chapter fixed one principle in isolation. Real designs apply them together — and they are rarely violated one at a time either. Fixing one usually improves the others.

## How the five reinforce each other

| Relationship | Why |
| --- | --- |
| **SRP → ISP** | The same idea at two levels. SRP keeps a *class* focused; ISP keeps a *contract* focused. A fat interface almost always describes a class with too many responsibilities. |
| **ISP → LSP** | Fat interfaces force implementers to stub out methods they cannot support — which is exactly how LSP gets broken. |
| **LSP → OCP** | OCP extends behaviour through polymorphism. That is only safe if every subtype is genuinely substitutable; otherwise "extension" silently breaks existing clients. |
| **DIP → OCP** | DIP is the mechanism that makes OCP achievable: you can only swap in a new implementation without editing a class if that class depends on an abstraction. |
| **SRP → testability → everything** | Small, single-purpose, dependency-injected classes are the ones that are actually easy to test — and cheap tests are what make ongoing refactoring safe. |

> **Key idea** — SRP and ISP tell you *where to draw the lines*. LSP tells you *whether the lines are honest*. DIP tells you *which way the arrows point*. OCP is the payoff.

## One example, all five

The invoice domain from the [SRP](srp.html) and [OCP](ocp.html) chapters, rebuilt with every principle in force:

```java-sample dir="lld/solid/together" variant="good" title="The invoice pipeline" files="Marker.java,Invoice.java,InvoiceRepository.java,InvoiceFormatter.java,InvoiceNotifier.java,DatabaseInvoiceRepository.java,FileInvoiceRepository.java,PlainTextInvoiceFormatter.java,InvoiceService.java,Application.java" run
```

### What each principle bought

| | Concrete payoff in the code above |
| --- | --- |
| **S** | `Invoice`, the repositories, the formatter and `InvoiceService` each change for exactly one reason. |
| **O** | Adding S3 storage or a PDF layout means adding a file; no existing line is edited. |
| **L** | Every `InvoiceRepository` really saves. `InvoiceService` never needs `instanceof`. |
| **I** | A caller that only formats doesn't depend on `save()` or `notifyCustomer()`. |
| **D** | `InvoiceService` can be unit-tested with in-memory fakes — no database required. |

The piece that ties it together is `Application` — the **composition root**. It is the only class in the program that names a concrete `DatabaseInvoiceRepository` or `PlainTextInvoiceFormatter`. Swap either argument and nothing else in the codebase changes. Everything else in the system talks to abstractions.

## Code smell → principle

Use this in reverse: start from what you can actually see in the code, and it tells you which principle is being broken.

| What you see | Principle | Usual fix |
| --- | --- | --- |
| A class you can only describe using "and" | SRP | Extract class per responsibility |
| A 900-line `*Manager` / `*Util` class | SRP | Split by actor |
| An `if/else` or `switch` on a type field that grows over time | OCP | Strategy — one class per branch |
| Editing a stable, tested class every sprint to add a variant | OCP | Extract an interface for the varying part |
| Override that throws `UnsupportedOperationException` | LSP / ISP | Split the interface, or use composition |
| Override returning `null` where the parent never does | LSP | Push the capability into a subtype |
| `instanceof` checks in client code | LSP | Fix the hierarchy; move behaviour into the type |
| Interface with 12 methods; each implementer uses 3 | ISP | Split into role interfaces |
| Adding one interface method breaks every implementer | ISP | Segregate the contract |
| `new PostgresClient()` inside a domain class | DIP | Inject an interface via the constructor |
| Test needs a live database to assert one calculation | DIP / SRP | Inject a fake; separate the calculation |
| A mock needs ten stubbed methods to test one call | ISP | Depend on a narrower interface |

## Design patterns mapped to principles

The Gang-of-Four patterns are, in large part, SOLID applied to recurring situations. Recognising which principle a pattern serves is usually more useful than memorising its UML:

| Pattern | Chiefly serves | How |
| --- | --- | --- |
| **Strategy** | OCP, DIP | New algorithm = new class implementing the strategy interface. |
| **Template Method** | OCP | Fixed skeleton, overridable steps — extend without modifying the algorithm. |
| **Decorator** | OCP, SRP | Add behaviour by wrapping instead of editing the wrapped class. |
| **Adapter** | DIP, LSP | Makes a foreign concrete type satisfy your abstraction without contaminating it. |
| **Factory / Abstract Factory** | DIP, OCP | Concentrates `new` in one place so the rest of the code stays abstract. |
| **Observer** | OCP, DIP | New listeners are added without touching the subject. |
| **Repository** | DIP, SRP | Abstracts persistence away from domain logic. |
| **Facade** | ISP | Presents a small, task-focused surface over a large subsystem. |
