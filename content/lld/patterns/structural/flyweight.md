---
title: Flyweight
short: Flyweight
order: 60
desc: Share the state that's common to many similar objects, and pass in the part that differs.
---

# Flyweight

<div class="definition"><span class="lbl">Definition</span>
The Flyweight Pattern <strong>reduces memory usage by sharing data common to multiple similar objects</strong>. It is used where an application must create a very large number of similar objects.
</div>

Unlike the other structural patterns, Flyweight doesn't wrap anything. It **splits** an object's state in two.

## Intrinsic vs. extrinsic state

The entire pattern rests on this distinction:

| | Meaning | Example — a game robot | Example — a text character |
| --- | --- | --- | --- |
| **Intrinsic** | Shared between objects; fixed once set | Type, sprite bitmap | The character, font, size |
| **Extrinsic** | Differs per object; supplied by the client | x, y coordinates | Row, column |

Intrinsic state lives **inside** the shared object. Extrinsic state is **passed in as a method parameter** at the moment of use.

Use it when memory is limited, objects share substantial data, and creating one is expensive.

## Example 1 — a game with a million robots

The naive version gives every robot its own sprite:

```java title="The naive approach" variant="bad"
// Create 5 lakh humanoids
for (int i = 0; i < 500000; i++) {
    Sprites humanoidSprite = new Sprites();          // a new bitmap every time
    Robot r = new Robot(x + i, y + i, "HUMANOID", humanoidSprite);
}
// and 5 lakh robotic dogs, the same way
```

10 lakh robots means 10 lakh `Sprites` objects. At roughly 40 KB each that is **40 GB** — on a 32 GB machine the application becomes unresponsive or crashes.

But there are only **two distinct sprites**. Every humanoid's bitmap is identical.

```java-sample dir="lld/patterns/structural/flyweight/robot" variant="good" title="Two sprites for a million robots" files="Sprites.java,IRobot.java,HumanoidRobot.java,RoboticDog.java,RoboticFactory.java,RoboticGameSimulation.java" run
```

Note `display(int x, int y)` — the coordinates are **parameters**, not fields. That is what allows one object to serve every robot on screen.

## Example 2 — a word processor

A document holds millions of characters, but only a few dozen distinct ones. The naive version creates an object per character position:

```java-sample dir="lld/patterns/structural/flyweight/word-processor" variant="good" title="Hello World — 11 characters, 8 objects" files="ILetter.java,DocumentCharacter.java,LetterFactory.java,WordProcessorSimulation.java" run
```

`'l'` appears three times and `'o'` twice, but each exists **once** in memory. Scale to a real document and storing `'A'` once instead of 5000 times is the difference between running and crashing.

## Participants

| Role | In this example | Responsibility |
| --- | --- | --- |
| **Flyweight** | `ILetter`, `IRobot` | Declares methods that **take** extrinsic state |
| **Concrete Flyweight** | `DocumentCharacter`, `HumanoidRobot` | Stores intrinsic state; immutable |
| **Flyweight Factory** | `LetterFactory`, `RoboticFactory` | Creates and caches flyweights; hands back the shared instance |
| **Client** | `WordProcessorSimulation` | Supplies extrinsic state on every call |

## Two rules that are not optional

**1. Flyweights must be immutable.** Every field `final`, getters only, no setters. The instance is shared by thousands of callers — if one mutates it, all of them see the change. This is the bug that makes flyweight implementations fail, and it is invisible until it isn't.

**2. Clients must go through the factory.** If a caller can reach the constructor, sharing silently stops. Note that the constructors in both samples are package-private for exactly this reason.

> **Note** — Flyweight is Factory plus a cache, plus a discipline about state. The **Factory creates**, the **cache reuses**, and the split between intrinsic and extrinsic state is what makes reuse possible at all. Getting that split right is the actual design work; the caching is mechanical.

> **Tip** — You already use this. Java interns `String` literals, so `"hello" == "hello"` is `true`. `Integer.valueOf()` caches −128 to 127, which is why `Integer.valueOf(100) == Integer.valueOf(100)` is `true` but the same comparison at 1000 is `false`. Both are Flyweight in the standard library.

> **Warning** — Flyweight buys memory and pays in **time and complexity**: a map lookup on every access, extrinsic state threaded through every call signature, and objects that can no longer carry their own context. Only worth it when the object count is genuinely large. At a thousand objects, don't — the lookup costs more than the memory saved.

> **Summary** — Flyweight separates what's shared from what varies, keeps one immutable copy of the shared part behind a caching factory, and passes the varying part in per call. Objects rendered stay in the millions while objects allocated drops to the number of genuinely distinct ones.
