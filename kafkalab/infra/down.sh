#!/usr/bin/env bash
# Stop the lab cluster. Pass --wipe to also delete all log data (topics, offsets, everything).
set -euo pipefail
cd "$(dirname "$0")/.."

ARGS=(down --remove-orphans)
[[ "${1:-}" == "--wipe" ]] && ARGS+=(--volumes) && echo "wiping all cluster data"

for f in infra/docker-compose.yml infra/docker-compose.cluster.yml; do
  docker compose -f "$f" "${ARGS[@]}" 2>/dev/null || true
done
echo "lab cluster stopped."
