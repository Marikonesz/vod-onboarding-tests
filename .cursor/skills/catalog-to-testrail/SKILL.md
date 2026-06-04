---
name: catalog-to-testrail
description: >-
  Export catalog to TestRail CSV and optionally sync via TestRail MCP.
  Use when publishing cases to TestRail.
---
# Catalog to TestRail

1. Run `./gradlew exportTestRail` → `build/testrail/vod-preferences.csv`.
2. If TestRail MCP configured: import/update cases; store returned ids in catalog `testrail_case_id`.
3. Otherwise: document manual CSV import steps for QA.

Git catalog remains source of truth.
