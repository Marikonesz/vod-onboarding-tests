#!/usr/bin/env bash
# Persist history/ from the generated report for the next CI run (and cache action).
set -euo pipefail

REPORT_DIR="${1:-build/reports/allure-report/allureReport}"
CACHE_DIR="${2:-.allure-history}"

if [[ ! -d "${REPORT_DIR}/history" ]]; then
  echo "No ${REPORT_DIR}/history — skip saving Allure history"
  exit 0
fi

rm -rf "$CACHE_DIR"
mkdir -p "$CACHE_DIR"
cp -R "${REPORT_DIR}/history"/. "$CACHE_DIR"/
echo "Saved Allure history for next run ($(find "$CACHE_DIR" -type f | wc -l | tr -d ' ') files)"
