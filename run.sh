#!/usr/bin/env bash
# Compile and run one code sample.
#
#   ./run.sh lld/solid/srp/violation
#   ./run.sh lld/solid/srp/refactored
#   ./run.sh --list            show every runnable sample
#   ./run.sh --all             compile every sample (does not run them)
#
# Samples live in code/<path>/ as plain .java files in the default package.
# Some "violation" samples throw on purpose - that is the lesson, and a
# non-zero exit code there is expected.

set -uo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
CODE="$ROOT/code"
BUILD="$ROOT/build"

c_red=$'\e[31m'; c_grn=$'\e[32m'; c_dim=$'\e[2m'; c_bold=$'\e[1m'; c_off=$'\e[0m'

sample_dirs() {
  find "$CODE" -type f -name '*.java' -printf '%h\n' | sort -u
}

usage() {
  sed -n '2,12p' "${BASH_SOURCE[0]}" | sed 's/^# \?//'
}

list_samples() {
  echo "${c_bold}Runnable samples:${c_off}"
  sample_dirs | while read -r d; do
    rel="${d#$CODE/}"
    main=$(grep -l 'static void main' "$d"/*.java 2>/dev/null | head -1)
    if [ -n "$main" ]; then
      printf '  %-42s %s\n' "$rel" "${c_dim}$(basename "$main" .java)${c_off}"
    fi
  done
}

compile() { # $1 = rel path
  local rel="$1" src="$CODE/$1" out="$BUILD/$1"
  [ -d "$src" ] || { echo "${c_red}no such sample: $rel${c_off}" >&2; return 2; }
  mkdir -p "$out"
  javac -d "$out" "$src"/*.java
}

run_one() {
  local rel="${1%/}"
  compile "$rel" || return $?
  local main
  main=$(grep -l 'static void main' "$CODE/$rel"/*.java 2>/dev/null | head -1)
  if [ -z "$main" ]; then
    echo "${c_grn}compiled${c_off} $rel ${c_dim}(no main method - nothing to run)${c_off}"
    return 0
  fi
  local cls; cls="$(basename "$main" .java)"
  echo "${c_dim}--- $rel  ->  java $cls ---${c_off}"
  java -cp "$BUILD/$rel" "$cls"
}

compile_all() {
  local fail=0 n=0
  while read -r d; do
    rel="${d#$CODE/}"; n=$((n+1))
    if compile "$rel" >/dev/null 2>&1; then
      printf '  %s ok%s   %s\n' "$c_grn" "$c_off" "$rel"
    else
      printf '  %s FAIL%s %s\n' "$c_red" "$c_off" "$rel"; compile "$rel"; fail=$((fail+1))
    fi
  done < <(sample_dirs)
  echo
  if [ "$fail" -eq 0 ]; then echo "${c_grn}all $n samples compile${c_off}"
  else echo "${c_red}$fail of $n samples failed${c_off}"; fi
  return "$fail"
}

case "${1:-}" in
  ""|-h|--help) usage; echo; list_samples ;;
  --list|-l)    list_samples ;;
  --all|-a)     compile_all ;;
  --clean)      rm -rf "$BUILD"; echo "removed $BUILD" ;;
  *)            run_one "$1" ;;
esac
