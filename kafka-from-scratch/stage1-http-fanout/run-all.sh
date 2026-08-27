#!/usr/bin/env bash
# Start the four consumer services + the order service. Ctrl-C kills all of them.
# Run from the repo root: ./stage1-http-fanout/run-all.sh
set -euo pipefail
cd "$(dirname "$0")/.."

mvn -q -pl common,stage1-http-fanout -am compile

CP="stage1-http-fanout/target/classes:common/target/classes:$(
  mvn -q -pl stage1-http-fanout dependency:build-classpath -Dmdep.outputFile=/dev/stdout -DincludeScope=runtime 2>/dev/null | tail -1)"

pids=()
start() { java -cp "$CP" "$@" & pids+=($!); }

start kafkalab.stage1.ConsumerService payment   9001
start kafkalab.stage1.ConsumerService inventory 9002
start kafkalab.stage1.ConsumerService email     9003
start kafkalab.stage1.ConsumerService analytics 9004
sleep 1
start kafkalab.stage1.OrderService

trap 'echo; echo "stopping ${#pids[@]} processes"; kill "${pids[@]}" 2>/dev/null || true' EXIT INT TERM
echo
echo "all up. try:  ./scripts/send-order.sh 8080 order-1"
echo "kill one consumer to run Experiment B:  kill \$(lsof -ti:9002)"
wait
