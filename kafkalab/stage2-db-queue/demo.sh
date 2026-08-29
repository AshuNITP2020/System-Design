#!/usr/bin/env bash
# ONE COMMAND to see stage 2. Resets the DB, starts the producer, shows orders piling up as
# durable rows with NO consumers running, then starts the workers and watches them drain.
#
#   ./kafkalab/stage2-db-queue/demo.sh
#
# Only works once OrderService.enqueue and Worker.claim/markDone are implemented.
set -uo pipefail
cd "$(dirname "$0")/../.."

ORDERS="${ORDERS:-10}"
WORKERS_ARG="${WORKERS:-payment inventory email analytics}"
read -r -a WORKERS <<<"$WORKERS_ARG"

R=$'\033[31m'; G=$'\033[32m'; Y=$'\033[33m'; B=$'\033[1m'; N=$'\033[0m'
pids=()

cleanup() {
  [[ ${#pids[@]} -gt 0 ]] && kill "${pids[@]}" 2>/dev/null
  lsof -ti:8080 -sTCP:LISTEN 2>/dev/null | xargs -r kill 2>/dev/null
  return 0
}
trap cleanup EXIT INT TERM

queue() { curl -s localhost:8080/queue 2>/dev/null || echo '{}'; }

echo
echo "${B}=== STAGE 2: the database queue ===${N}"
echo

echo "${B}[1/5]${N} building, wiping data/queue.db, starting producer..."
cleanup
rm -f kafkalab/stage2-db-queue/data/queue.db*
CP="$(./gradlew -q --console=plain :kafkalab:stage2-db-queue:printClasspath 2>/dev/null | tail -1)"
[[ -z "$CP" ]] && { echo "${R}gradle build failed${N}"; exit 1; }

java -cp "$CP" kafkalab.stage2.OrderService >/tmp/stage2-producer.log 2>&1 & pids+=($!)
for _ in $(seq 1 60); do curl -sf -o /dev/null localhost:8080/queue 2>/dev/null && break; sleep 0.5; done
curl -sf -o /dev/null localhost:8080/queue 2>/dev/null || {
  echo "${R}producer did not start. is enqueue()/Db wired up? log:${N}"; tail -20 /tmp/stage2-producer.log; exit 1; }
echo "      producer :8080 up.  ${B}no workers running yet.${N}"
echo

echo "${B}[2/5]${N} sending $ORDERS orders with ZERO consumers alive..."
ok=0; bad=0; t0=$(date +%s%N)
for i in $(seq 1 "$ORDERS"); do
  c=$(curl -s -o /dev/null -w '%{http_code}' -X POST localhost:8080/orders \
        -H 'Content-Type: application/json' -d "{\"orderId\":\"q-$i\",\"userId\":\"u\"}")
  [[ "$c" == "200" ]] && ok=$((ok+1)) || bad=$((bad+1))
done
ms=$(( ($(date +%s%N) - t0) / 1000000 ))
echo "      ${G}${ok} x 200${N}  ${R}${bad} x 500${N}   total ${B}${ms}ms${N} for $ORDERS orders"
echo "      (~$((ms / ORDERS))ms each. stage 1 was ~420ms each. that gap is the whole stage.)"
echo

echo "${B}[3/5]${N} the queue right now:"
echo "      $(queue)"
echo "      ${B}Nothing has been processed. Nothing has been lost.${N}"
echo "      In stage 1 these $ORDERS events would already be gone forever."
echo

echo "${B}[4/5]${N} starting workers: ${WORKERS[*]}"
for w in "${WORKERS[@]}"; do
  java -cp "$CP" kafkalab.stage2.Worker "$w" 200 >"/tmp/stage2-$w.log" 2>&1 & pids+=($!)
done
for i in $(seq 1 20); do
  sleep 2
  q=$(queue)
  echo "      t+$((i*2))s  $q"
  grep -q '"NEW"' <<<"$q" || break
done
echo

echo "${B}[5/5]${N} what each worker actually applied:"
echo
printf "      %-11s %-9s %s\n" WORKER APPLIED VERDICT
total_applied=0
for w in "${WORKERS[@]}"; do
  a=$(grep -c "applied" "/tmp/stage2-$w.log" 2>/dev/null || echo 0)
  total_applied=$((total_applied + a))
  if   [[ "$a" -eq "$ORDERS" ]]; then v="${G}saw everything${N}"
  elif [[ "$a" -eq 0 ]];         then v="${R}saw NOTHING${N}"
  else                                v="${Y}saw only $a of $ORDERS${N}"
  fi
  printf "      %-11s %-9s %b\n" "$w" "$a" "$v"
done
echo

echo "${B}=== VERDICT ===${N}"
if [[ ${#WORKERS[@]} -gt 1 && "$total_applied" -le $((ORDERS + ORDERS / 2)) ]]; then
  echo "  ${R}Each order was processed by roughly ONE worker, not all ${#WORKERS[@]}.${N}"
  echo
  echo "  'status' is a single column shared by every worker. Whoever marks a row DONE"
  echo "  first hides it from the other three. That is what a QUEUE does — it distributes"
  echo "  work among competing consumers."
  echo
  echo "  You did not want distribution. You wanted FAN-OUT: all four see everything."
  echo "  No amount of tuning fixes this; the schema is the wrong shape."
elif [[ "$total_applied" -eq $((ORDERS * ${#WORKERS[@]})) ]]; then
  echo "  ${G}Every worker saw every order${N} — you're past rung 2 (per-consumer state)."
  echo
  echo "  Now count the cost of a fifth consumer: an ALTER TABLE on the shared table,"
  echo "  coordinated with a deploy. That is stage 1's SUBSCRIBERS list, written in DDL."
else
  echo "  ${Y}total applied = ${total_applied} across ${#WORKERS[@]} workers, ${ORDERS} orders.${N}"
  echo "  Read the per-worker logs in /tmp/stage2-*.log and work out which rung you're on."
fi
echo
echo "  Durability is real and new: the producer answered in ~$((ms / ORDERS))ms and the events"
echo "  waited on disk for consumers that did not exist yet."
echo
echo "shutting everything down. (db left at kafkalab/stage2-db-queue/data/queue.db)"
