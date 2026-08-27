#!/usr/bin/env bash
# Start the four consumer services + the order service. Ctrl-C kills all of them.
# Run from anywhere: ./kafkalab/stage1-http-fanout/run-all.sh
#
# Five plain JVMs, not five `gradlew run` invocations — one Gradle daemon per process would be
# slow, noisy, and would not die on Ctrl-C. Gradle is asked for the classpath once, then left out.
set -euo pipefail
cd "$(dirname "$0")/../.."          # repo root, where gradlew lives

CP="$(./gradlew -q --console=plain :kafkalab:stage1-http-fanout:printClasspath | tail -1)"

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
echo "all up. try:  ./kafkalab/scripts/send-order.sh 8080 order-1"
echo "kill one consumer to run Experiment B:  kill \$(lsof -ti:9002)"
wait
