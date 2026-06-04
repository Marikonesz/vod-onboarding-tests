---
name: jira-story-to-catalog
description: >-
  Fetch Jira issue via MCP, snapshot to docs/briefs, link jira_key in catalog.
  Use when starting tests from a Jira story.
---
# Jira story to catalog

1. Fetch issue via Jira MCP.
2. Save `docs/briefs/{issueKey}.md` (summary, description, AC).
3. Fetch linked Confluence if present.
4. Update catalog with `jira_key` and cases from AC.
5. Run `./gradlew validateCatalog`.
