#!/usr/bin/env bash
# Single workflow summary: one pass-rate table + one Allure link (index.html).
set -euo pipefail

JUNIT_DIR="${1:-merged-junit}"
ALLURE_URL="${2:-}"

read -r API_TOTAL API_PASSED API_FAILED API_SKIPPED API_RATE UI_TOTAL UI_PASSED UI_FAILED UI_SKIPPED UI_RATE <<EOF
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
all_xml = list(root.rglob("TEST-*.xml")) if root.is_dir() else []
api_xml = [p for p in all_xml if "junit-results-api" in str(p)]
ui_xml = [p for p in all_xml if "junit-results-ui" in str(p)]

def emit(stats):
    t, p, f, s, r = stats
    print(f"{t} {p} {f} {s} {r}", end=" ")

emit(stats_for(api_xml))
emit(stats_for(ui_xml))
PY
)
EOF

T=$((API_TOTAL + UI_TOTAL))
P=$((API_PASSED + UI_PASSED))
F=$((API_FAILED + UI_FAILED))
S=$((API_SKIPPED + UI_SKIPPED))
if [[ "$T" -gt 0 ]]; then
  RATE=$(python3 -c "print(round(${P} / ${T} * 100, 1))")
else
  RATE="0.0"
fi

echo "::notice title=Pass rate::${P}/${T} passed (${RATE}%) — failed: ${F}, skipped: ${S}"

if [[ -n "${GITHUB_STEP_SUMMARY:-}" ]]; then
  {
    echo "## Test results"
    echo ""
    echo "| Suite | Pass rate | Passed | Failed | Skipped | Total |"
    echo "|-------|----------:|-------:|-------:|--------:|------:|"
    echo "| API | ${API_RATE}% | ${API_PASSED} | ${API_FAILED} | ${API_SKIPPED} | ${API_TOTAL} |"
    echo "| UI | ${UI_RATE}% | ${UI_PASSED} | ${UI_FAILED} | ${UI_SKIPPED} | ${UI_TOTAL} |"
    echo "| **All** | **${RATE}%** | **${P}** | **${F}** | **${S}** | **${T}** |"
    echo ""
    echo "## Allure report"
    echo ""
    if [[ -n "$ALLURE_URL" ]]; then
      LINK="${ALLURE_URL%/}/index.html"
      echo "### [View Allure report](${LINK})"
    else
      RUN_URL="${GITHUB_SERVER_URL}/${GITHUB_REPOSITORY}/actions/runs/${GITHUB_RUN_ID}"
      echo "Download artifact **allure-report-combined**, unzip, open \`index.html\`."
      echo ""
      echo "[Workflow artifacts](${RUN_URL}) · [Enable GitHub Pages](${GITHUB_SERVER_URL}/${GITHUB_REPOSITORY}/settings/pages) (Source: **GitHub Actions**) for a hosted report."
    fi
  } >>"$GITHUB_STEP_SUMMARY"
fi
