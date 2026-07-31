---
title: Single Responsibility Principle
short: SRP
order: 10
desc: A class should have only one reason to change — one job, one responsibility, one actor.
---

# S — Single Responsibility Principle

<div class="definition"><span class="lbl">Definition</span>
A class should have <strong>only ONE reason to change</strong> — meaning it should have <strong>one and only one job or responsibility</strong>.
</div>

If a class has multiple jobs, a change to one responsibility can affect or break the others. The class becomes bloated, fragile and hard to maintain. SRP restricts each unit of code to a single concern.

## The "one reason to change" test

"Reason to change" is the most misread phrase in SOLID. The sharper formulation is: **a module should be responsible to one, and only one, *actor*** — one group of people who request changes.

Two pieces of behaviour belong in separate classes when *different stakeholders* drive them:

| Responsibility | Who asks for the change | Change trigger |
| --- | --- | --- |
| Calculating the invoice total | Finance / business | Tax or pricing rules change |
| Saving the invoice | DBA / platform team | Schema or storage engine changes |
| Printing the invoice | Design / operations | Layout or output format changes |

Three actors ⇒ three reasons to change ⇒ three classes.

This framing also explains *why* it matters in a way "one job per class" doesn't: when three teams edit one file, you get merge conflicts, unrelated re-testing, and a deploy that couples three release schedules together.

## Violation

```java-sample dir="lld/solid/srp/violation" verdict="✗ Violates SRP" variant="bad" title="Invoice does three jobs" files="Marker.java,Invoice.java,Demo.java" run
```

### What is wrong

The `Invoice` class carries three distinct responsibilities:

1. `calculateTotal()` → business logic
2. `saveToDB()` → persistence
3. `printInvoice()` → presentation

Which means:

- If the **tax calculation rules** change, `Invoice` must be modified.
- If the **database structure** changes, `Invoice` must be modified.
- If the **printing requirement** changes, `Invoice` must be modified.
- A single class must be re-tested and re-deployed for three unrelated kinds of change.
- You cannot test the total calculation without dragging persistence and printing along with it.

## Refactoring

Split along the actor boundaries — one class per reason to change:

```java-sample dir="lld/solid/srp/refactored" verdict="✓ Follows SRP" variant="good" title="One responsibility per class" files="Marker.java,Invoice.java,InvoiceDao.java,InvoicePrinter.java,Demo.java" run
```

### What it bought

- **One reason to change per class** — `Invoice` changes only if the total/tax rules change, `InvoiceDao` only if persistence changes, `InvoicePrinter` only if print requirements change.
- **Better maintainability** — changes to one concern cannot break the others.
- **Improved testability** — each class can be unit-tested in isolation; the total calculation no longer needs a database.
- **Enhanced reusability** — domestic and international invoices can share the same `InvoicePrinter`.

Notice that the *caller* now wires the collaborators together. That is not incidental — pushing object assembly out to the edge is what makes the [Dependency Inversion](dip.html) chapter possible.

## Detection

Signs you're looking at an SRP violation:

- You need "and" to describe the class: "it calculates totals *and* saves *and* prints".
- Names like `Manager`, `Processor`, `Util`, `Helper` — vague names hide vague responsibilities.
- Imports span unrelated layers (JDBC + PDF + HTTP in one file).
- Unit tests need heavy mocking just to reach one method.
- `git blame` shows many authors from different teams.
- The class has more than one *group* of fields, where each group is used by a different set of methods.

The fix is almost always *Extract Class*: move each responsibility, and the fields it uses, into its own class. Split where **actors** differ — not where methods merely look different.

> **Warning** — SRP does **not** mean "one method per class". A class may have many methods as long as they all serve the same responsibility. Splitting past that point produces anaemic, scattered classes and makes the code harder to follow, not easier. The measure is *reasons to change*, not line count.

> **Summary** — SRP is the foundation of maintainable, testable, flexible code. It ensures each class has one reason to change, which keeps the system modular. It is about a single *responsibility*, not a single *method*.
