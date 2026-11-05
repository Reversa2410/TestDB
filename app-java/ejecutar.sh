#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"

mvn -q compile
mvn -q exec:java -Dexec.args="$*"
