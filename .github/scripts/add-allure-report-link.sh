#!/usr/bin/env bash
# Appends Allure report links to the GitHub Actions job summary.
set -euo pipefail

LABEL="${1:-Allure}"
REPORT_ARTIFACT="${2:-allure-report}"
RESULTS_ARTIFACT="${3:-allure-results}"

RUN_URL="${GITHUB_SERVER_URL}/${GITHUB_REPOSITORY}/actions/runs/${GITHUB_RUN_ID}"

if [[ -z "${GITHUB_STEP_SUMMARY:-}" ]]; then
  echo "Allure (${LABEL}): ${RUN_URL} → Artifacts: ${REPORT_ARTIFACT}, ${RESULTS_ARTIFACT}"
  exit 0
fi

{
  echo "## ${LABEL}"
  echo ""
  if [[ -n "${ALLURE_PAGE_URL:-}" ]]; then
    echo "### [View Allure report](${ALLURE_PAGE_URL})"
    echo ""
  fi
  echo "- [Workflow run — Artifacts tab](${RUN_URL})"
  echo "  - **${REPORT_ARTIFACT}** — generated HTML (unzip and open \`allureReport/index.html\`)"
  echo "  - **${RESULTS_ARTIFACT}** — raw Allure results"
  if [[ -z "${ALLURE_PAGE_URL:-}" ]]; then
    echo ""
    echo "_Interactive report on GitHub Pages is published by the **publish-allure** job (when enabled for this run)._"
  fi
} >>"$GITHUB_STEP_SUMMARY"

if [[ -n "${ALLURE_PAGE_URL:-}" ]]; then
  echo "::notice title=${LABEL}::View Allure report → ${ALLURE_PAGE_URL}"
else
  echo "::notice title=${LABEL}::Download report artifact → ${RUN_URL}"
fi
