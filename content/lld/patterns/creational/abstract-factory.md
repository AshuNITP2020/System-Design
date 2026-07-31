---
title: Abstract Factory
short: Abs. Factory
order: 20
desc: Create families of related objects without naming their concrete classes — a factory of factories.
---

# Abstract Factory

<div class="definition"><span class="lbl">Definition</span>
The Abstract Factory Pattern provides an interface for creating <strong>families of related objects</strong> without specifying their concrete classes. Each factory produces a complete family of products designed to work together.
</div>

It is often called the **factory of factories** or a **super factory**. It is more involved than [Factory Method](factory.html) because it creates several *related* product types at once — hence multiple factory methods on the one interface. That extra abstraction lets a client switch between entire product families at runtime.

## The problem it solves that Factory Method doesn't

An economy interior must never be fitted to a luxury exterior. With separate factories per product you can accidentally mix families; with an Abstract Factory the family is chosen **once**, and every product after that is guaranteed to match. Consistency across a group of objects is the whole point.

## Participants

| Role | In this example | Responsibility |
| --- | --- | --- |
| **Abstract Products** | `CarInterior`, `CarExterior` | One interface per product type in the family |
| **Concrete Products** | `EconomyCarInterior`, `LuxuryCarExterior`, … | Implementations; those from one family are designed to work together |
| **Abstract Factory** | `CarFactory` | Declares a creation method for each product type |
| **Concrete Factories** | `EconomyCarFactory`, `LuxuryCarFactory` | Each produces one complete family |
| **Factory Provider** | `CarFactoryProvider` | Returns the right concrete factory for the client's request |
| **Client** | `AbstractFactoryDemo` | Works only through the abstract factory |

## Implementation

### 1. Abstract products and their families

```java-sample dir="lld/patterns/creational/abstract-factory" title="Products" files="CarExterior.java,CarInterior.java,EconomyCarExterior.java,EconomyCarInterior.java,LuxuryCarExterior.java,LuxuryCarInterior.java"
```

### 2. The factory, its implementations, and the provider

```java-sample dir="lld/patterns/creational/abstract-factory" variant="good" title="Abstract Factory + concrete factories" files="CarType.java,CarFactory.java,EconomyCarFactory.java,LuxuryCarFactory.java,CarFactoryProvider.java,AbstractFactoryDemo.java" run
```

Note `produceCompleteVehicle()` — a `default` method on the factory interface. It calls both creation methods and assembles the result, so the *sequence* is written once and every family gets it for free. That is a Template Method riding along inside the Abstract Factory, and it is where the guarantee "interior and exterior always match" is actually enforced.

## Factory Method vs. Abstract Factory

| | Factory Method | Abstract Factory |
| --- | --- | --- |
| Creates | **One** product | A **family** of related products |
| Interface has | One creation method | One creation method per product type |
| Mechanism | Inheritance — a subclass overrides the method | Composition — the client holds a factory object |
| Varies by | Product subclass | Product family |
| Choose when | One product, many variants | Many products, grouped by theme / platform / brand |

> **Tip** — The deciding question: **one product with many variants → Factory Method. Many products grouped into families → Abstract Factory.** If the products in a group must be consistent with one another, that is the signal for Abstract Factory.

> **Pitfall** — Adding a new *product type* to the family (say `CarEngine`) means adding a method to `CarFactory` and implementing it in **every** concrete factory. Abstract Factory is open to new families but closed against new product types — the exact opposite of what people assume. Get the family's shape right before you commit to it.

> **Summary** — Abstract Factory is highly effective when related products must work together, which makes it well suited to complex systems with several product variants. It keeps the client entirely free of concrete classes and respects the Open/Closed Principle along its intended axis: new families.
