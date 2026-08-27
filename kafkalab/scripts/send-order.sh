#!/usr/bin/env bash
# Send one order. Usage: ./scripts/send-order.sh [port] [orderId] [userId]
set -euo pipefail
PORT="${1:-8080}"
ORDER_ID="${2:-order-$RANDOM}"
USER_ID="${3:-user-7}"

curl -sS -w '\nHTTP %{http_code} in %{time_total}s\n' \
  -X POST "http://localhost:${PORT}/orders" \
  -H 'Content-Type: application/json' \
  -d "{\"orderId\":\"${ORDER_ID}\",\"userId\":\"${USER_ID}\"}"
