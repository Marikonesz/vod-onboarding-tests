---
name: confluence-to-catalog
description: >-
  Fetch Confluence page via MCP, snapshot to docs/briefs, update contract and
  test catalog JSON. Use when adding tests from Confluence documentation.
---
# Confluence to catalog

1. Fetch page with Atlassian MCP (or read existing `docs/briefs/{feature}.md`).
2. Write snapshot to `docs/briefs/{feature}.md`.
3. Update `docs/contract/{feature}.md` from page facts only.
4. Generate/update `src/test/resources/test-cases/{feature}.json` using `prompts/catalog-from-brief.md`.
5. Run `./gradlew validateCatalog`.

Do not invent API endpoints not supported by contract.
