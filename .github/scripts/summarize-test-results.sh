#!/usr/bin/env bash
# Writes JUnit XML pass rate to the job log and GitHub Actions job summary.
set -euo pipefail

LABEL="${1:-Tests}"
RESULTS_DIR="${2:-build/test-results/test}"

if [[ ! -d "$RESULTS_DIR" ]]; then
  echo "::warning title=${LABEL}::No test results directory (${RESULTS_DIR}). Skipping pass rate summary."
  exit 0
fi

read -r TOTAL FAILURES ERRORS SKIPPED PASSED RATE <<EOF
$(python3 - "$RESULTS_DIR" <<'PY'
import sys
import xml.etree.ElementTree as ET
from pathlib import Path

results_dir = Path(sys.argv[1])
total = failures = errors = skipped = 0
files = list(results_dir.glob("TEST-*.xml"))
if not files:
    print("0 0 0 0 0 0.0")
    sys.exit(0)

for path in files:
    root = ET.parse(path).getroot()
    # Gradle/JUnit5: root is <testsuite> or nested; sum top-level suites
    suites = [root] if root.tag == "testsuite" else root.findall("testsuite")
    if not suites:
        suites = [root]
    for suite in suites:
        total += int(suite.attrib.get("tests", 0))
        failures += int(suite.attrib.get("failures", 0))
        errors += int(suite.attrib.get("errors", 0))
        skipped += int(suite.attrib.get("skipped", 0))

failed = failures + errors
passed = max(total - failed - skipped, 0)
rate = round((passed / total) * 100, 1) if total else 0.0
print(f"{total} {failures} {errors} {skipped} {passed} {rate}")
PY
)
EOF

if [[ "$TOTAL" -eq 0 ]]; then
  echo "::warning title=${LABEL}::No TEST-*.xml files found under ${RESULTS_DIR}."
  exit 0
fi

FAILED=$((FAILURES + ERRORS))
echo "::notice title=${LABEL} pass rate::${PASSED}/${TOTAL} passed (${RATE}%) — failed: ${FAILED}, skipped: ${SKIPPED}"

if [[ -n "${GITHUB_STEP_SUMMARY:-}" ]]; then
  {
    echo "## ${LABEL}"
    echo ""
    echo "| Metric | Count |"
    echo "|--------|------:|"
    echo "| **Pass rate** | **${RATE}%** (${PASSED}/${TOTAL}) |"
    echo "| Passed | ${PASSED} |"
    echo "| Failed | ${FAILED} |"
    echo "| Skipped | ${SKIPPED} |"
    echo "| Total | ${TOTAL} |"
  } >>"$GITHUB_STEP_SUMMARY"
fi

if [[ -n "${GITHUB_OUTPUT:-}" ]]; then
  {
    echo "total=${TOTAL}"
    echo "passed=${PASSED}"
    echo "failed=${FAILED}"
    echo "skipped=${SKIPPED}"
    echo "pass_rate=${RATE}"
  } >>"$GITHUB_OUTPUT"
fi
