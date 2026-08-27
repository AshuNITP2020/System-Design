#!/usr/bin/env bash
# Live terminal view of the cluster: brokers, the KRaft quorum, every partition's
# leader/replicas/ISR, and consumer-group lag.
#
#   ./infra/watch.sh              # default refresh
#   INTERVAL=1 ./infra/watch.sh   # tighter
#
# Keep this open in a split next to your app. The web console at :8081 is prettier, but THIS is
# the one that teaches you replication, because you watch the ISR list physically shrink when you
# run `docker stop kafkalab-kafka2` in another terminal.
#
# Note on speed: every Kafka CLI call starts a JVM inside the container and costs ~2.5s, so this
# script deliberately makes only TWO of them per refresh (describe-all-topics and
# describe-all-groups) instead of one per topic. The KRaft quorum changes rarely, so it is
# refreshed every 5th cycle. Expect a real refresh to land around 5-6s — the footer prints the
# measured time so you are never guessing.
set -uo pipefail

BROKER="${BROKER:-kafkalab-kafka1}"
INTERVAL="${INTERVAL:-2}"
# kafka1:19092 (the INTERNAL listener), not localhost:9092 — see the note in kcli.sh. Without
# this, anything whose coordinator is broker 2 or 3 is unreachable from inside kafka1.
BOOTSTRAP="${KAFKA_BOOTSTRAP:-kafka1:19092}"

k() { docker exec "$BROKER" "/opt/kafka/bin/$1" --bootstrap-server "$BOOTSTRAP" "${@:2}" 2>/dev/null; }

CYCLE=0
QUORUM_CACHE=""

render() {
  local t0 topics groups
  t0=$(date +%s%N)

  if ! docker ps --format '{{.Names}}' | grep -qx "$BROKER"; then
    printf '\033[H\033[2J'
    echo "  broker container '$BROKER' is not running."
    echo "  start it:  ./infra/up.sh   (or ./infra/up.sh cluster)"
    return
  fi

  # --- the two expensive calls, made once each ---
  topics=$(k kafka-topics.sh --describe)
  groups=$(k kafka-consumer-groups.sh --describe --all-groups)

  # --- quorum: only every 5th cycle ---
  if (( CYCLE % 5 == 0 )); then
    QUORUM_CACHE=$(k kafka-metadata-quorum.sh describe --replication)
  fi
  (( CYCLE++ ))

  # Build the whole frame in a variable, then paint it in one write. Painting incrementally
  # after a screen clear is what makes terminal dashboards flicker.
  local buf=""
  buf+="== kafkalab @ $(date +%H:%M:%S) =============================================="$'\n\n'

  buf+="-- brokers (live) ------------------------------------------------------------"$'\n'
  buf+=$(docker ps --filter 'name=kafkalab-kafka' --format '{{.Names}}  {{.Status}}  {{.Ports}}' \
           | sed 's/0.0.0.0://g; s/, \[::\][^ ]*//g; s/^/   /')$'\n\n'

  buf+="-- KRaft metadata quorum (no ZooKeeper) --------------------------------------"$'\n'
  if [[ -n "$QUORUM_CACHE" ]]; then
    buf+=$(awk 'NR==1{next}
                {printf "   node %-4s logEndOffset %-10s lag %-6s %s\n", $1, $3, $4, $NF}' \
             <<<"$QUORUM_CACHE")$'\n'
  else
    buf+="   (unavailable)"$'\n'
  fi
  buf+=$'\n'

  buf+="-- partitions ---------------------------------------------------------------"$'\n'
  local ptable
  ptable=$(awk '
    /Partition:/ {
      topic=""; part=""; leader=""; reps=""; isr=""
      for (i=1; i<=NF; i++) {
        v = (i<NF) ? $(i+1) : ""
        if (v ~ /:$/) v = ""                      # next label = this value was empty
        if      ($i=="Topic:")    topic=v
        else if ($i=="Partition:") part=v
        else if ($i=="Leader:")   leader=v
        else if ($i=="Replicas:") reps=v
        else if ($i=="Isr:")      isr=v
      }
      if (topic ~ /^__/) next                     # hide internal topics
      nrep = split(reps, a, ","); nisr = split(isr, b, ",")
      if (reps=="") nrep=0
      if (isr=="")  nisr=0
      if (leader=="" || leader=="-1" || leader=="none")
        health = "\033[31mNO LEADER - offline\033[0m"
      else if (nisr < nrep)
        health = sprintf("\033[33mUNDER-REPLICATED %d/%d\033[0m", nisr, nrep)
      else
        health = "\033[32mok\033[0m"
      printf "   %-18s %-3s %-8s %-12s %-12s %s\n", topic, part, leader, reps, (isr==""?"-":isr), health
    }' <<<"$topics")

  if [[ -z "$ptable" ]]; then
    buf+="   no topics yet. create one:"$'\n'
    buf+="   ./infra/kcli.sh kafka-topics.sh --create --topic orders --partitions 3 --replication-factor 1"$'\n'
  else
    buf+=$(printf "   %-18s %-3s %-8s %-12s %-12s %s\n" TOPIC P LEADER REPLICAS ISR HEALTH)$'\n'
    buf+="$ptable"$'\n'
  fi
  buf+=$'\n'

  buf+="-- consumer groups ----------------------------------------------------------"$'\n'
  local gtable
  gtable=$(awk 'NF>=6 && $1!="GROUP" && $1!="Consumer" {
                  lag=$6; flag=""
                  if (lag ~ /^[0-9]+$/ && lag+0 > 0) flag=sprintf("  \033[33m<- lag %d\033[0m", lag)
                  printf "   %-14s %-14s %-3s cur=%-8s end=%-8s lag=%-6s%s\n", $1,$2,$3,$4,$5,$6,flag
                }' <<<"$groups")
  if [[ -z "$gtable" ]]; then
    buf+="   no active groups — start a consumer."$'\n'
  else
    buf+="$gtable"$'\n'
  fi

  local ms=$(( ($(date +%s%N) - t0) / 1000000 ))
  buf+=$'\n'"(refreshed in ${ms}ms · +${INTERVAL}s idle · Ctrl-C to quit · console: http://localhost:8081)"$'\n'

  printf '\033[H\033[2J%b' "$buf"
}

trap 'printf "\033[?25h\n"; exit 0' INT TERM
printf '\033[?25l'
while true; do
  render
  sleep "$INTERVAL"
done
