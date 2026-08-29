#!/usr/bin/env bash
# ONE COMMAND to see stage 3 — and the direct comparison against stage 2's race.
#
#   ./kafkalab/stage3-my-own-log/demo.sh
#
# Only works once Log.append, Log.readFrom and the Consumer TODO(3) are implemented.
set -uo pipefail
cd "$(dirname "$0")/../.."

ORDERS="${ORDERS:-12}"
GROUPS=(payment inventory email analytics)

R=$'\033[31m'; G=$'\033[32m'; Y=$'\033[33m'; B=$'\033[1m'; N=$'\033[0m'
pids=()
cleanup() {
  [[ ${#pids[@]} -gt 0 ]] && kill "${pids[@]}" 2>/dev/null
  lsof -ti:8080 -sTCP:LISTEN 2>/dev/null | xargs -r kill 2>/dev/null
  return 0
}
trap cleanup EXIT INT TERM

echo
echo "${B}=== STAGE 3: your own log ===${N}"
echo

echo "${B}[1/5]${N} building, wiping data/, starting producer..."
cleanup
rm -rf kafkalab/stage3-my-own-log/data
CP="$(./gradlew -q --console=plain :kafkalab:stage3-my-own-log:printClasspath 2>/dev/null | tail -1)"
[[ -z "$CP" ]] && { echo "${R}gradle build failed${N}"; exit 1; }
java -cp "$CP" kafkalab.stage3.OrderService >/tmp/s3p.log 2>&1 & pids+=($!)
for _ in $(seq 1 60); do curl -sf -o /dev/null localhost:8080/log 2>/dev/null && break; sleep 0.5; done
curl -sf -o /dev/null localhost:8080/log 2>/dev/null || {
  echo "${R}producer did not start — is Log.append implemented? log:${N}"; tail -20 /tmp/s3p.log; exit 1; }
echo "      producer :8080 up, log empty, no consumers."
echo

echo "${B}[2/5]${N} appending $ORDERS orders (note the returned offsets)..."
for i in $(seq 1 "$ORDERS"); do
  r=$(curl -s -X POST localhost:8080/orders -H 'Content-Type: application/json' \
        -d "{\"orderId\":\"log-$i\",\"userId\":\"u\"}")
  [[ $i -le 3 || $i -eq $ORDERS ]] && echo "      order $i -> $r"
done
echo "      $(curl -s localhost:8080/log)"
echo

echo "${B}[3/5]${N} starting all ${#GROUPS[@]} consumer groups at once..."
for g in "${GROUPS[@]}"; do
  java -cp "$CP" kafkalab.stage3.Consumer "$g" -1 200 >"/tmp/s3-$g.log" 2>&1 & pids+=($!)
done
for i in $(seq 1 15); do
  sleep 2
  lag=$(curl -s localhost:8080/log | grep -o '"lag":[0-9]*' | grep -v '"lag":0' | wc -l)
  [[ "$lag" -eq 0 ]] && break
done
echo "      $(curl -s localhost:8080/log)"
echo

echo "${B}[4/5]${N} how many of the $ORDERS orders did each group apply?"
echo
printf "      %-11s %-9s %s\n" GROUP APPLIED VERDICT
total=0
for g in "${GROUPS[@]}"; do
  a=$(grep -c "applied" "/tmp/s3-$g.log" 2>/dev/null || echo 0)
  total=$((total + a))
  [[ "$a" -eq "$ORDERS" ]] && v="${G}saw everything${N}" || v="${Y}saw only $a of $ORDERS${N}"
  printf "      %-11s %-9s %b\n" "$g" "$a" "$v"
done
want=$((ORDERS * ${#GROUPS[@]}))
echo "      --------------------"
printf "      %-11s %-9s (want %d)\n" TOTAL "$total" "$want"
echo
if [[ "$total" -eq "$want" ]]; then
  echo "      ${G}^ $want of $want. Every group saw every order.${N}"
  echo "        Stage 2's queue gave 36-39 of 48, differently every run."
  echo "        Nothing is shared here, so there is nothing to race over."
else
  echo "      ${Y}^ $total of $want — expected if 'email' is still failing 25% and you chose${N}"
  echo "        to skip failed records. Check /tmp/s3-email.log."
fi
echo

echo "${B}[5/5]${N} REPLAY — rewinding analytics to offset 0, others untouched"
echo
before=$(curl -s localhost:8080/log)
java -cp "$CP" kafkalab.stage3.Consumer analytics 0 200 >/tmp/s3-replay.log 2>&1 & pids+=($!)
sleep 8
replayed=$(grep -c "applied" /tmp/s3-replay.log 2>/dev/null || echo 0)
echo "      analytics reprocessed ${B}${replayed}${N} orders from history"
echo "      $(curl -s localhost:8080/log)"
echo

echo "${B}=== VERDICT ===${N}"
echo "  ${G}Fan-out:${N} every group read every record. No claim, no lock, no lease."
echo "  ${G}Replay:${N}  one number in one file, rewound. The other three never noticed."
echo "  ${G}New consumer:${N} start a process. No schema change, no roster, no producer edit."
echo
echo "  Stage 2 stored per-consumer progress INSIDE the shared row, so consumers fought."
echo "  Stage 3 stores it WITH the consumer, so there is nothing to fight over."
echo
echo "  You have now written a single-node, unreplicated, crash-unsafe Kafka."
echo "  Walls 3-6 in the README are the rest of what real Kafka adds."
echo
echo "shutting everything down."
