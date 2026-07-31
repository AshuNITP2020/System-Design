---
title: Bridge
short: Bridge
order: 20
desc: Decouple an abstraction from its implementation so the two hierarchies can evolve independently.
---

# Bridge

<div class="definition"><span class="lbl">Definition</span>
The Bridge Pattern <strong>decouples an abstraction</strong> — the high-level logic, the "what" — <strong>from its implementation</strong> — the low-level details, the "how" — so that the two can evolve independently.
</div>

## The problem

Different living things, each with a respiratory mechanism. The naive approach puts the breathing logic straight into each class:

```java-sample dir="lld/patterns/structural/bridge/problem" variant="bad" title="Breathing logic baked into each animal" files="LivingThings.java,Dog.java,Whale.java,Fish.java,Tree.java,Demo.java" run
```

**What goes wrong:**

1. **Code duplication.** `Dog` and `Whale` both breathe through lungs — the same logic, written twice, worded slightly differently. Fix a bug in one and you must remember the other exists.
2. **Tight coupling.** Breathing is welded to the animal. You cannot reuse a mechanism, and you cannot introduce one independently.
3. **Class explosion.** Push further and you end up naming every combination: `DogWithLungs`, `WhaleWithLungs`, `FishWithGills`. With *m* animals and *n* mechanisms you are heading for *m* × *n* classes.

## The solution

Stop treating this as one hierarchy. It is **two**: what the thing *is*, and how it *breathes*. Give each its own hierarchy and connect them with a reference — that reference is the bridge.

```java-sample dir="lld/patterns/structural/bridge/solution" variant="good" title="Two hierarchies, one reference between them" files="BreathingProcess.java,LungBreathing.java,GillBreathing.java,Photosynthesis.java,LivingThings.java,Dog.java,Whale.java,Fish.java,Tree.java,Client.java" run
```

`LungBreathing` is now written **once** and used by both `Dog` and `Whale`. The combinations are made at runtime, in the client, by constructor argument.

## Participants

| Role | In this example | Responsibility |
| --- | --- | --- |
| **Abstraction** | `LivingThings` | The high-level concept; holds a reference to the Implementor |
| **Refined Abstraction** | `Dog`, `Fish`, `Whale`, `Tree` | Concrete types with more specific behaviour |
| **Implementor** | `BreathingProcess` | Defines *how* the operation is carried out |
| **Concrete Implementors** | `LungBreathing`, `GillBreathing`, `Photosynthesis` | The actual mechanisms, reusable across abstractions |

## What it bought

*m* × *n* became *m* + *n*:

| | Without Bridge | With Bridge |
| --- | --- | --- |
| 4 animals × 3 mechanisms | 12 classes | 7 classes |
| Add a mechanism (`SkinBreathing`) | Touch every animal that uses it | One new class |
| Add an animal (`Frog`) | Reimplement its breathing | One new class, plug in a mechanism |
| Change lung logic | Edit every lung-breathing class | Edit `LungBreathing` |

The saving grows with the product. At 10 × 5 it is 50 classes versus 15.

## Bridge vs. Strategy

Structurally near-identical — an object holding an interface it delegates to. The difference is **intent**:

| | Bridge | Strategy |
| --- | --- | --- |
| Purpose | Keep two **hierarchies** independent | Swap an **algorithm** at runtime |
| Both sides vary? | Yes — animals and mechanisms both grow | Usually only the algorithm varies |
| Set when | Typically at construction | Typically changed on the fly |
| Example | `LivingThings` and its respiratory mechanism | Google Maps route: fastest / shortest / avoid tolls |

Bridge is a **structural** decision about how you decompose a domain, made once at design time. Strategy is a **behavioural** decision about swapping behaviour, made repeatedly at runtime.

> **Tip** — The signal for Bridge is a class name containing two nouns joined by a preposition or conjunction — `DogWithLungs`, `WindowsScrollBar`, `PdfInvoiceFormatter`. Two independent dimensions have been flattened into one name. Split them, and the multiplication becomes an addition.

> **Warning** — Bridge only pays off when **both** dimensions genuinely vary. If there will only ever be one breathing mechanism, you have added an interface, a field and a constructor parameter to buy flexibility nobody will use. Wait until the second implementor is real.

> **Summary** — Bridge splits one hierarchy that was doing two jobs into two hierarchies that each do one, joined by a reference. New abstractions and new implementations can then be added independently, and combinations are made at runtime instead of being enumerated as classes.
