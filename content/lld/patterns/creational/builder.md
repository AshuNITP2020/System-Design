---
title: Builder
short: Builder
order: 30
desc: Construct a complex object step by step, instead of through a constructor with nine arguments.
---

# Builder

<div class="definition"><span class="lbl">Definition</span>
The Builder Pattern is a creational pattern that <strong>constructs complex objects step by step</strong>.
</div>

It earns its place when an object has many optional parameters, or when the same construction process should be able to produce different representations.

## The problem: telescoping constructors

Four mandatory fields and five optional ones. Every combination anyone has needed so far becomes another constructor:

```java-sample dir="lld/patterns/creational/builder/problem" variant="bad" title="Constructor overload explosion" files="Student.java,DemoProblem.java" run
```

Run it and look at the third call — `new Student(3, 21, "Ravi", "ECE", null, null, "ravi@iitb.com")`. Two nulls just to reach the email.

**What goes wrong:**

- **Telescoping constructors.** Many optional parameters produce a combinatorial pile of constructors.
- **Signatures collide.** Look at the commented-out `mobileNo` constructor — it has the *same erasure* as the `emailId` one, so it cannot exist. Uncomment it and the file stops compiling. You are out of room in the type system.
- **Unreadable call sites.** `new Student(2, 24, "Sarah", "MBA", "Gabriel", "Taylor", …)` — which String is which? Swap two by mistake and it compiles, runs, and quietly stores wrong data.
- **`null` padding.** Callers must pass `null` for options they don't want, which is a standing invitation to bugs.
- **No immutability.** If you want `final` fields you cannot fall back on setters.
- **Verbose test setup.** Building fixtures gets painful fast.
- **Violates [SRP](../../solid/srp.html).** The class manages both its data *and* the complexity of its own construction.

## Participants

| Role | In this example | Responsibility |
| --- | --- | --- |
| **Product** | `Student` | The complex object being built; package-private constructor so only builders can create it |
| **Abstract Builder** | `StudentBuilder` | Declares every construction step; returns `this` for chaining; has `build()` |
| **Concrete Builders** | `EngineeringStudentBuilder`, `MBAStudentBuilder` | Each knows how to build one variety of product |
| **Director** *(optional)* | `StudentRegistrationDirector` | Encapsulates a standard construction sequence |

## Implementation

```java-sample dir="lld/patterns/creational/builder/solution" variant="good" title="Builder" files="Student.java,StudentBuilder.java,EngineeringStudentBuilder.java,MBAStudentBuilder.java,StudentRegistrationDirector.java,Client.java" run
```

Three things make it work:

1. **Every setter returns `this`.** That is the entire trick behind method chaining.
2. **`Student`'s constructor is package-private** and takes the builder. The product cannot be built halfway, and once built it is not modified.
3. **`setSubjects()` is abstract.** The shared steps live in the base builder; only the step that genuinely differs per student type is deferred to subclasses.

The **Director** is optional and often skipped. It is worth having when several callers need the *same* standard configuration — it stores the recipe. The last block in `Client` builds a student without one, which is how Builder is used most of the time.

> **Note** — In modern Java you will more often see the **static nested builder**: `Student.builder().name("Ravi").build()`, with `Student.Builder` declared inside `Student`. Same pattern, less ceremony, and no separate hierarchy. Lombok's `@Builder` generates exactly this. The two-class version shown here is worth understanding first because it makes the roles explicit — and because the abstract-builder form is what you need when different *kinds* of product share construction steps.

## Builder vs. Decorator

Both build up an object in layers, which is why they get confused:

| | Builder | [Decorator](../structural/decorator.html) |
| --- | --- | --- |
| Purpose | Construct one complex object | Add behaviour to a finished object |
| When | Before the object exists | After it exists, at runtime |
| Result | One product, built once | A chain of wrappers |
| Signal | Too many constructor parameters | Too many subclass combinations |

**Building a complex object → Builder. Layering enhancements onto an existing one → Decorator.**

> **Summary** — Builder replaces an unusable set of constructors with a readable, self-labelling chain of steps. Every argument is named by the setter that takes it, optional parameters simply go unmentioned, and the product can be immutable because it is constructed exactly once, in one call.
