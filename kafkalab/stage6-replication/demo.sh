#!/usr/bin/env bash
# ONE COMMAND to kill a broker mid-produce and count what the cluster lost.
#
#   ./kafkalab/stage6-replication/demo.sh              # acks=1, min.insync.replicas=2
#   ACKS=0   ./kafkalab/stage6-replication/demo.sh
#   ACKS=all ./kafkalab/stage6-replication/demo.sh
#   MIN_ISR=1 ACKS=all ./kafkalab/stage6-replication/demo.sh
#   ./kafkalab/stage6-replication/demo.sh refuse       # stop TWO brokers, watch writes rejected
#
#   HARD=1 PAUSE=0 ACKS=1 ./kafkalab/stage6-replication/demo.sh
#     Pulls the plug instead of asking politely. `docker stop` sends SIGTERM, and Kafka's
#     controlled shutdown then hands leadership to a CAUGHT-UP follower before exiting — which
#     is precisely the thing that would have lost your data, so a graceful stop tends to lose
#     nothing even at acks=1. HARD=1 uses `docker kill` (SIGKILL): no handover, so the new leader
#     is whoever the controller picks, and it may be missing the newest records. PAUSE=0 widens
#     the window by producing as fast as possible.
#
# Needs all three TODOs implemented: producerProps, the send callback, LossAudit.report.
# Keep ./kafkalab/infra/watch.sh open in another split — the ISR column is the show.
set -uo pipefail
cd "$(dirname "$0")/../.."

MODE="${1:-loss}"
TOPIC=orders
PARTS="${PARTS:-3}"
RF=3
ACKS="${ACKS:-1}"
MIN_ISR="${MIN_ISR:-2}"
COUNT="${COUNT:-2000}"
PAUSE="${PAUSE:-5}"
BOOTSTRAP=localhost:9092,localhost:9093,localhost:9094

R=$'\033[31m'; G=$'\033[32m'; Y=$'\033[33m'; B=$'\033[1m'; N=$'\033[0m'
SURVIVOR=1                 # which container the CLI talks through; moves when we kill brokers
stopped=()                 # brokers this script stopped, so it can start them again
pids=()

cleanup() {
  [[ ${#pids[@]} -gt 0 ]] && kill "${pids[@]}" 2>/dev/null
  for n in "${stopped[@]:-}"; do
    [[ -n "$n" ]] && docker start "kafkalab-kafka$n" >/dev/null 2>&1
  done
  return 0
}
trap cleanup EXIT INT TERM

# Every CLI call goes through a broker we know is alive — kcli.sh defaults to kafka1, which is
# wrong the moment kafka1 is the one on the floor.
kc() { BROKER="kafkalab-kafka$SURVIVOR" KAFKA_BOOTSTRAP="kafka$SURVIVOR:19092" \
         ./kafkalab/infra/kcli.sh "$@"; }

leader_of() {  # leader_of <partition> -> broker node id
  kc kafka-topics.sh --describe --topic "$TOPIC" 2>/dev/null \
    | awk -v p="$1" '$0 ~ ("Partition: " p "\t") || $0 ~ ("Partition: " p " ") {
        for (i=1;i<=NF;i++) if ($i=="Leader:") { print $(i+1); exit }
      }'
}

layout() { kc kafka-topics.sh --describe --topic "$TOPIC" 2>/dev/null | grep 'Partition:' | sed 's/^\s*/      /'; }

stop_broker() {
  if [[ "${HARD:-0}" == "1" ]]; then
    docker kill "kafkalab-kafka$1" >/dev/null 2>&1     # SIGKILL: no controlled shutdown
  else
    docker stop "kafkalab-kafka$1" >/dev/null 2>&1     # SIGTERM: leadership handed over first
  fi
  stopped+=("$1")
  [[ "$SURVIVOR" == "$1" ]] && for c in 1 2 3; do
    [[ "$c" != "$1" ]] && docker ps --format '{{.Names}}' | grep -qx "kafkalab-kafka$c" && { SURVIVOR=$c; break; }
  done
  return 0
}

start_broker() {
  docker start "kafkalab-kafka$1" >/dev/null 2>&1
  for _ in $(seq 1 30); do
    docker exec "kafkalab-kafka$1" /opt/kafka/bin/kafka-broker-api-versions.sh \
      --bootstrap-server "kafka$1:19092" >/dev/null 2>&1 && return 0
    sleep 2
  done
  return 1
}

echo
echo "${B}=== STAGE 6: replication, ISR and acks ===${N}"
echo

echo "${B}[1/6]${N} three-broker cluster..."
if [[ "$(docker ps --format '{{.Names}}' | grep -c '^kafkalab-kafka[123]$')" -lt 3 ]]; then
  ./kafkalab/infra/up.sh cluster >/dev/null 2>&1 || { echo "${R}      cluster failed to start${N}"; exit 1; }
fi
for c in 1 2 3; do docker start "kafkalab-kafka$c" >/dev/null 2>&1; done
sleep 3
echo "      kafka1 kafka2 kafka3 up"

echo "${B}[2/6]${N} fresh topic: ${PARTS} partitions, RF=${RF}, min.insync.replicas=${MIN_ISR}"
kc kafka-topics.sh --delete --topic "$TOPIC" >/dev/null 2>&1
sleep 2
if ! kc kafka-topics.sh --create --topic "$TOPIC" --partitions "$PARTS" \
        --replication-factor "$RF" --config "min.insync.replicas=$MIN_ISR" >/tmp/s6-topic.log 2>&1; then
  echo "${R}      topic create failed:${N}"; sed 's/^/      /' /tmp/s6-topic.log; exit 1
fi
echo
layout
echo "      ${G}^ Replicas is the assignment. Isr is who is currently caught up. They differ soon.${N}"
echo

CP="$(./gradlew -q --console=plain :kafkalab:stage6-replication:printClasspath 2>/dev/null | tail -1)"
[[ -z "$CP" ]] && { echo "${R}gradle build failed${N}"; exit 1; }

produce() {  # produce <acks> <count> -> /tmp/s6-producer.log
  java -Dorg.slf4j.simpleLogger.defaultLogLevel=warn \
       -Dbootstrap="$BOOTSTRAP" -Dacks="$1" -Dcount="$2" -Dpause="$PAUSE" \
       -cp "$CP" kafkalab.stage6.OrderProducer >/tmp/s6-producer.log 2>&1 & pids+=($!)
}

todo_check() {
  if grep -q "TODO(1)" /tmp/s6-producer.log 2>/dev/null; then
    echo "${R}      TODO(1) producerProps is not implemented yet.${N}"; exit 1
  fi
  if grep -q "TODO(2)" /tmp/s6-producer.log 2>/dev/null; then
    echo "${R}      TODO(2) the send callback is not implemented yet.${N}"; exit 1
  fi
}

# Kill the broker when the producer is genuinely MID-FLIGHT, not after a fixed nap. A stopwatch
# gets this wrong in both directions: at PAUSE=0 a small run finishes in under a second and the
# "mid-produce" kill lands on an idle broker, which quietly turns the whole experiment into a
# no-op that reports zero loss and zero errors. Returns 1 if the producer is already done.
wait_until_midflight() {
  local target=$(( COUNT / 4 )) sent
  for _ in $(seq 1 150); do
    sent=$(grep -o 'sent [0-9]*' /tmp/s6-producer.log 2>/dev/null | tail -1 | awk '{print $2}')
    [[ -n "$sent" && "$sent" -ge "$target" ]] && return 0
    kill -0 "${pids[-1]}" 2>/dev/null || return 1
    sleep 0.2
  done
  return 0
}

# ---------------------------------------------------------------------------------------------
if [[ "$MODE" == "refuse" ]]; then
  echo "${B}[3/6]${N} stopping TWO brokers with min.insync.replicas=${MIN_ISR}..."
  stop_broker 3; stop_broker 2
  sleep 8
  echo
  layout
  echo "      ${Y}^ Isr is down to one broker. It is healthy and it has the data.${N}"
  echo

  echo "${B}[4/6]${N} producing 60 records with acks=${ACKS} against the survivor..."
  produce "$ACKS" 60
  wait "${pids[-1]}" 2>/dev/null
  todo_check
  echo
  sed -n '/attempted/,$p' /tmp/s6-producer.log | sed 's/^/      /'
  grep -m3 -i "exception\|NotEnough" /tmp/s6-producer.log | sed 's/^/      /'
  echo
  if [[ "$MIN_ISR" -ge 2 && "$ACKS" == "all" ]]; then
    echo "      ${Y}^ the writes were REFUSED. Nothing is broken.${N}"
    echo "        min.insync.replicas=2 means: rather be unavailable than hold one copy."
  elif [[ "$MIN_ISR" -ge 2 ]]; then
    echo "      ${Y}^ these went through — with min.insync.replicas=${MIN_ISR} set on the topic${N}"
    echo "      ${Y}  and only ONE replica alive. Read that twice.${N}"
    echo
    echo "        min.insync.replicas is consulted ONLY when the producer asks for acks=all."
    echo "        At acks=${ACKS} the leader was told 'write it locally and answer me, do not"
    echo "        consult anyone' — so the topic's minimum was never evaluated. The safety"
    echo "        setting is a PAIR. Configure the topic, ship a producer at acks=1, and you"
    echo "        have no guarantee at all, silently."
    echo
    echo "        Now ask for it:  ACKS=all $0 refuse"
  else
    echo "      ${Y}^ these went through. One copy, no complaint, no guarantee.${N}"
  fi
  echo
  echo "${B}[5/6]${N} bringing both brokers back..."
  start_broker 2; start_broker 3; sleep 6
  layout
  echo "      ${G}^ ISR healed on its own. Nobody ran a repair command.${N}"
  echo
  echo "${B}[6/6]${N} now flip the trade the other way:"
  echo "      MIN_ISR=1 ACKS=all ./kafkalab/stage6-replication/demo.sh refuse"
  echo "      ...same cluster, same failure, writes accepted. One integer, two philosophies."
  echo
  exit 0
fi

# ---------------------------------------------------------------------------------------------
echo "${B}[3/6]${N} producing ${COUNT} records with ${B}acks=${ACKS}${N}..."
produce "$ACKS" "$COUNT"
sleep 2
todo_check
MIDFLIGHT=yes
wait_until_midflight || MIDFLIGHT=no
head -4 /tmp/s6-producer.log | sed 's/^/      /'
echo
if [[ "$MIDFLIGHT" == "no" ]]; then
  echo "      ${Y}WARNING: the producer finished before the kill. Whatever the audit prints${N}"
  echo "      ${Y}below, it measured nothing — the broker died with no records in flight.${N}"
  echo "      ${Y}Give the run more work so it is still going when the leader dies:${N}"
  echo "      ${Y}  HARD=1 PAUSE=0 COUNT=300000 ACKS=${ACKS} $0${N}"
  echo
fi

VICTIM="$(leader_of 0)"
[[ -z "$VICTIM" ]] && VICTIM=2
if [[ "${HARD:-0}" == "1" ]]; then
  echo "${B}[4/6]${N} ${R}docker kill kafkalab-kafka${VICTIM}${N} — SIGKILL, no handover, mid-flight"
else
  echo "${B}[4/6]${N} ${R}docker stop kafkalab-kafka${VICTIM}${N} — it leads partition 0, mid-flight"
  echo "      (a polite SIGTERM: Kafka hands leadership over before exiting. Try HARD=1.)"
fi
stop_broker "$VICTIM"
sleep 10
echo
layout
echo "      ${Y}^ new leader wherever kafka${VICTIM} led, ISR shrunk. Replicas did not change.${N}"
echo

echo "${B}[5/6]${N} waiting for the producer to finish, then restoring kafka${VICTIM}..."
wait "${pids[-1]}" 2>/dev/null
sed -n '/attempted/,$p' /tmp/s6-producer.log | sed 's/^/      /'
start_broker "$VICTIM" || echo "      ${Y}(kafka${VICTIM} slow to return)${N}"
sleep 8
echo
layout
echo "      ${G}^ back in the ISR. It caught up from its own disk — the volume survived the stop.${N}"
echo

echo "${B}[6/6]${N} auditing: what was acknowledged, and what is actually there?"
echo
java -Dorg.slf4j.simpleLogger.defaultLogLevel=warn -Dbootstrap="$BOOTSTRAP" \
     -cp "$CP" kafkalab.stage6.LossAudit 2>&1 | sed 's/^/      /' | tee /tmp/s6-audit.log
if grep -q "TODO(3)" /tmp/s6-audit.log 2>/dev/null; then
  echo "      ${R}TODO(3) LossAudit.report is not implemented yet.${N}"; exit 1
fi
echo
echo "      ${B}Every acked-but-absent key was a successful callback in your producer.${N}"
echo
echo "      Run the other two and fill in the README table:"
echo "        ACKS=0   ./kafkalab/stage6-replication/demo.sh"
echo "        ACKS=all ./kafkalab/stage6-replication/demo.sh"
echo "        ./kafkalab/stage6-replication/demo.sh refuse"
echo
