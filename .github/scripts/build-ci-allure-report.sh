#!/usr/bin/env bash
# Merge shard Allure results, write executor.json, generate report with history.
set -euo pipefail

INCOMING="${1:-incoming-allure}"
RESULTS="${2:-build/allure-results}"
REPORT="${3:-build/reports/allure-report/allureReport}"
CACHE="${4:-.allure-history}"
PAGES_BASE="${ALLURE_PAGES_BASE:-${5:-}}"

bash .github/scripts/merge-allure-results.sh "$INCOMING" "$RESULTS"

if [[ -z "$(find "$RESULTS" -maxdepth 1 -name '*-result.json' -print -quit 2>/dev/null)" ]]; then
  if [[ -n "${GITHUB_ENV:-}" ]]; then
    echo "skip_publish=true" >>"$GITHUB_ENV"
  fi
  echo "No Allure results — skipping report publish"
  exit 0
fi

RUN_URL="${GITHUB_SERVER_URL}/${GITHUB_REPOSITORY}/actions/runs/${GITHUB_RUN_ID}"
cat >"$RESULTS/executor.json" <<EOF
{
  "name": "GitHub Actions",
  "type": "github",
  "url": "${RUN_URL}",
  "buildOrder": ${GITHUB_RUN_NUMBER:-0},
  "buildName": "build #${GITHUB_RUN_NUMBER:-0}",
  "buildUrl": "${RUN_URL}",
  "reportName": "VOD Onboarding Tests #${GITHUB_RUN_NUMBER:-0}",
  "reportUrl": "${PAGES_BASE%/}/index.html"
}
EOF

bash .github/scripts/finalize-allure-report.sh "$RESULTS" "$REPORT" "$CACHE" "$PAGES_BASE"
