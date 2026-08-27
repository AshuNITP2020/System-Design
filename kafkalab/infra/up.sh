#!/usr/bin/env bash
# Bring the lab cluster up and wait until it actually answers.
#   ./infra/up.sh           single broker  (stages 4, 5, 7, 8)
#   ./infra/up.sh cluster   three brokers  (stage 6)
set -euo pipefail
cd "$(dirname "$0")/.."

MODE="${1:-single}"
case "$MODE" in
  single)  FILE=infra/docker-compose.yml ;;
  cluster) FILE=infra/docker-compose.cluster.yml ;;
  *) echo "usage: ./infra/up.sh [single|cluster]" >&2; exit 2 ;;
esac

# The two files claim the same container names and the same host ports, so only one at a time.
OTHER=$([[ "$MODE" == single ]] && echo infra/docker-compose.cluster.yml || echo infra/docker-compose.yml)
docker compose -f "$OTHER" down --remove-orphans >/dev/null 2>&1 || true

echo "starting ${MODE} cluster from ${FILE} (first run pulls ~600MB)..."
docker compose -f "$FILE" up -d --wait 2>&1 | tail -5 || {
  echo "compose reported a problem; recent broker logs:" >&2
  docker logs --tail 30 kafkalab-kafka1 2>&1 || true
  exit 1
}

echo
echo "waiting for broker to accept client connections..."
for i in $(seq 1 40); do
  if docker exec kafkalab-kafka1 /opt/kafka/bin/kafka-broker-api-versions.sh \
        --bootstrap-server localhost:9092 >/dev/null 2>&1; then
    echo "broker is up."
    break
  fi
  [[ $i -eq 40 ]] && { echo "broker never came up. logs:" >&2; docker logs --tail 40 kafkalab-kafka1; exit 1; }
  sleep 2
done

echo
docker compose -f "$FILE" ps --format 'table {{.Name}}\t{{.Status}}\t{{.Ports}}'
cat <<'EOF'

  live console  ->  http://localhost:8081
  terminal view ->  ./infra/watch.sh
  cli           ->  ./infra/kcli.sh kafka-topics.sh --list
  java clients  ->  bootstrap.servers = localhost:9092

EOF
