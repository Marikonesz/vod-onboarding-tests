#!/usr/bin/env bash
# Single workflow summary: one pass-rate table + one Allure link (index.html).
set -euo pipefail

JUNIT_DIR="${1:-merged-junit}"
ALLURE_URL="${2:-}"

read -r TOTAL PASSED FAILED SKIPPED RATE <<EOF
$(python3 - "$JUNIT_DIR" <<'PY'
import sys
import xml.etree.ElementTree as ET
from pathlib import Path

def stats_for(paths):
    total = passed = failed = skipped = 0
    for path in paths:
        root = ET.parse(path).getroot()
        suites = [root] if root.tag == "testsuite" else root.findall("testsuite")
        if not suites:
            suites = [root]
        for suite in suites:
            for tc in suite.findall("testcase"):
                total += 1
                if tc.find("failure") is not None or tc.find("error") is not None:
                    failed += 1
                elif tc.find("skipped") is not None:
                    skipped += 1
                else:
                    passed += 1
    rate = round((passed / total) * 100, 1) if total else 0.0
    return total, passed, failed, skipped, rate

root = Path(sys.argv[1])
if not root.is_dir():
    print("0 0 0 0 0.0")
    sys.exit(0)

all_xml = sorted(root.rglob("TEST-*.xml"))
t, p, f, s, r = stats_for(all_xml)
print(f"{t} {p} {f} {s} {r}")
PY
)
EOF

if [[ "$TOTAL" -eq 0 ]]; then
  echo "::warning title=Test results::No TEST-*.xml under ${JUNIT_DIR} (check junit-results-* artifacts were uploaded)."
fi

echo "::notice title=Pass rate::${PASSED}/${TOTAL} passed (${RATE}%) — failed: ${FAILED}, skipped: ${SKIPPED}"

if [[ -n "${GITHUB_STEP_SUMMARY:-}" ]]; then
  {
    echo "## Test results"
    echo ""
    echo "| Pass rate | Passed | Failed | Skipped | Total |"
    echo "|----------:|-------:|-------:|--------:|------:|"
    echo "| **${RATE}%** | **${PASSED}** | **${FAILED}** | **${SKIPPED}** | **${TOTAL}** |"
    echo ""
    echo "## Allure report"
    echo ""
    if [[ -n "$ALLURE_URL" ]]; then
      LINK="${ALLURE_URL%/}/index.html"
      echo "[View Allure report](${LINK})"
    else
      RUN_URL="${GITHUB_SERVER_URL}/${GITHUB_REPOSITORY}/actions/runs/${GITHUB_RUN_ID}"
      echo "Download artifact **allure-report-combined**, unzip, open \`index.html\`."
      echo ""
      echo "[Workflow artifacts](${RUN_URL}) · [Enable GitHub Pages](${GITHUB_SERVER_URL}/${GITHUB_REPOSITORY}/settings/pages) (Source: **GitHub Actions**) for a hosted report."
    fi
  } >>"$GITHUB_STEP_SUMMARY"
fi
