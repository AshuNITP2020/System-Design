---
title: Composite
short: Composite
order: 30
desc: Compose objects into trees and let clients treat individual objects and whole trees the same way.
---

# Composite

<div class="definition"><span class="lbl">Definition</span>
The Composite Pattern composes objects into <strong>tree structures</strong> representing <strong>part-whole hierarchies</strong>, letting clients treat individual objects (leaves) and compositions of objects (composites) <strong>uniformly</strong> — without knowing which one they are holding.
</div>

## The problem

A file system built the obvious way: a `MyFile` class, a `Directory` class, and no relationship between them.

```java-sample dir="lld/patterns/structural/composite/problem" variant="bad" title="No common type, so everything is instanceof" files="MyFile.java,Directory.java,Client.java" run
```

**What goes wrong:**

- **No common abstraction.** `MyFile` and `Directory` are unrelated types, so the list must be `List<Object>` and every traversal has to ask what each element is.
- **Breaks [OCP](../../solid/ocp.html).** Add a `Shortcut` or a `ZipFolder` and you edit `printContents()` — and every other method that walks the tree, in every class that walks it.
- **Doesn't scale.** Those `instanceof` chains multiply: one per operation per traversal site. The system becomes rigid and tightly coupled.

## The solution

Give leaf and composite the **same interface**. A directory's children are then just components — some of which happen to be directories, and the client never has to care.

```java-sample dir="lld/patterns/structural/composite/solution" variant="good" title="One interface for leaf and composite" files="FileSystemComponent.java,MyFile.java,Directory.java,FileSystemDemo.java" run
```

`printContents()` on `Directory` is now three lines with no `instanceof` and no cast. The recursion is implicit: a child that is a directory prints its own children, all the way down.

Adding `ZipFolder` means implementing `FileSystemComponent` — **no client code changes at all**.

## Participants

| Role | In this example | Responsibility |
| --- | --- | --- |
| **Component** | `FileSystemComponent` | The interface common to leaves and composites |
| **Leaf** | `MyFile` | An end node — no children |
| **Composite** | `Directory` | Holds children (themselves Components) and delegates to them |
| **Client** | `FileSystemDemo` | Works through the Component interface only |

## A second example: expression trees

The same shape in a completely different domain. A number is a leaf; an operation is a composite holding two child expressions — each of which may itself be a number or another operation:

```java-sample dir="lld/patterns/structural/composite/expression" variant="good" title="Evaluating 2 * (1 + 7)" files="ArithmeticExpression.java,OperationType.java,Numeral.java,Expression.java,MathExpressionEvaluator.java" run
```

```text title="The tree being evaluated"
        *
       / \
      2   +
         / \
        1   7
```

`parentExpression.evaluate()` is one call. The recursion walks the whole tree, and the client never distinguishes a literal from a sub-expression. This is how interpreters, query planners and rules engines are built.

## Where it shows up

1. **File systems** — files and directories
2. **Organisational charts** — an employee and a department of employees
3. **Mathematical expressions** — literals and operations
4. **UI toolkits** — a button and a panel containing buttons
5. **Drawing / CAD** — a shape and a group of shapes

The tell is always the same: *"this thing, or a collection of these things, treated the same way."*

> **Warning** — The classic design tension: where do `add()` and `remove()` go? Put them on the **Component** and every leaf inherits methods it cannot honour — a file with `add()` must throw, which breaks [LSP](../../solid/lsp.html) and [ISP](../../solid/isp.html). Put them on the **Composite** only — as done here — and safety is preserved, but a client holding a `FileSystemComponent` must downcast to add a child. GoF calls this *transparency vs. safety*. Prefer safety: keep child management on the composite.

> **Tip** — Composite and [Decorator](decorator.html) are structurally the same idea with different arity. **Composite holds many children and forms a tree; Decorator holds exactly one and forms a chain.** If you find yourself with a composite that always has exactly one child, you have written a decorator.

> **Summary** — Composite trades a pile of `instanceof` checks for one shared interface. Leaves and composites become interchangeable, traversal becomes ordinary recursion, and new node types can be added without touching a single client.
