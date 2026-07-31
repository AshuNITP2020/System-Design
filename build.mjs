/*
 * Static site generator for the notes.
 *
 *   node build.mjs            build content/ -> site/
 *
 * What it does
 *   - reads every .md under content/, with simple `key: value` front matter
 *   - builds the sidebar from the directory layout (section / group / page)
 *   - turns fenced code into CodeMirror editors
 *   - resolves `java-sample` blocks against real files in code/, so a snippet
 *     in the notes and the file you actually run are never out of sync
 *   - emits a client-side search index
 */
import { readFileSync, writeFileSync, mkdirSync, readdirSync, statSync, existsSync, cpSync, rmSync } from 'node:fs';
import { join, dirname, relative, basename } from 'node:path';
import { fileURLToPath } from 'node:url';
import { marked } from 'marked';

const ROOT = dirname(fileURLToPath(import.meta.url));
const CONTENT = join(ROOT, 'content');
const CODE = join(ROOT, 'code');
const SITE = join(ROOT, 'site');
const CONFIG = JSON.parse(readFileSync(join(ROOT, 'site.config.json'), 'utf8'));

const esc = s => String(s).replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;');
const unesc = s => String(s).replace(/&quot;/g, '"').replace(/&#39;/g, "'").replace(/&lt;/g, '<')
                            .replace(/&gt;/g, '>').replace(/&amp;/g, '&');
const slug = s => unesc(String(s).replace(/<[^>]+>/g, ''))
  .toLowerCase().replace(/[^\w\s-]/g, '').trim().replace(/\s+/g, '-');

/* ------------------------------------------------------------------ */
/* front matter                                                        */
/* ------------------------------------------------------------------ */
function parseFrontMatter(raw) {
  const m = raw.match(/^---\r?\n([\s\S]*?)\r?\n---\r?\n?/);
  if (!m) return { data: {}, body: raw };
  const data = {};
  for (const line of m[1].split(/\r?\n/)) {
    const kv = line.match(/^([\w-]+)\s*:\s*(.*)$/);
    if (kv) data[kv[1]] = kv[2].replace(/^["']|["']$/g, '').trim();
  }
  return { data, body: raw.slice(m[0].length) };
}

/* ------------------------------------------------------------------ */
/* code widgets                                                        */
/* ------------------------------------------------------------------ */
let widgetId = 0;

function parseAttrs(info) {
  const attrs = {};
  const re = /([\w-]+)(?:=(?:"([^"]*)"|'([^']*)'|([^\s]+)))?/g;
  let m;
  while ((m = re.exec(info)) !== null) attrs[m[1]] = m[2] ?? m[3] ?? m[4] ?? true;
  return attrs;
}

// Default captions are deliberately neutral so they read correctly on a SOLID
// page and a design-pattern page alike. Override per block with verdict="...".
const VERDICT = {
  bad:  { cls: 'bad',  label: '✗ Problem' },
  good: { cls: 'good', label: '✓ Solution' },
  neutral: { cls: 'plain', label: '' },
};

/* In print mode every file is stacked and static - no editors, no tabs. */
let printMode = false;

function editorPane(name, source, active) {
  if (printMode) {
    return `<div class="pane print"><div class="fname">${esc(name)}</div>` +
           `<pre class="static"><code>${esc(source)}</code></pre></div>`;
  }
  return `<div class="pane${active ? ' active' : ''}" data-file="${esc(name)}">` +
         `<textarea class="cm-src">${esc(source)}</textarea></div>`;
}

/** Multi-file sample pulled straight out of code/ */
function javaSample(attrs) {
  const dir = attrs.dir;
  const abs = join(CODE, dir || '');
  if (!dir || !existsSync(abs)) {
    console.warn(`  ! java-sample: missing directory code/${dir}`);
    return `<div class="sample-error">Missing sample: <code>code/${esc(dir || '?')}</code></div>`;
  }
  let files = attrs.files
    ? String(attrs.files).split(',').map(s => s.trim())
    : readdirSync(abs).filter(f => f.endsWith('.java')).sort();

  const missing = files.filter(f => !existsSync(join(abs, f)));
  if (missing.length) console.warn(`  ! java-sample ${dir}: missing ${missing.join(', ')}`);
  files = files.filter(f => existsSync(join(abs, f)));

  const v = VERDICT[attrs.variant] || VERDICT.neutral;
  const label = attrs.verdict === true ? v.label : (attrs.verdict || v.label);
  const id = `s${++widgetId}`;
  const tabs = printMode ? '' : files.map((f, i) =>
    `<button class="tab${i === 0 ? ' active' : ''}" data-i="${i}" type="button">${esc(f)}</button>`).join('');
  const panes = files.map((f, i) => editorPane(f, readFileSync(join(abs, f), 'utf8'), i === 0)).join('');
  const runCmd = attrs.run === true ? dir : attrs.run;
  const note = attrs.note ? `<p class="sample-note">${esc(attrs.note)}</p>` : '';

  return `
<figure class="sample ${v.cls}" id="${id}">
  <figcaption>
    <span class="verdict">${label ? esc(label) : ''}${attrs.title ? `<em>${esc(attrs.title)}</em>` : ''}</span>
    <span class="actions">
      <button class="act" data-act="copy"  type="button" title="Copy this file">Copy</button>
      <button class="act" data-act="reset" type="button" title="Undo your edits">Reset</button>
      <button class="act" data-act="expand" type="button" title="Show all lines">Expand</button>
    </span>
  </figcaption>
  <div class="tabs" role="tablist">${tabs}<span class="dir">code/${esc(dir)}/</span></div>
  <div class="panes">${panes}</div>
  ${runCmd ? `<div class="runbar"><span class="play">▸</span> run it: <code>./run.sh ${esc(runCmd)}</code>
      <button class="act ghost" data-act="copyrun" type="button">Copy</button></div>` : ''}
</figure>${note}`;
}

/** Single inline snippet written directly in the markdown */
function inlineCode(code, attrs, lang) {
  const v = VERDICT[attrs.variant] || VERDICT.neutral;
  const label = attrs.verdict === true ? v.label : (attrs.verdict || v.label);
  const id = `s${++widgetId}`;
  const title = attrs.title || (lang === 'java' ? 'Java' : lang || 'text');
  if (lang && lang !== 'java') {
    return `<figure class="sample plain" id="${id}"><figcaption><span class="verdict"><em>${esc(title)}</em></span>
      <span class="actions"><button class="act" data-act="copy" type="button">Copy</button></span></figcaption>
      <pre class="static"><code>${esc(code)}</code></pre></figure>`;
  }
  return `
<figure class="sample ${v.cls}" id="${id}">
  <figcaption>
    <span class="verdict">${label ? esc(label) : ''}<em>${esc(title)}</em></span>
    <span class="actions">
      <button class="act" data-act="copy"  type="button">Copy</button>
      <button class="act" data-act="reset" type="button">Reset</button>
      <button class="act" data-act="expand" type="button">Expand</button>
    </span>
  </figcaption>
  <div class="panes">${editorPane(title, code, true)}</div>
</figure>`;
}

/* ------------------------------------------------------------------ */
/* markdown renderer                                                   */
/* ------------------------------------------------------------------ */
const headings = [];

marked.use({
  gfm: true,
  renderer: {
    code(a, b) {
      // marked changed this signature across majors - accept both shapes
      const code = typeof a === 'object' ? a.text : a;
      const info = (typeof a === 'object' ? a.lang : b) || '';
      const [lang, ...rest] = info.split(/\s+/);
      const attrs = parseAttrs(rest.join(' '));
      if (lang === 'java-sample') return javaSample(attrs);
      return inlineCode(code, attrs, lang);
    },
    heading(a, b) {
      const token = typeof a === 'object' ? a : null;
      const text = token ? this.parser.parseInline(token.tokens) : a;
      const depth = token ? token.depth : b;
      const id = slug(text);
      if (depth <= 3) headings.push({ id, text: unesc(text.replace(/<[^>]+>/g, '')), depth });
      return `<h${depth} id="${id}">${text}<a class="anchor" href="#${id}" aria-label="Link to this section">#</a></h${depth}>\n`;
    },
    blockquote(a) {
      const inner = typeof a === 'object' ? this.parser.parse(a.tokens) : a;
      // > **Note** / **Tip** / **Warning** / **Pitfall** at the start becomes a callout
      const m = inner.match(/^<p><strong>(Note|Tip|Warning|Pitfall|Summary|Key idea)<\/strong>\s*[:—-]?\s*/i);
      if (m) {
        const kind = m[1].toLowerCase();
        const cls = ({ tip: 'tip', warning: 'warn', pitfall: 'warn', summary: 'sum' })[kind] || 'note';
        return `<div class="callout ${cls}"><span class="lbl">${esc(m[1])}</span>${inner.slice(0, m.index) + inner.slice(m.index + m[0].length).replace(/^/, '<p>')}</div>\n`;
      }
      return `<blockquote>${inner}</blockquote>\n`;
    },
    table(a, b) {
      // marked v12 passes (headerHtml, bodyHtml); v13+ passes a single token
      const inner = (a && typeof a === 'object' && a.header)
        ? renderTableToken.call(this, a)
        : `<thead>${a || ''}</thead><tbody>${b || ''}</tbody>`;
      return `<div class="tablewrap"><table>${inner}</table></div>\n`;
    },
  },
});

// marked v12 passes header/body strings to table(); v13+ passes a token.
function renderTableToken(token) {
  const th = token.header.map((c, i) =>
    `<th${token.align[i] ? ` align="${token.align[i]}"` : ''}>${this.parser.parseInline(c.tokens)}</th>`).join('');
  const rows = token.rows.map(r =>
    `<tr>${r.map((c, i) => `<td${token.align[i] ? ` align="${token.align[i]}"` : ''}>${this.parser.parseInline(c.tokens)}</td>`).join('')}</tr>`).join('');
  return `<thead><tr>${th}</tr></thead><tbody>${rows}</tbody>`;
}

/* ------------------------------------------------------------------ */
/* collect pages                                                       */
/* ------------------------------------------------------------------ */
function walk(dir, out = []) {
  for (const e of readdirSync(dir)) {
    const p = join(dir, e);
    if (statSync(p).isDirectory()) walk(p, out);
    else if (e.endsWith('.md')) out.push(p);
  }
  return out;
}

function groupMeta(dirParts) {
  const f = join(CONTENT, ...dirParts, '_group.json');
  return existsSync(f) ? JSON.parse(readFileSync(f, 'utf8')) : {};
}

const pages = walk(CONTENT).map(file => {
  const rel = relative(CONTENT, file).replace(/\\/g, '/');
  const { data, body } = parseFrontMatter(readFileSync(file, 'utf8'));
  const parts = rel.replace(/\.md$/, '').split('/');
  const isIndex = parts[parts.length - 1] === 'index';
  return {
    file, rel, body, isIndex,
    url: parts.join('/') + '.html',
    dirParts: parts.slice(0, -1),
    title: data.title || basename(file, '.md'),
    short: data.short || data.title || basename(file, '.md'),
    // an index page is the overview of its directory, so it leads by default
    order: Number(data.order ?? (isIndex ? -1 : 999)),
    desc: data.desc || '',
    sectionId: parts.length > 1 ? parts[0] : '',
  };
});

/* Sidebar tree, nested to whatever depth content/ actually uses:
   section -> group -> subgroup -> ... -> pages. */
function buildNode(dirParts, secId) {
  const at = dirParts.length;
  const key = dirParts.join('/');
  const inSection = pages.filter(p => p.sectionId === secId);
  const under = inSection.filter(p => p.dirParts.slice(0, at).join('/') === key);

  const own = under.filter(p => p.dirParts.length === at).sort((a, b) => a.order - b.order);
  const childIds = [...new Set(under.filter(p => p.dirParts.length > at).map(p => p.dirParts[at]))];

  const children = childIds.map(id => {
    const cp = [...dirParts, id];
    const meta = groupMeta(cp);
    return { id, label: meta.label || id, order: Number(meta.order ?? 999), ...buildNode(cp, secId) };
  }).sort((a, b) => a.order - b.order);

  return { pages: own, children };
}

const tree = CONFIG.sections
  .filter(sec => pages.some(p => p.sectionId === sec.id))
  .map(sec => ({ ...sec, ...buildNode([sec.id], sec.id) }));

/* flat reading order (depth-first), for prev/next */
function flatten(node, out = []) {
  out.push(...node.pages);
  for (const c of node.children) flatten(c, out);
  return out;
}
const flat = tree.flatMap(s => flatten(s));
const home = pages.find(p => p.url === 'index.html');
const ordered = [home, ...flat.filter(p => p !== home)].filter(Boolean);

/* ------------------------------------------------------------------ */
/* render                                                              */
/* ------------------------------------------------------------------ */
const template = readFileSync(join(ROOT, 'templates', 'page.html'), 'utf8');
const depthOf = url => url.split('/').length - 1;
const rootRel = url => depthOf(url) === 0 ? '.' : Array(depthOf(url)).fill('..').join('/');

function holds(node, url) {
  return node.pages.some(p => p.url === url) || node.children.some(c => holds(c, url));
}

function renderNode(node, current, R, depth) {
  const link = p => `<a class="nav-link${p.url === current.url ? ' active' : ''}" href="${R}/${p.url}">`
    + `${p.short !== p.title ? `<span class="badge">${esc(p.short)}</span>` : ''}<span>${esc(p.title)}</span></a>`;

  let html = node.pages.map(link).join('');
  for (const c of node.children) {
    const open = holds(c, current.url);
    html += `<details class="nav-group lvl-${depth}"${open ? ' open' : ''}>`
      + `<summary>${esc(c.label)}</summary>`
      + `<div class="nav-children">${renderNode(c, current, R, depth + 1)}</div>`
      + `</details>`;
  }
  return html;
}

function sidebar(current) {
  const R = rootRel(current.url);
  return tree.map(s =>
    `<div class="nav-section"><div class="nav-sec-label">${esc(s.label)}</div>`
    + renderNode(s, current, R, 0) + `</div>`).join('');
}

rmSync(SITE, { recursive: true, force: true });
mkdirSync(SITE, { recursive: true });
cpSync(join(ROOT, 'assets'), join(SITE, 'assets'), { recursive: true });
cpSync(join(ROOT, 'vendor'), join(SITE, 'vendor'), { recursive: true });

const searchIndex = [];

for (const page of ordered) {
  headings.length = 0;
  widgetId = 0;
  const content = marked.parse(page.body);
  const R = rootRel(page.url);
  const i = ordered.indexOf(page);
  const prev = ordered[i - 1], next = ordered[i + 1];

  const toc = headings.filter(h => h.depth === 2 || h.depth === 3)
    .map(h => `<a class="toc-${h.depth}" href="#${h.id}">${esc(h.text)}</a>`).join('');

  const nav = [
    prev ? `<a class="pn prev" href="${R}/${prev.url}"><span>Previous</span>${esc(prev.title)}</a>` : '<span></span>',
    next ? `<a class="pn next" href="${R}/${next.url}"><span>Next</span>${esc(next.title)}</a>` : '<span></span>',
  ].join('');

  const html = template
    .replace(/\{\{ROOT\}\}/g, R)
    .replace(/\{\{TITLE\}\}/g, esc(page.title))
    .replace(/\{\{SITE_TITLE\}\}/g, esc(CONFIG.title))
    .replace(/\{\{DESC\}\}/g, esc(page.desc))
    .replace(/\{\{SIDEBAR\}\}/g, sidebar(page))
    .replace(/\{\{TOC\}\}/g, toc ? `<div class="toc-inner"><div class="toc-label">On this page</div>${toc}</div>` : '')
    .replace(/\{\{CONTENT\}\}/g, content)
    .replace(/\{\{PAGENAV\}\}/g, nav)
    .replace(/\{\{CREDIT\}\}/g, esc(CONFIG.credit || ''));

  const out = join(SITE, page.url);
  mkdirSync(dirname(out), { recursive: true });
  writeFileSync(out, html);

  const plain = content.replace(/<textarea[\s\S]*?<\/textarea>/g, ' ').replace(/<[^>]+>/g, ' ').replace(/\s+/g, ' ').trim();
  searchIndex.push({ url: page.url, title: page.title, short: page.short, desc: page.desc,
                     headings: headings.map(h => h.text), text: plain.slice(0, 30000) });
  console.log(`  ${page.url}`);
}

// Emitted as JS, not JSON: a <script> tag works from file:// where fetch() is
// blocked by CORS, so search keeps working when you just open site/index.html.
writeFileSync(join(SITE, 'search-index.js'), `window.SEARCH_INDEX=${JSON.stringify(searchIndex)};`);

/* ------------------------------------------------------------------ */
/* print.html - every page in one document, for Ctrl-P / tools/pdf.sh  */
/* ------------------------------------------------------------------ */
printMode = true;
const printBody = ordered.map((page, i) => {
  widgetId = 0;
  return `<section class="print-page${i ? ' brk' : ''}">${marked.parse(page.body)}</section>`;
}).join('\n');

writeFileSync(join(SITE, 'print.html'), `<!DOCTYPE html>
<html lang="en" data-theme="light"><head>
<meta charset="utf-8"><title>${esc(CONFIG.title)}</title>
<link rel="stylesheet" href="assets/styles.css">
<style>
  @page { size: A4; margin: 15mm 14mm; }
  body { font-size: 10.5pt; }
  .print-page { max-width: none; padding: 0 0 8mm; }
  .print-page.brk { break-before: page; }
  .prose, .sample { max-width: none; }
  .sample .pane.print { display: block; border-top: 1px solid var(--line); }
  .sample .pane.print:first-child { border-top: 0; }
  .sample .fname { font-family: var(--mono); font-size: 10px; padding: 4px 12px;
                   color: var(--ink-faint); background: var(--bg-soft); border-bottom: 1px solid var(--line-soft); }
  .sample pre.static { font-size: 8pt; line-height: 1.45; white-space: pre-wrap; orphans: 4; widows: 4; }
  .sample .tabs { display: none; }
  .sample { break-inside: auto; }
  .sample figcaption { break-after: avoid; }
  h1, h2, h3 { break-after: avoid; }
  .anchor { display: none; }
</style></head>
<body><main><article class="prose">${printBody}</article>
<footer class="foot">${esc(CONFIG.credit || '')}</footer></main></body></html>`);

console.log(`\nbuilt ${ordered.length} pages + print.html -> site/`);
