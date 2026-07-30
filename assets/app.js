/* ------------------------------------------------------------------ *
 *  Page behaviour: theme, editors, search, scrollspy, shortcuts.
 *  No build step, no runtime dependencies beyond the vendored CodeMirror.
 * ------------------------------------------------------------------ */
(function () {
  'use strict';

  const $  = (s, r) => (r || document).querySelector(s);
  const $$ = (s, r) => Array.from((r || document).querySelectorAll(s));
  const root = document.documentElement;

  /* ------------------------------ theme ------------------------------ */
  const editors = [];
  function cmTheme() { return root.dataset.theme === 'dark' ? 'material-darker' : 'default'; }
  function applyTheme(t) {
    root.dataset.theme = t;
    localStorage.setItem('theme', t);
    editors.forEach(cm => cm.setOption('theme', cmTheme()));
  }
  $('#themeBtn').addEventListener('click', () =>
    applyTheme(root.dataset.theme === 'dark' ? 'light' : 'dark'));

  /* ------------------------------ mobile nav ------------------------------ */
  const menuBtn = $('#menuBtn');
  if (menuBtn) menuBtn.addEventListener('click', () => document.body.classList.toggle('nav-open'));
  $$('.sidebar a').forEach(a => a.addEventListener('click', () => document.body.classList.remove('nav-open')));

  /* ------------------------------ editors ------------------------------ */
  function mount(pane) {
    if (pane.dataset.mounted) return;
    pane.dataset.mounted = '1';
    const ta = $('.cm-src', pane);
    if (!ta) return;
    const original = ta.value;
    const cm = CodeMirror.fromTextArea(ta, {
      mode: 'text/x-java',
      theme: cmTheme(),
      lineNumbers: true,
      lineWrapping: false,
      indentUnit: 4,
      tabSize: 4,
      matchBrackets: true,
      styleActiveLine: true,
      foldGutter: true,
      gutters: ['CodeMirror-linenumbers', 'CodeMirror-foldgutter'],
      viewportMargin: Infinity,
      extraKeys: {
        'Ctrl-/': cm => cm.execCommand('toggleComment'),
        Tab: cm => cm.execCommand(cm.somethingSelected() ? 'indentMore' : 'insertSoftTab'),
      },
    });
    cm.__original = original;
    const fig = pane.closest('.sample');
    cm.on('change', () => {
      const dirty = cm.getValue() !== cm.__original;
      let flag = $('.edited-flag', fig);
      if (dirty && !flag) {
        flag = document.createElement('span');
        flag.className = 'edited-flag';
        flag.textContent = '· edited';
        $('figcaption .verdict', fig).appendChild(flag);
      } else if (!dirty && flag) flag.remove();
    });
    editors.push(cm);
    pane.__cm = cm;
    return cm;
  }

  function activeCM(fig) {
    const pane = $('.pane.active', fig);
    return pane ? (pane.__cm || mount(pane)) : null;
  }

  function flash(btn, text) {
    const old = btn.textContent;
    btn.textContent = text;
    btn.classList.add('ok');
    setTimeout(() => { btn.textContent = old; btn.classList.remove('ok'); }, 1200);
  }

  function copy(text, btn) {
    navigator.clipboard.writeText(text)
      .then(() => flash(btn, 'Copied'))
      .catch(() => flash(btn, 'Failed'));
  }

  $$('.sample').forEach(fig => {
    // mount the first visible pane straight away; the rest lazily on tab click
    const first = $('.pane.active', fig);
    if (first) mount(first);

    $$('.tab', fig).forEach(tab => tab.addEventListener('click', () => {
      const i = Number(tab.dataset.i);
      $$('.tab', fig).forEach(t => t.classList.toggle('active', t === tab));
      $$('.pane', fig).forEach((p, j) => p.classList.toggle('active', j === i));
      const pane = $$('.pane', fig)[i];
      const cm = pane.__cm || mount(pane);
      if (cm) cm.refresh();
    }));

    $$('.act', fig).forEach(btn => btn.addEventListener('click', () => {
      const act = btn.dataset.act;
      if (act === 'copy') {
        const cm = activeCM(fig);
        copy(cm ? cm.getValue() : ($('pre.static', fig) || {}).textContent || '', btn);
      } else if (act === 'copyrun') {
        copy(($('.runbar code', fig).textContent || '').trim(), btn);
      } else if (act === 'reset') {
        const cm = activeCM(fig);
        if (cm) { cm.setValue(cm.__original); flash(btn, 'Reset'); }
      } else if (act === 'expand') {
        fig.classList.toggle('expanded');
        btn.textContent = fig.classList.contains('expanded') ? 'Collapse' : 'Expand';
        const cm = activeCM(fig);
        if (cm) cm.refresh();
      }
    }));
  });

  /* ------------------------------ scrollspy ------------------------------ */
  const tocLinks = $$('.toc a');
  if (tocLinks.length) {
    const targets = tocLinks
      .map(a => ({ a, el: document.getElementById(decodeURIComponent(a.hash.slice(1))) }))
      .filter(t => t.el);
    const spy = () => {
      const y = window.scrollY + 120;
      let cur = targets[0];
      for (const t of targets) if (t.el.offsetTop <= y) cur = t;
      tocLinks.forEach(a => a.classList.toggle('active', cur && a === cur.a));
    };
    let tick = false;
    addEventListener('scroll', () => {
      if (tick) return;
      tick = true;
      requestAnimationFrame(() => { spy(); tick = false; });
    }, { passive: true });
    spy();
  }

  /* ------------------------------ search ------------------------------ */
  const q = $('#q'), results = $('#results');
  let index = null, sel = -1;

  async function loadIndex() {
    if (index) return index;
    const res = await fetch(window.SITE_ROOT + '/search-index.json');
    index = await res.json();
    return index;
  }

  function score(page, terms) {
    const title = page.title.toLowerCase();
    const heads = page.headings.join(' • ').toLowerCase();
    const text = page.text.toLowerCase();
    let s = 0, hit = '';
    for (const t of terms) {
      if (!t) continue;
      let sub = 0;
      if (page.short.toLowerCase() === t) sub += 60;
      if (title.includes(t)) sub += 30;
      if (heads.includes(t)) sub += 12;
      const n = text.split(t).length - 1;
      sub += Math.min(n, 6) * 2;
      if (!sub) return null;              // every term must appear somewhere
      s += sub;
      if (!hit) {
        const i = text.indexOf(t);
        if (i > -1) hit = page.text.slice(Math.max(0, i - 45), i + 75);
      }
    }
    return { page, s, hit };
  }

  function render(list) {
    if (!list.length) { results.innerHTML = '<div class="r-empty">No matches</div>'; results.hidden = false; return; }
    results.innerHTML = list.slice(0, 8).map((r, i) =>
      `<a href="${window.SITE_ROOT}/${r.page.url}" class="${i === 0 ? 'sel' : ''}">
         <div class="r-title">${r.page.title}</div>
         <div class="r-sub">${(r.hit || r.page.desc || '').replace(/[<>]/g, '').slice(0, 110)}…</div>
       </a>`).join('');
    results.hidden = false;
    sel = 0;
  }

  if (q) {
    q.addEventListener('input', async () => {
      const v = q.value.trim().toLowerCase();
      if (v.length < 2) { results.hidden = true; return; }
      const terms = v.split(/\s+/);
      const idx = await loadIndex();
      render(idx.map(p => score(p, terms)).filter(Boolean).sort((a, b) => b.s - a.s));
    });
    q.addEventListener('keydown', e => {
      const items = $$('a', results);
      if (e.key === 'Escape') { results.hidden = true; q.blur(); }
      else if (e.key === 'ArrowDown' || e.key === 'ArrowUp') {
        e.preventDefault();
        if (!items.length) return;
        sel = (sel + (e.key === 'ArrowDown' ? 1 : -1) + items.length) % items.length;
        items.forEach((a, i) => a.classList.toggle('sel', i === sel));
        items[sel].scrollIntoView({ block: 'nearest' });
      } else if (e.key === 'Enter' && items[sel]) { location.href = items[sel].href; }
    });
    document.addEventListener('click', e => { if (!e.target.closest('.search')) results.hidden = true; });
  }

  /* ------------------------------ shortcuts ------------------------------ */
  addEventListener('keydown', e => {
    const typing = /^(INPUT|TEXTAREA)$/.test(e.target.tagName) || e.target.closest('.CodeMirror');
    if (e.key === '/' && !typing) { e.preventDefault(); q && q.focus(); }
    if (e.key.toLowerCase() === 't' && !typing && !e.metaKey && !e.ctrlKey)
      applyTheme(root.dataset.theme === 'dark' ? 'light' : 'dark');
  });
})();
