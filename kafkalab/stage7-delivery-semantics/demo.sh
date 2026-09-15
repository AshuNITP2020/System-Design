#!/usr/bin/env bash
# THREE demos, one per source of duplication. Run them in order.
#
#   ./kafkalab/stage7-delivery-semantics/demo.sh dupes   # producer retries -> duplicates -> idempotence
#   ./kafkalab/stage7-delivery-semantics/demo.sh crash   # consumer crash -> double charge -> dedupe store
#   ./kafkalab/stage7-delivery-semantics/demo.sh tx      # transactions and read_committed
#
# Single broker for this stage: ./kafkalab/infra/up.sh single
set -uo pipefail
cd "$(dirname "$0")/../.."

MODE="${1:-dupes}"
TOPIC=orders
OUT=orders-validated
PARTS="${PARTS:-3}"
COUNT="${COUNT:-500}"
# How long the producer waits for the broker's reply before giving up and retrying. The whole
# dupes demo depends on this being SHORTER than the broker's real reply time, and a broker on
# localhost answers in a millisecond or two — 3ms bites, 25ms never fires. If the first run
# shows EXTRA COPIES 0, lower it (TIMEOUT=2); if every send errors out, raise it.
TIMEOUT="${TIMEOUT:-3}"

R=$'\033[31m'; G=$'\033[32m'; Y=$'\033[33m'; B=$'\033[1m'; N=$'\033[0m'
pids=()
cleanup() { [[ ${#pids[@]} -gt 0 ]] && kill "${pids[@]}" 2>/dev/null; return 0; }
trap cleanup EXIT INT TERM

K=./kafkalab/infra/kcli.sh

recreate() {  # recreate <topic>
  $K kafka-topics.sh --delete --topic "$1" >/dev/null 2>&1
  sleep 1
  $K kafka-topics.sh --create --topic "$1" --partitions "$PARTS" --replication-factor 1 \
     >/tmp/s7-topic.log 2>&1 || { echo "${R}  topic create failed:${N}"; sed 's/^/  /' /tmp/s7-topic.log; exit 1; }
}

todo_check() {  # todo_check <logfile> <n> <what>
  if grep -q "TODO($2)" "$1" 2>/dev/null; then
    echo "${R}      TODO($2) — $3 — is not implemented yet.${N}"; exit 1
  fi
}

echo
echo "${B}=== STAGE 7: delivery semantics (${MODE}) ===${N}"
echo

echo "${B}[setup]${N} single broker..."
docker ps --format '{{.Names}}' | grep -qx kafkalab-kafka1 || ./kafkalab/infra/up.sh single >/dev/null 2>&1
CP="$(./gradlew -q --console=plain :kafkalab:stage7-delivery-semantics:printClasspath 2>/dev/null | tail -1)"
[[ -z "$CP" ]] && { echo "${R}gradle build failed${N}"; exit 1; }

# An ARRAY, not a string. The repo path contains a space ("System Design"), so an unquoted
# $J string would split the classpath in half and java dies with ClassNotFoundException.
# "${J[@]}" expands each element as one word, space and all, and works under `timeout` too.
J=(java -Dorg.slf4j.simpleLogger.defaultLogLevel=warn -Dbootstrap=localhost:9092 -cp "$CP")

produce() {  # produce <idempotence>
  "${J[@]}" -Didempotence="$1" -Drequest.timeout="$TIMEOUT" -Dcount="$COUNT" \
     kafkalab.stage7.OrderProducer >/tmp/s7-producer.log 2>&1
  todo_check /tmp/s7-producer.log 1 "the producer config"
  sed -n '/attempted/,/send errors/p' /tmp/s7-producer.log | sed 's/^/      /'
}

audit() {  # audit <topic> <isolation>
  "${J[@]}" -Dtopic="$1" -Disolation="$2" kafkalab.stage7.DupeAudit 2>&1 \
    | sed -n '/topic  /,$p' | sed 's/^/      /'
}

# ---------------------------------------------------------------------------------------------
if [[ "$MODE" == "dupes" ]]; then
  echo "${B}[1/4]${N} fresh topic, then ${COUNT} orders with ${B}idempotence OFF${N}"
  echo "      (request.timeout.ms=${TIMEOUT} — the client gives up before the broker replies,"
  echo "       so it resends records the broker has already written)"
  recreate "$TOPIC"
  produce false
  echo
  echo "${B}[2/4]${N} what is actually in the topic?"
  audit "$TOPIC" read_committed
  echo
  echo "      ${Y}^ every extra copy came from a retry. Your loop sent each id exactly once.${N}"
  echo

  echo "${B}[3/4]${N} same topic, same timeout, ${B}idempotence ON${N}"
  recreate "$TOPIC"
  produce true
  echo
  echo "${B}[4/4]${N} and now?"
  audit "$TOPIC" read_committed
  echo
  echo "      ${G}^ zero extra copies, with the retries still happening.${N}"
  echo "        The broker rejected the resends by producer id + sequence number."
  echo
  echo "      But this only fixed the PRODUCER's duplicates. Next:"
  echo "        ./kafkalab/stage7-delivery-semantics/demo.sh crash"
  echo
  exit 0
fi

# ---------------------------------------------------------------------------------------------
if [[ "$MODE" == "crash" ]]; then
  echo "${B}[1/5]${N} 40 orders in a fresh topic, idempotence on (producer side is solved)"
  recreate "$TOPIC"
  COUNT=40 "${J[@]}" -Didempotence=true -Drequest.timeout=30000 -Dcount=40 \
     kafkalab.stage7.OrderProducer >/tmp/s7-producer.log 2>&1
  todo_check /tmp/s7-producer.log 1 "the producer config"
  rm -f /tmp/s7-charges.txt /tmp/s7-dedupe.txt
  $K kafka-consumer-groups.sh --delete --group payment >/dev/null 2>&1
  echo "      done. charge ledger and dedupe store wiped."
  echo

  echo "${B}[2/5]${N} consumer charges 20 customers, then dies BEFORE committing"
  "${J[@]}" -Dcrash.after=20 -Ddedupe=false kafkalab.stage7.AtLeastOnceConsumer >/tmp/s7-c1.log 2>&1
  todo_check /tmp/s7-c1.log 2 "the consume loop"
  grep -E "simulated crash" /tmp/s7-c1.log | sed 's/^/      /'
  # halt(9) means the process cannot report on itself — but the ledger FILE survived the crash,
  # which is the entire reason it is a file. Read the evidence directly:
  echo "      the ledger survived the crash: $(wc -l < /tmp/s7-charges.txt 2>/dev/null || echo 0) charges on record"
  echo

  echo "${B}[3/5]${N} restart it. it has no committed offset, so it re-reads from the start"
  timeout 60 "${J[@]}" -Ddedupe=false kafkalab.stage7.AtLeastOnceConsumer >/tmp/s7-c2.log 2>&1 &
  pids+=($!); sleep 25; kill "${pids[-1]}" 2>/dev/null; sleep 3
  grep -E "charges written|distinct orders|CHARGED|charged [0-9]" /tmp/s7-c2.log | sed 's/^/      /'
  echo
  echo "      ${R}^ customers charged twice. Kafka's idempotent producer did nothing here —${N}"
  echo "      ${R}  the duplicate was created by YOUR consumer redoing work after a crash.${N}"
  echo

  echo "${B}[4/5]${N} same crash, same restart, but with the idempotency store switched on"
  rm -f /tmp/s7-charges.txt /tmp/s7-dedupe.txt
  $K kafka-consumer-groups.sh --delete --group payment >/dev/null 2>&1
  "${J[@]}" -Dcrash.after=20 -Ddedupe=true kafkalab.stage7.AtLeastOnceConsumer >/tmp/s7-c3.log 2>&1
  todo_check /tmp/s7-c3.log 3a "the dedupe store"
  timeout 60 "${J[@]}" -Ddedupe=true kafkalab.stage7.AtLeastOnceConsumer >/tmp/s7-c4.log 2>&1 &
  pids+=($!); sleep 25; kill "${pids[-1]}" 2>/dev/null; sleep 3
  echo
  grep -E "store has|applied [0-9]+, skipped|charges written|distinct orders|CHARGED" /tmp/s7-c4.log | sed 's/^/      /'
  echo
  echo "      ${G}^ every customer charged exactly once, and the skipped count shows the store${N}"
  echo "      ${G}  catching the replays.${N}"
  echo
  echo "${B}[5/5]${N} the honest caveat"
  echo "      The charge and the mark are two separate writes. Kill the process BETWEEN them"
  echo "      and you are back to a double charge — rarer, not impossible. Closing that gap"
  echo "      needs both writes in ONE transaction of your own database, which is a thing"
  echo "      Kafka cannot give you."
  echo
  exit 0
fi

# ---------------------------------------------------------------------------------------------
if [[ "$MODE" == "tx" ]]; then
  echo "${B}[1/4]${N} fresh input and output topics, 60 orders in"
  recreate "$TOPIC"; recreate "$OUT"
  "${J[@]}" -Didempotence=true -Drequest.timeout=30000 -Dcount=60 \
     kafkalab.stage7.OrderProducer >/tmp/s7-producer.log 2>&1
  todo_check /tmp/s7-producer.log 1 "the producer config"
  $K kafka-consumer-groups.sh --delete --group validator >/dev/null 2>&1
  echo "      60 orders in '$TOPIC'"
  echo

  echo "${B}[2/4]${N} transactional processor, aborting every 2nd transaction on purpose"
  timeout 90 "${J[@]}" -Dabort.every=2 kafkalab.stage7.TransactionalProcessor >/tmp/s7-tx.log 2>&1 &
  pids+=($!); sleep 35; kill "${pids[-1]}" 2>/dev/null; sleep 4
  todo_check /tmp/s7-tx.log 4 "the transaction lifecycle"
  grep -E "^batches [0-9]" /tmp/s7-tx.log | sed 's/^/      /'
  echo

  echo "${B}[3/4]${N} the output topic, read two different ways"
  echo
  echo "      ${B}read_uncommitted${N} — everything physically in the log:"
  audit "$OUT" read_uncommitted
  echo
  echo "      ${B}read_committed${N} — only what a transaction actually committed:"
  audit "$OUT" read_committed
  echo
  echo "      ${Y}^ same bytes on disk, two different answers. The aborted records were never${N}"
  echo "      ${Y}  deleted — the reader is told to stop at the last stable offset instead.${N}"
  echo

  echo "${B}[4/4]${N} what this did NOT do"
  echo "      No credit card was involved. Transactions make consume-transform-produce atomic"
  echo "      INSIDE Kafka: the output records and the consumer's offsets commit together or"
  echo "      not at all. The moment your side effect leaves Kafka, you are back to the"
  echo "      idempotency store from the 'crash' demo."
  echo
  exit 0
fi

echo "${R}unknown mode '$MODE'. use: dupes | crash | tx${N}"
exit 2
