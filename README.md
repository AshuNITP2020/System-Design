# LLD · HLD Notes

Working notes on low-level and high-level software design. Markdown in, searchable static site out, with Java samples that actually compile and run.

**Currently covered:** SOLID principles (5 chapters + synthesis + practice), 13 runnable samples.

---

## Quick start

```bash
npm install          # one dependency: marked
npm run build        # content/ -> site/
npm run serve        # build + serve at http://localhost:8080
npm run pdf          # build + dist/LLD-HLD-Notes.pdf (needs Chrome/Chromium)
```

Or just open `site/index.html` in a browser — the site is fully static and works from `file://`.

### Running the code

```bash
./run.sh --list                       # every runnable sample
./run.sh lld/solid/srp/violation      # compile + run one
./run.sh --all                        # compile everything (sanity check)
./run.sh --clean                      # drop build/
```

Requires a JDK on `PATH` (developed against JDK 21). Samples are plain `.java` files in the default package — no Maven, no Gradle, nothing to configure.

> Some **violation** samples throw on purpose (`AssertionError`, `NullPointerException`). That crash is the lesson; a non-zero exit code there is expected.

---

## Layout

```
content/                 the notes (markdown) - this is what you edit
  index.md                 site home
  lld/
    solid/
      _group.json          sidebar label + order for the group
      index.md  srp.md  ocp.md  lsp.md  isp.md  dip.md
      together.md  practice.md
  hld/
    index.md

code/                    Java samples, mirroring the content tree
  lld/solid/srp/violation/*.java
  lld/solid/srp/refactored/*.java
  ...

assets/                  styles.css, app.js  (copied into site/)
vendor/codemirror/       vendored editor - no CDN at runtime
templates/page.html      the page shell
tools/pdf.sh             site -> single PDF
build.mjs                the generator (~250 lines, no framework)
run.sh                   compile + run a sample

site/                    generated - gitignored
build/                   compiled .class files - gitignored
dist/                    generated PDF - gitignored
```

The **content tree drives the sidebar**: `content/<section>/<group>/<page>.md`. Sections are declared in `site.config.json`; groups get their label and ordering from a `_group.json` in the directory.

---

## Adding a topic

```bash
mkdir -p content/lld/design-patterns
echo '{ "label": "Design Patterns", "order": 20 }' > content/lld/design-patterns/_group.json
$EDITOR content/lld/design-patterns/strategy.md
npm run build
```

Front matter controls the sidebar entry and search:

```markdown
---
title: Strategy Pattern      # page title and sidebar text
short: Strategy              # optional badge in the sidebar
order: 10                    # position within the group
desc: One-line summary       # shown in search results
---
```

### Showing code

Point at a directory under `code/` instead of pasting a snippet — the notes and the code you run then can't drift apart:

````markdown
```java-sample dir="lld/solid/srp/violation" variant="bad" title="Invoice does three jobs" files="Marker.java,Invoice.java,Demo.java" run
```
````

| Attribute | Meaning |
| --- | --- |
| `dir` | **Required.** Directory under `code/`. |
| `files` | Comma-separated, in the order you want the tabs. Defaults to every `.java` in the directory, alphabetically. |
| `variant` | `bad` (red) or `good` (green). Omit for neutral. |
| `title` | Caption text next to the verdict. |
| `run` | Adds the "▸ run it" bar. Bare `run` uses `dir`; `run="some/other/path"` overrides it. |
| `note` | Small italic note under the block. |

A plain fenced block still works for one-off snippets that don't need to be runnable:

````markdown
```java variant="bad" title="Type-switching"
if (type.equals("CARD")) { ... }
```
````

### Callouts

A blockquote starting with a bold keyword becomes a styled callout — `Note`, `Tip`, `Warning`, `Pitfall`, `Summary`, `Key idea`:

```markdown
> **Warning** — SRP does not mean "one method per class".
```

For the definition boxes at the top of a principle page, drop in raw HTML:

```html
<div class="definition"><span class="lbl">Definition</span>
A class should have <strong>only ONE reason to change</strong>.
</div>
```

---

## Site features

- **Search** — press <kbd>/</kbd>, arrow keys to navigate, <kbd>Enter</kbd> to open. Index is built at build time.
- **Theme** — light/dark toggle in the header, or press <kbd>t</kbd>. Persisted in `localStorage`.
- **Editable code** — every Java block is a real CodeMirror editor. Type in it, break it, hit *Reset*. Nothing is saved.
- **Per-file tabs** — multi-file samples show one tab per class.
- **Fold / expand** — click the gutter arrows to fold a method; *Expand* removes the height cap on long files.
- **On-this-page outline** with scroll tracking, plus prev/next links at the foot of each page.
- **Offline** — CodeMirror is vendored into `vendor/`. Nothing is fetched at runtime.

---

## Publishing later

The repo is local-only for now. To put it on GitHub Pages:

```bash
git remote add origin git@github.com:<you>/lld-hld-notes.git
git push -u origin main
```

Then either commit `site/` (drop it from `.gitignore` and point Pages at it), or add a workflow that runs `npm ci && npm run build` and deploys `site/`.

---

## Credits

The SOLID material follows **Shrayansh Jain's Low Level Design series** (*Concept && Coding*) — the structure, the worked examples (`Marker`/`Invoice`, `Bike`/`Bicycle`, `RestaurantEmployee`, `MacBook`) and the terminology come from that course.

Added while studying, and therefore my own commentary rather than course material: the actor-based framing of SRP, the four LSP contract rules, the DIP vs. DI vs. IoC distinction, detection heuristics, the over-engineering chapter, the code-smell diagnostic table, the combined end-to-end example, and all of the runnable code.

Further reading is listed at the end of [Applying SOLID Without Over-Engineering](content/lld/solid/practice.md).
