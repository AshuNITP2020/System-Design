---
title: Prototype
short: Prototype
order: 40
desc: Create new objects by cloning an existing instance instead of building from scratch.
---

# Prototype

<div class="definition"><span class="lbl">Definition</span>
The Prototype Pattern creates new objects by <strong>cloning existing instances</strong> rather than constructing them from scratch.
</div>

Useful when object creation is expensive, or when you want to avoid the complexity of building an object through its constructors.

The mental image: building each type of task-specific robot from nothing is costly. Instead you keep one fully-assembled industrial robot — the **prototype** — clone it, and customise the copy for its task.

## The problem: cloning from outside the class

The obvious approach is for the client to copy the fields itself:

```java-sample dir="lld/patterns/creational/prototype/problem" variant="bad" title="The client copies fields by hand" files="Student.java,DemoProblem.java" run
```

Run it and the clone comes out with `Roll No: 0`.

**What goes wrong:**

- **Private fields are unreachable.** `rollNo` is `private`, so the client simply cannot copy it. The commented line in `DemoProblem` is a compile error — uncomment it to see. The clone is silently incomplete.
- **No control over what gets copied.** The class cannot express "copy these fields, reset those, deep-copy that list" — the client decides, and the client doesn't know the rules.
- **Every client repeats the logic.** Add a field to `Student` and every copy site in the codebase is now wrong, with nothing to flag it.

## The solution

Move the responsibility for cloning **into the class being cloned**. The object knows its own fields — including the private ones — and knows which ones should be copied.

```java-sample dir="lld/patterns/creational/prototype/solution" variant="good" title="The object clones itself" files="StudentPrototype.java,Student.java,DemoSolution.java" run
```

The interface matters as much as the method: it guarantees every prototype in the hierarchy can be cloned the same way, so a client holding a `StudentPrototype` can copy it without knowing the concrete type.

## Shallow vs. deep copy

The single most important decision, and the one the pattern makes it possible to get right:

| | What happens | When it's fine |
| --- | --- | --- |
| **Shallow copy** | Object fields are copied by *reference* — original and clone share the same nested objects | The shared fields are immutable (`String`, boxed primitives, value objects) |
| **Deep copy** | Nested objects are cloned too, recursively | The clone will be mutated independently of the original |

The `Student` above is shallow, and correct — every field is a primitive or a `String`. Add a `List<String> subjects` and it becomes a bug: mutating the clone's list would change the original's. The fix goes inside `clone()`, where it belongs:

```java title="Deep-copying a mutable field"
@Override
public StudentPrototype clone() {
    Student copy = new Student(id, name, branch, rollNo);
    copy.subjects = new ArrayList<>(this.subjects);   // deep copy, not a shared reference
    return copy;
}
```

> **Warning** — Java has a built-in `Cloneable` interface and `Object.clone()`. Avoid them. `Cloneable` is a marker interface that declares no `clone()` method, `Object.clone()` bypasses constructors, throws a checked `CloneNotSupportedException`, and does a shallow copy by default. Josh Bloch's *Effective Java* is blunt about it. A plain copy constructor or an explicit `clone()` on your own interface — as above — is clearer and safer.

> **Tip** — Prototype is at its best when the *expensive* part is the setup rather than the allocation: an object built from a slow database read, a parsed config, a warmed cache, or a deeply-nested default structure. Build it once, clone it thereafter.

> **Summary** — Prototype puts copying where the knowledge is. The class copies itself, so private state comes along, the shallow-vs-deep decision is made once by the code that understands the fields, and clients get consistent copies through a shared interface.
