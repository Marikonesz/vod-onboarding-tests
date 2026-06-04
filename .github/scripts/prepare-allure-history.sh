#!/usr/bin/env bash
# Copy previous Allure history into allure-results so the next report keeps trends/charts.
set -euo pipefail

RESULTS_DIR="${1:-build/allure-results}"
CACHE_DIR="${2:-.allure-history}"
PAGES_BASE="${3:-}"

HISTORY_DEST="${RESULTS_DIR}/history"
mkdir -p "$HISTORY_DEST"

copy_history() {
  local src="$1"
  if [[ -d "$src" ]] && [[ -n "$(ls -A "$src" 2>/dev/null)" ]]; then
    cp -R "$src"/. "$HISTORY_DEST"/
    echo "Allure history restored from ${src} ($(find "$HISTORY_DEST" -type f | wc -l | tr -d ' ') files)"
    if [[ -f "${HISTORY_DEST}/history-trend.json" ]]; then
      python3 -c "import json,sys; d=json.load(open('${HISTORY_DEST}/history-trend.json')); print(f'history-trend entries: {len(d) if isinstance(d,list) else 0}')"
    fi
    return 0
  fi
  return 1
}

if copy_history "$CACHE_DIR"; then
  exit 0
fi

if [[ -n "$PAGES_BASE" ]]; then
  PAGES_BASE="${PAGES_BASE%/}/"
  echo "Trying Allure history from GitHub Pages: ${PAGES_BASE}history/"
  for file in history.json history-trend.json duration-trend.json retry-trend.json categories-trend.json; do
    curl -fsSL "${PAGES_BASE}history/${file}" -o "${HISTORY_DEST}/${file}" 2>/dev/null || true
  done
  if [[ -f "${HISTORY_DEST}/history-trend.json" ]]; then
    echo "Allure history restored from Pages"
    exit 0
  fi
fi

echo "No previous Allure history (first run or cache/Pages empty); trends appear after the next successful report run."
