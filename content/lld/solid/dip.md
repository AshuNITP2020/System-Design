---
title: Dependency Inversion Principle
short: DIP
order: 50
desc: High-level modules should depend on abstractions, not on concrete low-level modules.
---

# D — Dependency Inversion Principle

<div class="definition"><span class="lbl">Definition</span>
High-level components should <strong>not depend on low-level components directly</strong>; instead, both should <strong>depend on abstractions</strong>. Additionally, <strong>abstractions should not depend on details — details should depend on abstractions</strong>.
</div>

In practice: classes should depend on interfaces rather than on concrete classes.

## What exactly is being "inverted"

In a conventional layered design, the arrow of dependency follows the arrow of control: high-level policy calls — and therefore depends on — low-level detail.

| Traditional (no DIP) | Inverted (DIP) |
| --- | --- |
| `MacBook` → `WiredKeyboard` | `MacBook` → `Keyboard` ← `WiredKeyboard` |
| High-level policy is chained to a specific detail. Change the detail, change the policy. | Both sides point at the abstraction. The low-level detail now depends on the contract, not the other way round. |

The **direction of the source-code dependency** is inverted relative to the direction of the runtime call. That inversion is what lets you swap, mock or add implementations freely.

## The utility classes

Two abstractions and their concrete implementations, shared by both versions below. `BluetoothKeyboard`, `WiredMouse` and `BluetoothMouse` have the same shape as `WiredKeyboard` and differ only in the label they print.

```java-sample dir="lld/solid/dip/violation" title="Abstractions and their implementations" files="Keyboard.java,Mouse.java,WiredKeyboard.java,BluetoothKeyboard.java,WiredMouse.java,BluetoothMouse.java"
```

## Violation

```java-sample dir="lld/solid/dip/violation" variant="bad" title="High-level module nailed to concrete types" files="MacBook.java,DemoViolation.java" run
```

### What is wrong

- `MacBook` is tightly coupled to `WiredKeyboard` and `WiredMouse`.
- A `MacBook` cannot be built with different parts without modifying the `MacBook` class — which is also an [OCP](ocp.html) violation.
- `MacBook` is difficult to test in isolation: no test double can stand in for a concrete `WiredKeyboard`.
- The high-level module depends on low-level modules — precisely the arrangement DIP forbids.

Note that the `Keyboard` and `Mouse` interfaces **already existed**. Declaring an abstraction is not enough; the *dependency* has to point at it. This is the single most common way DIP is faked in real codebases — interfaces everywhere, and constructors that still name concrete types.

## Refactoring

The utility code is unchanged. Only the field and constructor **types** change — and that is the whole principle:

```java-sample dir="lld/solid/dip/refactored" variant="good" title="Depend on the abstraction, inject the detail" files="MacBook.java,DemoSolution.java" run
```

### What it bought

- `WiredKeyboard`, `BluetoothKeyboard`, `WiredMouse` and `BluetoothMouse` depend only on the `Keyboard` / `Mouse` abstractions.
- Any parts can be injected without changing `MacBook`.
- Easy to mock for testing — pass a stub `Keyboard` and assert on it.
- New parts can be added without touching existing code (DIP delivering OCP).

## DIP vs. Dependency Injection vs. IoC

These three get conflated constantly, in interviews and in code review. They're related but distinct:

| Term | Kind | What it means |
| --- | --- | --- |
| **DIP** — Dependency Inversion Principle | Design principle | The *rule*: depend on abstractions, not concretions. Says nothing about how the dependency arrives. |
| **DI** — Dependency Injection | Technique | A class receives its collaborators from outside — constructor, setter or field — instead of creating them with `new`. The usual way to *achieve* DIP. |
| **IoC** — Inversion of Control | Broader pattern | The framework, not your code, drives the flow and supplies the objects (e.g. the Spring container). DI is one form of IoC. |

You can satisfy DIP with no framework at all — the refactored `MacBook` above uses plain constructor injection and a `main` method. Equally, you can use Spring throughout and still violate DIP, if your services `@Autowired` concrete classes.

### Injection styles

| Style | Verdict | Why |
| --- | --- | --- |
| **Constructor** | **Preferred** | Dependencies are explicit and mandatory, fields can be `final` (immutable), and the object is never in a half-built state. A constructor with too many parameters is also a useful SRP alarm. |
| **Setter** | Situational | Fine for genuinely optional or reconfigurable dependencies; otherwise it allows objects that are constructed but not usable. |
| **Field** (`@Autowired` on a field) | Avoid | Hides dependencies from the constructor, prevents `final`, and makes the class hard to instantiate in a plain unit test without a container. |

> **Tip** — Who should own the abstraction? A refinement that matters in layered architectures: the interface should be defined by, and live with, the **high-level module that consumes it** — not with the low-level module that implements it. If `Keyboard` lives in the vendor's package, `MacBook` still depends on the vendor. If `Keyboard` is declared by the laptop domain and vendors implement it, the dependency truly points inward. This is the core idea behind **Hexagonal / Ports-and-Adapters** and **Clean Architecture**.

## Detection

Signs of a violation:

- `new SomeConcreteService()` inside business logic.
- Static or singleton access to infrastructure — `Database.getInstance()`.
- A unit test that cannot run without a real database, network or clock.
- Domain classes importing framework, driver or vendor packages.

How to fix:

1. Introduce an interface for the collaborator and inject it via the constructor.
2. Move object creation to a **composition root** — `main()`, a factory, or a DI container. Ideally exactly one place in the program names concrete types.
3. Keep the interface in the consumer's package.

> **Pitfall** — Not everything deserves an interface. Creating a one-implementation interface for every class ("`FooImpl` syndrome") adds indirection with no benefit. Invert dependencies that cross a *meaningful boundary* — I/O, external systems, policy vs. mechanism. Value objects, DTOs and pure functions do not need inverting.

> **Summary** — The refactored design demonstrates true dependency inversion because the high-level module (`MacBook`) and the low-level modules (`WiredKeyboard`, `BluetoothKeyboard`, `WiredMouse`, `BluetoothMouse`) both depend on the same abstractions (`Keyboard` and `Mouse`), and those abstractions depend on no concrete implementation detail.
