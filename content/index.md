---
title: LLD & HLD Notes
short: Home
order: 0
desc: Working notes on low-level and high-level system design, with runnable Java samples.
---

# LLD & HLD Notes

<p class="lede">Working notes on software design — kept as markdown, published as a searchable site, and backed by Java samples that actually compile and run.</p>

> **Note** — These notes follow **Shrayansh Jain's Low Level Design series** (*Concept && Coding*). The structure, examples and terminology come from that course; the detection heuristics, trade-off sections, principle-interaction notes and the runnable code were added while studying, so treat those as my own commentary rather than course material.

## What's here

| Section | Status |
| --- | --- |
| [SOLID Principles](lld/solid/index.html) | Complete — 5 principles, 13 runnable samples |
| Design Patterns | Planned |
| UML & class modelling | Planned |
| Machine-coding problems | Planned |
| High Level Design | Planned — see the [HLD section](hld/index.html) |

## How to use this

**Read it in the browser.** Every page has an "on this page" outline on the right, prev/next links at the bottom, and a search box in the header (press <kbd>/</kbd> to focus it, <kbd>t</kbd> to flip between light and dark).

**Edit the code as you read.** Every Java block is a real editor — type in it, break it, see what you think would happen. *Reset* puts it back. Nothing is saved, so experiment freely.

**Then actually run it.** Each sample maps to a directory under `code/`, and the `▸ run it` bar under a sample gives you the exact command:

```bash
./run.sh lld/solid/srp/violation      # compile and run one sample
./run.sh --list                       # every runnable sample
./run.sh --all                        # compile everything (a cheap sanity check)
```

Some *violation* samples throw on purpose — a `Bicycle` with no engine, a `null` where the caller expected a `Boolean`. Running them and reading the stack trace is the point; a non-zero exit code there is expected, not a bug.

## Adding a new topic

The site is generated from `content/`, so adding notes is just adding markdown:

```bash
mkdir -p content/lld/design-patterns
echo '{ "label": "Design Patterns", "order": 20 }' > content/lld/design-patterns/_group.json
$EDITOR content/lld/design-patterns/strategy.md
npm run build
```

Front matter drives the sidebar:

```text
---
title: Strategy Pattern
short: Strategy          # optional short badge in the sidebar
order: 10                # position within the group
desc: One-line summary, used by search
---
```

To show code, point at a directory under `code/` rather than pasting a snippet — that way the notes and the code you run can never drift apart:

````text
```java-sample dir="lld/design-patterns/strategy" variant="good" title="Strategy" files="PaymentMethod.java,CardPayment.java,Demo.java" run
```
````

`variant` is `bad`, `good` or omitted; `files` is optional (defaults to every `.java` in the directory, alphabetically); `run` adds the run-it bar.

`README.md` in the project root has the full layout.
