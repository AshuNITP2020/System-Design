#!/usr/bin/env bash
# ONE COMMAND to watch a consumer group scale out and rebalance.
#
#   ./kafkalab/stage5-partitions-groups/demo.sh
#   PARTS=1 ./kafkalab/stage5-partitions-groups/demo.sh     # only one member can ever work
#   KEY=fixed ./kafkalab/stage5-partitions-groups/demo.sh   # everything on one partition
#
# Needs both TODOs implemented: OrderService.partitionKey and the rebalance listener.
set -uo pipefail
cd "$(dirname "$0")/../.."

TOPIC=orders
PARTS="${PARTS:-3}"
KEY="${KEY:-user}"
CGROUP=payment        # not GROUPS — that is a reserved bash variable

R=$'\033[31m'; G=$'\033[32m'; Y=$'\033[33m'; B=$'\033[1m'; N=$'\033[0m'
pids=()
cleanup() {
  [[ ${#pids[@]} -gt 0 ]] && kill "${pids[@]}" 2>/dev/null
  lsof -ti:8080 -sTCP:LISTEN 2>/dev/null | xargs -r kill 2>/dev/null
  return 0
}
trap cleanup EXIT INT TERM

K=./kafkalab/infra/kcli.sh
# The listener output is the whole point, so read it straight from the member logs.
moves() { grep -hiE "revoked|assigned|idle|nothing" /tmp/s5-*.log 2>/dev/null | tail -"${1:-6}"; }

echo
echo "${B}=== STAGE 5: partitions and consumer groups ===${N}"
echo

echo "${B}[1/6]${N} broker + a fresh ${PARTS}-partition topic..."
docker ps --format '{{.Names}}' | grep -qx kafkalab-kafka1 || ./kafkalab/infra/up.sh single >/dev/null 2>&1
$K kafka-topics.sh --delete --topic "$TOPIC" >/dev/null 2>&1
if ! $K kafka-topics.sh --create --topic "$TOPIC" --partitions "$PARTS" --replication-factor 1 >/tmp/s5-topic.log 2>&1; then
  echo "${R}      topic create failed:${N}"; sed 's/^/      /' /tmp/s5-topic.log; exit 1
fi
$K kafka-consumer-groups.sh --delete --group "$CGROUP" >/dev/null 2>&1
rm -f /tmp/s5-[A-Z].log
echo "      topic '$TOPIC' with ${B}${PARTS} partitions${N}, group '$CGROUP' reset"

CP="$(./gradlew -q --console=plain :kafkalab:stage5-partitions-groups:printClasspath 2>/dev/null | tail -1)"
[[ -z "$CP" ]] && { echo "${R}gradle build failed${N}"; exit 1; }

java -Dorg.slf4j.simpleLogger.defaultLogLevel=warn -Dkey.strategy="$KEY" -cp "$CP" \
  kafkalab.stage5.OrderService >/tmp/s5-producer.log 2>&1 & pids+=($!)
for _ in $(seq 1 60); do curl -sf -o /dev/null localhost:8080/stats 2>/dev/null && break
  curl -s -o /dev/null -X POST localhost:8080/orders -d '{}' 2>/dev/null && break; sleep 0.5; done
if grep -q "UnsupportedOperationException" /tmp/s5-producer.log 2>/dev/null; then
  echo "${R}      TODO(1) partitionKey is not implemented yet.${N}"; exit 1
fi
echo

member() {
  java -Dorg.slf4j.simpleLogger.defaultLogLevel=warn -cp "$CP" \
    kafkalab.stage5.GroupMember "$CGROUP" "$1" >"/tmp/s5-$1.log" 2>&1 & pids+=($!)
}

echo "${B}[2/6]${N} ONE member in group '$CGROUP'..."
member A; sleep 9
moves 3
echo "      ${G}^ one member owns all ${PARTS} partitions.${N}"
if ! grep -qiE "assigned|revoked" /tmp/s5-A.log 2>/dev/null; then
  echo "      ${Y}(nothing printed — TODO(2), the rebalance listener, isn't implemented yet)${N}"
fi
echo

echo "${B}[3/6]${N} adding a SECOND member to the same group..."
member B; sleep 11
moves 4
echo "      ${G}^ the partitions were divided. nobody was asked; it just happened.${N}"
echo "        A was REVOKED everything first — that stop is the rebalance."
echo

echo "${B}[4/6]${N} adding a THIRD member..."
member C; sleep 11
moves 5
echo "      ${G}^ one partition each. maximum useful parallelism.${N}"
echo

echo "${B}[5/6]${N} adding a FOURTH member to a ${PARTS}-partition topic..."
member D; sleep 11
moves 6
echo
echo "      ${Y}^ the 4th member got NOTHING and will stay that way.${N}"
echo "        ${B}Partition count is the ceiling on consumer parallelism.${N}"
echo

echo "${B}[6/6]${N} killing member B — watch its partition move"
echo
pkill -f "stage5[.]GroupMember $CGROUP B" 2>/dev/null && echo "      killed member B"
sleep 12
moves 5
echo "      ${G}^ picked up automatically. the idle member was a hot spare, not waste.${N}"
echo

echo "${B}=== 20 orders, where they landed (key.strategy=$KEY) ===${N}"
for i in $(seq 1 20); do
  curl -s -o /dev/null -X POST localhost:8080/orders -H 'Content-Type: application/json' \
    -d "{\"orderId\":\"o-$i\",\"userId\":\"user-$i\"}"
done
sleep 6
echo
for p in $(seq 0 $((PARTS-1))); do
  c=$(grep -ho "p$p@" /tmp/s5-[A-Z].log 2>/dev/null | grep -c .)
  bar=""; [[ "$c" -gt 0 ]] && bar=$(printf '%.0s#' $(seq 1 "$c"))
  printf "      partition %d: %-3s %s\n" "$p" "$c" "$bar"
done
echo
echo "      Try:  KEY=fixed  ./kafkalab/stage5-partitions-groups/demo.sh"
echo "            ...every record on ONE partition. total ordering, idle consumers."
echo "            PARTS=1  ...only one member can ever work."
echo
echo "shutting down (broker stays up)."
