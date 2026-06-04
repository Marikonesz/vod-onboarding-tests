#!/usr/bin/env bash
# Generate Allure report with history; re-generate once so trend widgets include the current build.
set -euo pipefail

RESULTS_DIR="${1:-build/allure-results}"
REPORT_DIR="${2:-build/reports/allure-report/allureReport}"
CACHE_DIR="${3:-.allure-history}"
PAGES_BASE="${4:-}"

prepare() {
  bash .github/scripts/prepare-allure-history.sh "$RESULTS_DIR" "$CACHE_DIR" "$PAGES_BASE"
}

generate() {
  ./gradlew allureReport --no-daemon
}

prepare
generate

if [[ -d "${REPORT_DIR}/history" ]]; then
  mkdir -p "${RESULTS_DIR}/history"
  cp -R "${REPORT_DIR}/history"/. "${RESULTS_DIR}/history"/
  echo "Re-generating report so TREND widgets include merged history..."
  generate
fi

bash .github/scripts/save-allure-history.sh "$REPORT_DIR" "$CACHE_DIR"

if [[ -f "${REPORT_DIR}/history/history-trend.json" ]]; then
  python3 -c "import json; d=json.load(open('${REPORT_DIR}/history/history-trend.json')); print(f'Final history-trend entries: {len(d)}')"
else
  echo "::warning::No history-trend.json in report — TREND chart will stay empty until the next CI run."
fi
