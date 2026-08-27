#!/usr/bin/env bash
# Fire N orders concurrently and time the whole batch.
# Usage: ./scripts/burst.sh [count] [port] [concurrency]
set -euo pipefail
COUNT="${1:-50}"
PORT="${2:-8080}"
CONC="${3:-10}"

echo "firing ${COUNT} orders at :${PORT} with concurrency ${CONC}"
START=$(date +%s.%N)

seq 1 "${COUNT}" | xargs -P "${CONC}" -I{} \
  curl -sS -o /dev/null -w '%{http_code} %{time_total}\n' \
    -X POST "http://localhost:${PORT}/orders" \
    -H 'Content-Type: application/json' \
    -d '{"orderId":"burst-{}","userId":"user-{}"}' \
  | sort | uniq -c | sort -rn

END=$(date +%s.%N)
echo "wall clock: $(echo "$END - $START" | bc)s"
