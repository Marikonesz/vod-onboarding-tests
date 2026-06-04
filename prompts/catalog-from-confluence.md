# Prompt: catalog from Confluence

1. Use Atlassian MCP to fetch the Confluence page (or read `docs/briefs/{feature}.md` if already exported).
2. Extract acceptance criteria and user flows.
3. Draft/update `docs/contract/{feature}.md` (paths, status codes, JSON fields — do not invent endpoints).
4. Generate `src/test/resources/test-cases/{feature}.json` per `prompts/catalog-from-brief.md`.
5. Ask human to triage ~20% of cases before merging.
