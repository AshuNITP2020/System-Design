#!/usr/bin/env bash
# ONE COMMAND to see stage 4: real Kafka doing what you hand-built in stage 3.
#
#   ./kafkalab/stage4-kafka-single/demo.sh
#
# Starts the broker if it isn't up, creates the topic, produces, runs four consumer groups,
# then shows REAL consumer-group lag from Kafka's own CLI and does a real offset reset.
set -uo pipefail
cd "$(dirname "$0")/../.."

ORDERS="${ORDERS:-12}"
CGROUPS=(payment inventory email analytics)   # not GROUPS — that is a reserved bash variable
TOPIC=orders

R=$'\033[31m'; G=$'\033[32m'; Y=$'\033[33m'; B=$'\033[1m'; N=$'\033[0m'
pids=()
cleanup() {
  [[ ${#pids[@]} -gt 0 ]] && kill "${pids[@]}" 2>/dev/null
  lsof -ti:8080 -sTCP:LISTEN 2>/dev/null | xargs -r kill 2>/dev/null
  return 0
}
trap cleanup EXIT INT TERM

echo
echo "${B}=== STAGE 4: real Kafka, one broker, KRaft ===${N}"
echo

echo "${B}[1/6]${N} making sure the broker is up..."
if ! docker ps --format '{{.Names}}' | grep -qx kafkalab-kafka1; then
  ./kafkalab/infra/up.sh single >/dev/null 2>&1 || { echo "${R}./kafkalab/infra/up.sh failed${N}"; exit 1; }
fi
echo "      broker up. console: http://localhost:8081"

# Fresh topic each run so offsets and lag are meaningful.
# Do NOT swallow the error here. An earlier version redirected stderr to /dev/null and then
# echoed "topic created" unconditionally — so when the path to kcli.sh was wrong, the demo
# cheerfully reported success while creating nothing. Silent failure is the enemy.
./kafkalab/infra/kcli.sh kafka-topics.sh --delete --topic "$TOPIC" >/dev/null 2>&1
if ! ./kafkalab/infra/kcli.sh kafka-topics.sh --create --topic "$TOPIC" \
        --partitions 3 --replication-factor 1 >/tmp/s4-topic.log 2>&1; then
  echo "${R}      could not create topic '$TOPIC':${N}"; sed 's/^/      /' /tmp/s4-topic.log; exit 1
fi
echo "      topic '$TOPIC' created with ${B}3 partitions${N}"

# Delete the consumer groups too, and this is not optional.
#
# Consumer group offsets live in __consumer_offsets, NOT in the topic. Deleting a topic does
# clear them, but asynchronously — so on a re-run the first group to join can still find stale
# offsets from the previous run. Because each run produces the same 12 keyed records, those stale
# offsets point exactly at the new end-of-log, and that group reads nothing and reports 0 applied.
#
# auto.offset.reset=earliest does NOT save you: it only applies when there is no committed offset
# at all. Once a group has one, the setting is ignored.
for g in "${CGROUPS[@]}"; do
  ./kafkalab/infra/kcli.sh kafka-consumer-groups.sh --delete --group "$g" >/dev/null 2>&1
done
echo "      consumer groups reset, so every run starts from a clean slate"
echo

echo "${B}[2/6]${N} starting producer..."
cleanup
CP="$(./gradlew -q --console=plain :kafkalab:stage4-kafka-single:printClasspath 2>/dev/null | tail -1)"
[[ -z "$CP" ]] && { echo "${R}gradle build failed${N}"; exit 1; }
java -Dorg.slf4j.simpleLogger.defaultLogLevel=warn -cp "$CP" kafkalab.stage4.OrderService \
  >/tmp/s4p.log 2>&1 & pids+=($!)
for _ in $(seq 1 60); do curl -sf -o /dev/null localhost:8080/stats 2>/dev/null && break; sleep 0.5; done
curl -sf -o /dev/null localhost:8080/stats 2>/dev/null || {
  echo "${R}producer did not start:${N}"; tail -20 /tmp/s4p.log; exit 1; }
echo "      producer :8080 -> kafka localhost:9092"
echo

echo "${B}[3/6]${N} producing $ORDERS orders (keyed by userId — watch the partitions)..."
for i in $(seq 1 "$ORDERS"); do
  r=$(curl -s -X POST localhost:8080/orders -H 'Content-Type: application/json' \
        -d "{\"orderId\":\"k-$i\",\"userId\":\"user-$i\"}")
  [[ $i -le 4 ]] && echo "      user-$i -> $r"
done
echo "      ..."
echo "      ${Y}different users land on different partitions. same user always the same one.${N}"
echo

echo "${B}[4/6]${N} starting ${#CGROUPS[@]} consumer groups (4 values of one config key)..."
for g in "${CGROUPS[@]}"; do
  java -Dorg.slf4j.simpleLogger.defaultLogLevel=warn -cp "$CP" kafkalab.stage4.Consumer "$g" \
    >"/tmp/s4-$g.log" 2>&1 & pids+=($!)
done
sleep 18
echo
printf "      %-11s %-9s %s\n" GROUP APPLIED VERDICT
for g in "${CGROUPS[@]}"; do
  a=$(grep -c "applied" "/tmp/s4-$g.log" 2>/dev/null)
  a=${a:-0}
  f=$(grep -c "FAILED" "/tmp/s4-$g.log" 2>/dev/null); f=${f:-0}
  if   [[ "$a" -ge "$ORDERS" ]];             then v="${G}saw everything${N}"
  elif [[ "$a" -eq 0 ]];                     then v="${R}saw NOTHING — check /tmp/s4-$g.log${N}"
  elif [[ "$f" -gt 0 ]];                     then v="${Y}$a applied, $f failed (this one is flaky by design)${N}"
  else                                            v="${Y}$a of $ORDERS — still catching up?${N}"
  fi
  printf "      %-11s %-9s %b\n" "$g" "$a" "$v"
done
echo

echo "${B}[5/6]${N} REAL consumer-group lag, straight from Kafka's CLI:"
echo
# One header, then only real rows. The CLI prints a fresh header per group, and prints a bare
# header with no rows for any group that happens to be mid-rebalance — which looked like garbage.
printf "      %-11s %-8s %-4s %-10s %-10s %s\n" GROUP TOPIC PART CURRENT END LAG
./kafkalab/infra/kcli.sh kafka-consumer-groups.sh --describe --all-groups 2>&1 \
  | grep -E '^(payment|inventory|email|analytics)[[:space:]]' \
  | sort \
  | awk '{printf "      %-11s %-8s %-4s %-10s %-10s %s\n", $1,$2,$3,$4,$5,$6}' | head -20
echo
echo "      ${G}^ every group independently at the end of all 3 partitions.${N}"
echo "        This is __consumer_offsets — Kafka's version of your 4 .offset files."
echo

echo "${B}[6/6]${N} REPLAY — resetting analytics to the beginning"
echo
# A group's offsets can only be reset while it has NO active members. That is a real
# operational constraint, not a quirk of this demo.
echo "      stopping the analytics consumer (offsets can only be reset with no active members)"
pkill -f 'stage4[.]Consumer analytics' 2>/dev/null
sleep 8
if ! ./kafkalab/infra/kcli.sh kafka-consumer-groups.sh --group analytics --topic "$TOPIC" \
        --reset-offsets --to-earliest --execute >/tmp/s4-reset.log 2>&1; then
  echo "${R}      reset failed:${N}"; sed 's/^/      /' /tmp/s4-reset.log | head -4
else
  grep -E '^(GROUP|analytics)' /tmp/s4-reset.log | awk '{printf "      %-11s %-8s %-4s %s\n",$1,$2,$3,$4}'
fi
echo
java -Dorg.slf4j.simpleLogger.defaultLogLevel=warn -cp "$CP" kafkalab.stage4.Consumer analytics \
  >/tmp/s4-replay.log 2>&1 & pids+=($!)
sleep 12
rep=$(grep -c "applied" /tmp/s4-replay.log 2>/dev/null); rep=${rep:-0}
echo "      analytics re-applied ${B}${rep}${N} records from the start"
echo "      ${G}^ the other three groups never moved.${N}"
echo

echo "${B}=== WHAT DISAPPEARED ===${N}"
cat <<EOF
  Log.java      append(), length prefix, O(n) offset scan, torn-write guard
                  -> producer.send()
  Offsets.java  position(), commit(), four .offset files
                  -> group.id + commitSync()   (stored in __consumer_offsets)
  the poll loop readFrom() + Thread.sleep(200)
                  -> poll(Duration)  — a LONG poll, so stage 2's
                     "no interval is both cheap and fast" dilemma is just gone
  wall #6       splitting the file by hash(key) % N
                  -> --partitions 3, and the key you pass to ProducerRecord

  Still yours to worry about: at-least-once vs at-most-once. That did not go away,
  it just moved to where you put commitSync(). Stage 7 is where you attack it.

  Look at it live:  http://localhost:8081     or    ./kafkalab/infra/watch.sh
EOF
echo
echo "shutting the app down (broker stays up — ./kafkalab/infra/down.sh to stop it)."
