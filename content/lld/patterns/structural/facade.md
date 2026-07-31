---
title: Facade
short: Facade
order: 50
desc: Put one simple door in front of a complicated subsystem.
---

# Facade

<div class="definition"><span class="lbl">Definition</span>
The Facade Pattern provides a <strong>simplified interface to a complex subsystem</strong>, so the client can interact with it seamlessly.
</div>

Whenever system complexity has to be hidden from the client, this is the pattern.

The real-world version is a **car**. You press the accelerator and the brake. Behind those two pedals sit fuel injection, ignition timing, transmission, ABS and a dozen sensors, all sequenced correctly. The driver gets two controls; the complexity is real but not theirs to manage.

## The problem

Placing an order touches four services, and without a facade the client drives all of them:

- The client must talk to **every subsystem directly** and get the **sequence** right.
- **No encapsulation** — the client is coupled to the subsystem's internal structure.
- **Hard to change.** Add a `DiscountService` or alter the payment flow and you edit *every* client.
- **Error-prone.** A client can forget a step — `sendConfirmation()` — or run them out of order, taking payment before checking stock.

## The solution

```java-sample dir="lld/patterns/structural/facade" variant="good" title="One call instead of four, in the right order" files="InventoryService.java,PaymentService.java,ShippingService.java,NotificationService.java,OrderFacade.java,ECommerceApp.java" run
```

The client makes one call. The knowledge that stock must be checked *before* payment, and confirmation sent *after* shipping, now lives in exactly one place.

## Participants

| Role | In this example | Responsibility |
| --- | --- | --- |
| **Facade** | `OrderFacade` | Holds every subsystem and exposes the operation the client actually wants |
| **Subsystem A** | `InventoryService` | Checks stock |
| **Subsystem B** | `PaymentService` | Processes payment |
| **Subsystem C** | `ShippingService` | Arranges delivery |
| **Subsystem D** | `NotificationService` | Sends confirmation |
| **Client** | `ECommerceApp` | One call: `placeOrder()` |

## Three ways it gets used

1. **Expose only what's needed.** A subsystem has forty public methods and clients use six. The facade offers those six.
2. **Sequence a workflow.** As above — the order of steps is the thing being encapsulated.
3. **A facade over facades.** A facade may call another facade rather than a raw subsystem. Layering them is normal; each provides a single entry point at its own level.

> **Note** — A facade **does not block** access. Clients can still talk to a subsystem directly when they genuinely need to — a reporting job might query `InventoryService` on its own. The facade removes the *need*, not the *ability*. That is a deliberate difference from [Proxy](proxy.html), which exists precisely to stand between the client and the object.

## Facade vs. Proxy

Both structural, different problems:

| | Facade | Proxy |
| --- | --- | --- |
| Intent | Simplify a complex subsystem | Control access to one object |
| Interface | New and simpler, invented for the client | The **same** as the real object's |
| Objects behind it | **Many** | **One** |
| Client awareness | Knows it's using a facade | Usually doesn't know |

## Facade vs. Adapter

Both hand the client a different interface:

| | Facade | [Adapter](adapter.html) |
| --- | --- | --- |
| Intent | Hide complexity | Fix incompatibility |
| Problem | The subsystem is complicated | The interface is the wrong shape |
| Interface | Invented for convenience | Dictated by what the client already expects |
| Result | A smaller surface | Two things that now fit together |

> **Warning** — The failure mode is the facade becoming a **god object**. It starts as four delegating calls and accumulates business logic, validation, error handling and special cases until it is the system. Keep it thin: a facade *sequences and delegates*. When a step needs real logic, that logic belongs in a subsystem, not in the door.

> **Tip** — Facade is [ISP](../../solid/isp.html) at subsystem scale — the client depends on the small surface it uses rather than everything the subsystem exposes. It is also where a service layer usually starts: `OrderFacade.placeOrder()` and `OrderService.placeOrder()` are frequently the same class under different names.

> **Summary** — Facade gives the client one entry point instead of four, and moves the sequencing knowledge out of every caller into a single class. The subsystems stay reachable; they just stop being everybody's problem.
