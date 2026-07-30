---
title: Liskov Substitution Principle
short: LSP
order: 30
desc: Subtypes must be substitutable for their base type without breaking the program.
---

# L — Liskov Substitution Principle

<div class="definition"><span class="lbl">Definition</span>
Objects of a superclass should be <strong>replaceable with objects of its subclasses without breaking the application</strong>.
</div>

If class `B` is a subtype of class `A`, then objects of `A` can be replaced with objects of `B` without altering the correctness of the program. A subclass must **extend** the capability of its parent — never **narrow** it.

Formulated by **Barbara Liskov** in 1987, and given its formal treatment in Liskov & Wing's *A Behavioral Notion of Subtyping* (1994).

## The four contract rules

LSP is not about method signatures — the compiler already checks those. It is about **behaviour**. A subtype must honour the promises its parent made to callers:

| Rule | Meaning | Typical violation |
| --- | --- | --- |
| **Preconditions must not be strengthened** | The subclass may not demand more of the caller than the parent did. | Parent accepts any `int`; child rejects negatives. Child requires an engine the parent never required. |
| **Postconditions must not be weakened** | The subclass must deliver at least what the parent promised. | Parent guarantees a non-null result; child returns `null`. |
| **Invariants must be preserved** | Rules that always hold for the parent must still hold for the child. | `Square extends Rectangle` breaks "width and height are independent". |
| **History constraint** | A subclass may not permit state changes the parent forbids. | A `MutablePoint` subclassing an immutable `Point`. |

A fifth, practical rule: **no new checked exceptions**. A subtype must not throw exception types that callers of the supertype were never told to handle.

## Example 1 — the Bike hierarchy

### Violation

```java-sample dir="lld/solid/lsp/bike-violation" variant="bad" title="A Bicycle that cannot be a Bike" files="Bike.java,MotorCycle.java,Bicycle.java,Demo.java" run
```

Run it and you'll see the `MotorCycle` lines print, then an `AssertionError` from `Bicycle.turnOnEngine()`. That crash *is* the principle being violated, live.

**What is wrong:**

- Not all bikes have engines (bicycles, and partly e-bikes), yet the base type forces `turnOnEngine()`.
- `Bicycle` throws instead of behaving — it breaks the contract its own type advertises. That is a **strengthened precondition**: the caller now has to know which kind of `Bike` it holds.
- Client code cannot treat all `Bike` subtypes uniformly, which defeats the entire point of having the abstraction.

### Refactoring

Take the capability that isn't universal out of the base type and give it its own contract:

```java-sample dir="lld/solid/lsp/bike-refactored" variant="good" title="Bike + optional Engine capability" files="Bike.java,Engine.java,MotorCycle.java,Bicycle.java,Demo.java" run
```

> **Note** — The fix — splitting a fat `Bike` contract into `Bike` + `Engine` — is literally the [Interface Segregation Principle](isp.html). LSP violations caused by "not every subtype supports this method" are almost always ISP violations wearing a different hat.

**What it bought:**

- **Prevents fragile code** — no subclass can violate the expectations set by its parent.
- **Improves flexibility** — types can be substituted freely without breaking the contract.
- **Improved maintainability** — changes to a subclass do not break existing client code.
- **Promotes reuse** — base classes can be reused and new subclasses added without modifying existing code.

## Example 2 — the Vehicle hierarchy

A subtler violation. Nothing throws, nothing is left unimplemented — but a **postcondition is weakened**: the parent implicitly promises a non-null `Boolean`, and the subclass returns `null`.

### Violation

```java-sample dir="lld/solid/lsp/vehicle-violation" variant="bad" title="null where the caller expected a Boolean" files="Vehicle.java,MotorCycle.java,Car.java,Bicycle.java,ViolationDemo.java" run
```

The first loop works. The second loop — same code, one extra element — dies with a `NullPointerException`. The client did nothing wrong; the type system told it every `Vehicle` could answer `hasEngine()`.

### Refactoring

Push the capability *down* into an intermediate type, so a class that cannot answer `hasEngine()` never exposes it:

```java-sample dir="lld/solid/lsp/vehicle-refactored" variant="good" title="Capability pushed into a subtype" files="Vehicle.java,Bicycle.java,EngineVehicle.java,MotorCycle.java,Car.java,SolutionDemo.java" run
```

> **Tip** — The real win here: a runtime `NullPointerException` that only appears when a bicycle happens to be in the list has become an error the compiler refuses to build. Uncomment the `vehicleList2.add(new Bicycle())` line in `SolutionDemo.java` and the build fails — which is exactly what you want. Pushing correctness from runtime to compile time is the whole point of getting a hierarchy right.

## Detection

Signs of a violation:

- An override that `throw`s `UnsupportedOperationException` or `AssertionError`.
- An override with an empty body, or one that returns `null` / a sentinel value.
- `instanceof` or a downcast in *client* code before calling a method.
- Documentation that says "do not call `x()` on `SubClass`".
- Tests written against the base type fail when run against a subtype.

How to fix, in rough order of preference:

1. **Prefer composition** — most "is-a" relationships are better modelled as "has-a". This is the correct answer more often than any of the others.
2. Split the capability into its own interface (Example 1).
3. Push the capability into an intermediate subtype (Example 2).
4. Write one shared test suite against the base type and run it against every subtype — *contract tests*. This turns LSP from a code-review argument into a build failure.

> **Warning** — "IS-A" is not enough. A square *is a* rectangle in geometry, but `Square extends Rectangle` breaks LSP: client code that sets width and height independently and expects `area == w × h` fails, because setting one side of a square silently changes the other. Inheritance requires **behaves-like-a**, not merely is-a.

> **Summary** — LSP upholds the integrity of a class hierarchy, ensuring that extending functionality or adding subclasses doesn't disrupt existing behaviour. It keeps code clean, modular and safe to evolve — and it's the precondition that makes [OCP's](ocp.html) polymorphic extension points trustworthy in the first place.
