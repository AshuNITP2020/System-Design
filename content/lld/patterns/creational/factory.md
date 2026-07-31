---
title: Factory Method
short: Factory
order: 10
desc: Encapsulate object creation so the client asks for a product without naming its concrete class.
---

# Factory Method

<div class="definition"><span class="lbl">Definition</span>
The Factory Method Pattern <strong>encapsulates object creation</strong> in one place, offering an interface for creating objects <strong>without specifying their exact classes</strong>. It delegates instantiation to subclasses.
</div>

Instead of using `new` directly, the client calls a factory method that returns an object conforming to a common interface. Which concrete class is actually instantiated is decided at runtime. The result is loose coupling and easy extension.

## Participants

| Role | In this example | Responsibility |
| --- | --- | --- |
| **Product** | `Shape` | The interface the factory's output conforms to |
| **Concrete Products** | `Circle`, `Rectangle`, `Square` | Specific implementations, each with its own drawing and area logic |
| **Creator** | `ShapeFactory` | Declares the factory method |
| **Concrete Creators** | `CircleCreator`, `RectangleCreator`, … | Override the factory method to return one specific product |

## The products

Both implementations below share the same product hierarchy:

```java-sample dir="lld/patterns/creational/factory/simple-factory" title="Product + concrete products" files="Shape.java,Circle.java,Rectangle.java,Square.java,ShapeType.java"
```

## Implementation 1 — Simple Factory

The version you meet first: one static method that switches on a type parameter.

```java-sample dir="lld/patterns/creational/factory/simple-factory" title="Simple Factory" files="ShapeFactory.java,SimpleFactoryDemo.java" run
```

This is genuinely useful and very common — but note what it costs. Adding `Triangle` means **editing `ShapeFactory`**, a class that is already tested and deployed. That is an [Open/Closed](../../solid/ocp.html) violation, and it is why Simple Factory is usually called an idiom rather than a pattern.

## Implementation 2 — Factory Method

The real pattern: replace the switch with polymorphism. One creator subclass per product, each responsible for exactly one type.

```java-sample dir="lld/patterns/creational/factory/factory-method" variant="good" title="Factory Method" files="ShapeFactory.java,CircleCreator.java,RectangleCreator.java,SquareCreator.java,FactoryMethodDemo.java" run
```

Adding `Triangle` is now a **new file** — `class TriangleCreator extends ShapeFactory` — and no existing line changes.

> **Note** — The demo still has a `switch` in `getShapeInstance()`, which can look like the problem hasn't moved. In real systems that mapping lives somewhere it belongs: a config file, a DI container, a registry `Map<ShapeType, ShapeFactory>`, or the framework itself. The point is that the *product-creation logic* is now polymorphic — only the lookup remains, and a lookup is data, not behaviour.

## Factory Method vs. Simple Factory

| | Simple Factory | Factory Method |
| --- | --- | --- |
| Mechanism | A static method switching on a parameter | Inheritance and polymorphism |
| Is it a GoF pattern? | No — a programming idiom | Yes |
| Adding a new product | Edit the existing factory | Add a new creator class |
| Open/Closed | Violated | Respected |
| When to prefer it | Few, stable product types | Products keep being added; you are writing a framework |

> **Tip** — Factory Method earns its keep in **frameworks and libraries**, where the framework calls the factory method but *your* code decides what it returns. That inversion — the library calling code it doesn't know about — is exactly the extension point the pattern exists to provide.

> **Summary** — Factory Method removes `new ConcreteClass()` from the client. Simple Factory centralises creation but keeps a switch you must keep editing; Factory Method replaces that switch with one class per product, so the set of products can grow without any existing class changing.
