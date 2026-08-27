#!/usr/bin/env bash
# Run any Kafka CLI tool inside a broker container — no local Kafka install needed.
#
#   ./infra/kcli.sh kafka-topics.sh --list
#   ./infra/kcli.sh kafka-topics.sh --create --topic orders --partitions 3 --replication-factor 1
#   ./infra/kcli.sh kafka-topics.sh --describe --topic orders
#   ./infra/kcli.sh kafka-consumer-groups.sh --describe --all-groups
#   ./infra/kcli.sh kafka-console-consumer.sh --topic orders --from-beginning
#
# --bootstrap-server is appended automatically. Override the container with BROKER=.
#
# Why the bootstrap address is kafka1:19092 and not localhost:9092: this runs INSIDE the broker
# container. The HOST listener advertises brokers as localhost:9092/9093/9094, which are correct
# from your laptop but meaningless inside a container — localhost:9093 there is nothing. The
# INTERNAL listener advertises kafka1/2/3:19092, which docker DNS resolves on the shared network,
# so all three brokers are reachable. Use localhost:9092 from your Java code on the host instead.
set -euo pipefail

BROKER="${BROKER:-kafkalab-kafka1}"
BOOTSTRAP="${KAFKA_BOOTSTRAP:-kafka1:19092}"

if [[ $# -eq 0 ]]; then
  echo "usage: ./infra/kcli.sh <kafka-tool.sh> [args...]" >&2
  echo "tools available:" >&2
  docker exec "$BROKER" ls /opt/kafka/bin 2>/dev/null | grep '\.sh$' | sed 's/^/  /' >&2
  exit 2
fi

if ! docker ps --format '{{.Names}}' | grep -qx "$BROKER"; then
  echo "error: container '$BROKER' is not running." >&2
  echo "start it with:  ./infra/up.sh        (single broker)" >&2
  echo "            or: ./infra/up.sh cluster  (three brokers)" >&2
  exit 1
fi

TOOL="$1"; shift
docker exec -i "$BROKER" "/opt/kafka/bin/${TOOL}" --bootstrap-server "$BOOTSTRAP" "$@"
