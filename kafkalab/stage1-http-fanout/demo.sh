#!/usr/bin/env bash
# ONE COMMAND to see stage 1. Starts everything, runs the experiments, prints a verdict,
# tears it all down again.
#
#   ./kafkalab/stage1-http-fanout/demo.sh
#
# You do not need to start anything first, and nothing is left running afterwards.
set -uo pipefail
cd "$(dirname "$0")/../.."          # repo root, where gradlew lives

ORDERS="${ORDERS:-12}"
NAMES=(payment inventory email analytics)
PORTS=(9001    9002      9003  9004)

R=$'\033[31m'; G=$'\033[32m'; Y=$'\033[33m'; B=$'\033[1m'; N=$'\033[0m'
pids=()

# -sTCP:LISTEN matters. Plain `lsof -ti:9002` matches ANY process holding a socket on that
# port — including the producer, because HttpClient pools keep-alive connections to it. Without
# this filter, "kill inventory" also kills the producer, and the demo reports HTTP 000.
cleanup() {
  [[ ${#pids[@]} -gt 0 ]] && kill "${pids[@]}" 2>/dev/null
  lsof -ti:8080,9001,9002,9003,9004 -sTCP:LISTEN 2>/dev/null | xargs -r kill 2>/dev/null
  return 0
}
trap cleanup EXIT INT TERM

applied() { curl -s "localhost:$1/stats" 2>/dev/null \
    | python3 -c 'import sys,json;print(json.load(sys.stdin)["distinctApplied"])' 2>/dev/null || echo 0; }
failures() { curl -s "localhost:$1/stats" 2>/dev/null \
    | python3 -c 'import sys,json;print(json.load(sys.stdin)["failures"])' 2>/dev/null || echo 0; }
wait_up() { for _ in $(seq 1 60); do curl -sf "localhost:$1/stats" >/dev/null 2>&1 && return 0; sleep 0.5; done; return 1; }
order()   { curl -s -o /dev/null -w '%{http_code}' -X POST localhost:8080/orders \
              -H 'Content-Type: application/json' -d "{\"orderId\":\"$1\",\"userId\":\"u\"}"; }

echo
echo "${B}=== STAGE 1: HTTP fan-out ===${N}"
echo

# ---------------------------------------------------------------- 1. build + start
echo "${B}[1/4]${N} building and starting 5 processes..."
cleanup
CP="$(./gradlew -q --console=plain :kafkalab:stage1-http-fanout:printClasspath 2>/dev/null | tail -1)"
[[ -z "$CP" ]] && { echo "${R}gradle build failed${N}"; exit 1; }

for i in "${!NAMES[@]}"; do
  java -cp "$CP" kafkalab.stage1.ConsumerService "${NAMES[$i]}" "${PORTS[$i]}" >/dev/null 2>&1 &
  pids+=($!)
done
java -cp "$CP" kafkalab.stage1.OrderService >/dev/null 2>&1 & pids+=($!)

for p in "${PORTS[@]}"; do wait_up "$p" || { echo "${R}consumer on $p never came up${N}"; exit 1; }; done
for _ in $(seq 1 60); do curl -sf -o /dev/null localhost:8080/stats 2>/dev/null && break; sleep 0.5; done
echo "      producer :8080   consumers :9001 :9002 :9003 :9004"
echo

# ---------------------------------------------------------------- 2. normal traffic
echo "${B}[2/4]${N} sending $ORDERS orders (all consumers healthy)..."
ok=0; bad=0
for i in $(seq 1 "$ORDERS"); do
  [[ "$(order "demo-$i")" == "200" ]] && ok=$((ok+1)) || bad=$((bad+1))
done
echo "      producer replied: ${G}${ok} x 200${N}, ${R}${bad} x 500${N}"
echo

# ---------------------------------------------------------------- 3. the ledgers
echo "${B}[3/4]${N} what each consumer ACTUALLY applied (expected $ORDERS each):"
echo
printf "      %-11s %-9s %-9s %s\n" CONSUMER APPLIED FAILED VERDICT
a_analytics=0
for i in "${!NAMES[@]}"; do
  n="${NAMES[$i]}"; a=$(applied "${PORTS[$i]}"); f=$(failures "${PORTS[$i]}")
  [[ "$n" == "analytics" ]] && a_analytics=$a
  if   [[ "$a" -eq "$ORDERS" ]]; then v="${G}ok${N}"
  elif [[ "$f" -gt 0 ]];         then v="${Y}$((ORDERS-a)) lost — it failed${N}"
  else                                v="${R}$((ORDERS-a)) LOST — never even called${N}"
  fi
  printf "      %-11s %-9s %-9s %b\n" "$n" "$a" "$f" "$v"
done
echo
if [[ "$a_analytics" -lt "$ORDERS" ]]; then
  echo "      ${Y}^ analytics never failed, yet missed orders.${N}"
  echo "        publish() aborts on the first failure, so subscribers AFTER the"
  echo "        failing one are never contacted. Partial fan-out. (This is rung 1.)"
else
  echo "      ${G}^ every consumer was contacted — publish() continues past failures (rung 2+).${N}"
fi
echo

# ---------------------------------------------------------------- 4. Experiment B
echo "${B}[4/4]${N} EXPERIMENT B — a consumer that is DOWN, not failing"
echo
echo "      killing inventory (:9002)..."
lsof -ti:9002 -sTCP:LISTEN 2>/dev/null | xargs -r kill 2>/dev/null
sleep 1

code=$(order "order-lost")
echo "      sent 'order-lost' while inventory was down"
echo "      producer replied: ${B}HTTP ${code}${N}"
echo
echo "      restarting inventory..."
java -cp "$CP" kafkalab.stage1.ConsumerService inventory 9002 >/dev/null 2>&1 & pids+=($!)
wait_up 9002 || { echo "${R}inventory did not restart${N}"; exit 1; }

echo "      inventory is back up. waiting 5s to see if it catches up..."
sleep 5
caught=$(applied 9002)
echo "      orders inventory has applied since restarting: ${B}${caught}${N}"
echo
echo "      now sending ONE more order to prove inventory is alive and reachable..."
order "order-after" >/dev/null
sleep 1
after=$(applied 9002)
echo "      orders inventory has applied now: ${B}${after}${N}"
echo

echo "${B}=== VERDICT ===${N}"
if [[ "$caught" -eq 0 && "$after" -ge 1 ]]; then
  echo "  ${G}inventory is alive${N} — it applied 'order-after' the moment it was sent."
  echo "  ${R}'order-lost' was never delivered, and never will be.${N}"
  echo
  echo "  Nothing anywhere recorded that inventory still owed that work. There is no"
  echo "  queue, no retry, no catch-up. The event existed only as bytes on a TCP"
  echo "  connection that failed, and then it was gone."
  echo
  echo "  Note inventory's counter restarted at 0 — its ledger was in memory too."
  echo "  Even the record of what it HAD processed did not survive a restart."
else
  echo "  ${Y}unexpected: caught=${caught} after=${after}${N} — check the rung publish() is on."
fi
echo
echo "  The producer said HTTP ${code}. Ask yourself whether that was the truth."
echo
echo "shutting everything down."
