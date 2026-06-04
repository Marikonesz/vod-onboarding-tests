#!/usr/bin/env bash
# Merge shard artifacts into build/allure-results (keeps attachments; avoids merge-multiple overwrites).
set -euo pipefail

INCOMING="${1:-incoming-allure}"
DEST="${2:-build/allure-results}"

mkdir -p "$DEST"

if [[ ! -d "$INCOMING" ]]; then
  echo "No ${INCOMING} directory"
  exit 0
fi

count=0
while IFS= read -r -d '' file; do
  cp -f "$file" "$DEST"/
  count=$((count + 1))
done < <(find "$INCOMING" -type f \( \
  -name '*-result.json' -o \
  -name '*-container.json' -o \
  -name '*-attachment.*' -o \
  -name '*.png' -o -name '*.jpg' -o -name '*.txt' -o -name '*.zip' \
  \) -print0)

echo "Merged ${count} Allure file(s) into ${DEST}"
