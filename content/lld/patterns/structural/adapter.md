---
title: Adapter
short: Adapter
order: 10
desc: Bridge two incompatible interfaces so they can work together.
---

# Adapter

<div class="definition"><span class="lbl">Definition</span>
The Adapter Pattern is a structural pattern that acts as a <strong>bridge between two incompatible interfaces</strong>, letting them work together by presenting data in the format the client expects.
</div>

The everyday version is a **power adapter**: your laptop's plug and a foreign socket are incompatible, neither can be modified, and a small piece of hardware in between makes them work. Same with a weighing scale that reads pounds when your application expects kilograms, or a service returning XML when your client parses JSON.

## Participants

| Role | In this example | Responsibility |
| --- | --- | --- |
| **Target** | `WeighingMachineAdapter` | The interface the client wants — weight in kg |
| **Adaptee** | `ImperialWeighingMachineImpl` | The existing, incompatible class — reports pounds |
| **Adapter** | `WeightMachineAdapterImpl` | Implements the Target, holds the Adaptee, converts between them |
| **Client** | `MetricWeighingMachine` | Uses the Target, unaware any conversion happens |

## Implementation

```java-sample dir="lld/patterns/structural/adapter" variant="good" title="Pounds to kilograms" files="ImperialWeighingMachine.java,ImperialWeighingMachineImpl.java,WeighingMachineAdapter.java,WeightMachineAdapterImpl.java,MetricWeighingMachine.java" run
```

The whole pattern is in `WeightMachineAdapterImpl`: it **implements** the interface the client wants and **holds** the object it is adapting. Two relationships, and the conversion lives between them.

Note that the adapter uses **composition**, not inheritance — it has-a `ImperialWeighingMachine`. This is *object adapter*, the form you almost always want. There is also a *class adapter* that inherits from both, but Java's single inheritance rules it out for classes, and composition keeps the adaptee swappable.

## Where it earns its keep

Adapter is the pattern you use most often without naming it, because it is the standard answer to code you don't own:

- A third-party SDK whose types you refuse to let leak through your codebase.
- A legacy service you're strangling — the adapter presents the new interface while the old implementation is still behind it.
- Two subsystems from different teams that model the same concept differently.

> **Tip** — Adapter is where [DIP](../../solid/dip.html) meets reality. You define the interface your domain wants; the adapter makes the vendor satisfy it. Without the adapter, every class touching that vendor depends on the vendor's types — and swapping it becomes a codebase-wide edit. With it, the blast radius is one class. This is precisely what "adapters" means in Hexagonal / Ports-and-Adapters architecture.

## Adapter vs. Facade

Both hand the client a different interface, which is why they're confused:

| | Adapter | [Facade](facade.html) |
| --- | --- | --- |
| Problem | The interface is **incompatible** | The subsystem is **complicated** |
| Goal | Compatibility — make two things fit | Simplicity — reduce what the client must know |
| Objects behind it | Usually one | Many |
| The interface it exposes | Dictated by the client's existing expectations | Newly invented for convenience |

## Adapter vs. Decorator vs. Proxy

All three wrap a single object:

- **Adapter** changes the interface. Different type in, different type out.
- **[Decorator](decorator.html)** keeps the interface and adds behaviour.
- **[Proxy](proxy.html)** keeps the interface and controls access.

> **Warning** — An adapter should **translate, not compute**. Once conversion logic starts making decisions — retrying, caching, branching on business rules — it has stopped being an adapter and become a service wearing one. Keep it to mapping; put the decisions behind their own abstraction.

> **Summary** — Adapter lets two interfaces that were never designed for each other work together, without modifying either. It implements the interface the client expects, delegates to the one that exists, and confines the mismatch to a single class.
