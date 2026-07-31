---
title: Decorator
short: Decorator
order: 40
desc: Add behaviour to objects dynamically by wrapping them, instead of subclassing every combination.
---

# Decorator

<div class="definition"><span class="lbl">Definition</span>
The Decorator Pattern allows you to <strong>add new functionality to objects dynamically</strong>, at runtime, <strong>without altering their original structure</strong>.
</div>

## The problem: class explosion

A pizza shop. One base pizza — Margherita, crust and cheese — and a list of toppings: extra cheese, olives, jalapenos, pepperoni, veggies, spicy red pepper.

Customers order combinations:

- Margherita + Extra Cheese
- Margherita + Olives + Jalapenos
- Margherita + Olives + Jalapenos + Veggies + Extra Cheese
- Margherita + Pepperoni + Spicy Red Pepper

Or a coffee shop, which is the same problem: espresso plus sugar, hot water, cold water, ice, steamed milk, milk foam, chocolate syrup, vanilla ice cream — producing Doppio, Americano, Cappuccino, Mocha, Cold Coffee, and whatever a customer invents at the counter.

Model each combination as a subclass and you get **class explosion**: `MilkAndSugarCoffee`, `SugarAndVanillaCoffee`, `MilkVanillaIceCreamCoffee`, `ChocolateSyrupVanillaIceCreamCoffee`… The count grows toward 2ⁿ in the number of add-ons. With 8 toppings that is 256 classes, and a customer can still ask for one you didn't anticipate.

## The solution

Make each topping an object that **wraps** a pizza and is itself a pizza. Combinations are then built at runtime by nesting, not declared as classes.

```java-sample dir="lld/patterns/structural/decorator" variant="good" title="Wrapping instead of subclassing" files="BasePizza.java,PlainPizza.java,Farmhouse.java,TandooriPaneerDelight.java,ChickenDominator.java,ToppingDecorator.java,ExtraCheeseTopping.java,VeggiesTopping.java,MushroomTopping.java,PepperoniTopping.java,PizzaShop.java" run
```

Ten different products in the demo, from **four base classes and four toppings** — and no class named `ExtraCheeseAndMushroomFarmhousePizza` anywhere.

## Participants

| Role | In this example | Responsibility |
| --- | --- | --- |
| **Component** | `BasePizza` | The contract shared by base objects *and* decorators |
| **Concrete Components** | `PlainPizza`, `Farmhouse`, `TandooriPaneerDelight`, `ChickenDominator` | The base implementations |
| **Base Decorator** | `ToppingDecorator` | Abstract; implements the Component and holds one |
| **Concrete Decorators** | `ExtraCheeseTopping`, `VeggiesTopping`, `MushroomTopping`, `PepperoniTopping` | Override methods to add their contribution, then delegate |

## The two relationships

The pattern only works because `ToppingDecorator` has **both** relationships to `BasePizza` at once:

**IS-A (inheritance)** — a decorated pizza can be used anywhere a pizza can:

- `PlainPizza` **is-a** `BasePizza`
- `ExtraCheeseTopping` **is-a** `ToppingDecorator`
- `ToppingDecorator` **is-a** `BasePizza`
- therefore `ExtraCheeseTopping` **is-a** `BasePizza`

**HAS-A (composition)** — a decorator wraps another pizza and delegates to it:

- `ToppingDecorator` **has-a** `BasePizza`
- `ExtraCheeseTopping` has a `PlainPizza` — which is itself a `BasePizza`

Because a decorator both *is* and *has* a `BasePizza`, it can wrap another decorator. That is what makes them stack:

```text title="How Order 5 nests"
new MushroomTopping( new PepperoniTopping( new ExtraCheeseTopping( new PlainPizza() ) ) )

getCost() unwinds inwards, then adds outwards:
  PlainPizza            200
  + Extra Cheese         20   -> 220
  + Pepperoni            50   -> 270
  + Mushroom             40   -> 310
```

## Where you have already used it

Java's I/O library is Decorator throughout:

```java title="java.io is a decorator chain"
new BufferedReader(new InputStreamReader(new FileInputStream("data.txt")));
```

`FileInputStream` reads bytes; `InputStreamReader` decorates it with character decoding; `BufferedReader` decorates that with buffering. Same interface at every layer, each adding one thing.

> **Tip** — **Order can matter.** Compress-then-encrypt and encrypt-then-compress give very different results, and nothing in the type system stops you writing either. When the sequence is significant, document it or hide the assembly behind a [Builder](../creational/builder.html) or [Facade](facade.html) so callers cannot get it wrong.

> **Warning** — Debugging a deep chain is genuinely unpleasant. A stack trace through six wrappers of the same interface tells you very little about which layer misbehaved, and `toString()` on the outermost object often hides everything inside. Keep chains shallow, and give decorators descriptive names.

## Decorator vs. Proxy

Structurally identical — same interface, wraps one object:

- **Decorator** *adds* behaviour the original doesn't have. Usually stacked, and the client typically assembles the chain deliberately.
- **[Proxy](proxy.html)** *controls access* to behaviour that already exists. Usually one layer, and the client often doesn't know it's there.

> **Summary** — Decorator is a flexible alternative to subclassing. Behaviour is mixed and matched at runtime by wrapping, so *n* optional features cost *n* classes instead of 2ⁿ, and combinations nobody anticipated still work.
