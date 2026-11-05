#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"

mvn -q compile
echo "Compilacion correcta. Ahora ejecute ./ejecutar.sh"
