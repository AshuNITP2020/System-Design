#!/usr/bin/env bash
# Render the whole notes site to a single PDF.
#
#   npm run pdf              -> dist/LLD-HLD-Notes.pdf
#
# Uses headless Chrome against site/print.html, which the build emits with
# every page concatenated and code shown as static, non-editable blocks.

set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
OUT="$ROOT/dist/LLD-HLD-Notes.pdf"

CHROME=""
for c in google-chrome google-chrome-stable chromium chromium-browser \
         "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome"; do
  if command -v "$c" >/dev/null 2>&1 || [ -x "$c" ]; then CHROME="$c"; break; fi
done
if [ -z "$CHROME" ]; then
  echo "No Chrome/Chromium found. Open site/print.html in a browser and print to PDF instead." >&2
  exit 1
fi

[ -f "$ROOT/site/print.html" ] || { echo "site/print.html missing - run 'npm run build' first." >&2; exit 1; }

mkdir -p "$ROOT/dist"
"$CHROME" --headless=new --disable-gpu --no-sandbox --no-pdf-header-footer \
  --run-all-compositor-stages-before-draw --virtual-time-budget=15000 \
  --print-to-pdf="$OUT" "file://$ROOT/site/print.html" 2>&1 | grep -v '^\[' || true

if [ -f "$OUT" ]; then
  echo "wrote $OUT"
  command -v pdfinfo >/dev/null && pdfinfo "$OUT" | grep -i '^Pages'
else
  echo "PDF generation failed" >&2; exit 1
fi
