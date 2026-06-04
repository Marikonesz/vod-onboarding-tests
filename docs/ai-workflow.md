# AI agent workflow

Step-by-step operator guide. Assumes Cursor with project rules and skills enabled.

## 1. New feature from Confluence

1. Enable Atlassian MCP ([integrations/mcp-setup.md](integrations/mcp-setup.md)).
2. Invoke skill **confluence-to-catalog** with page URL or id.
3. Agent writes `docs/briefs/{feature}.md` and drafts `docs/contract/{feature}.md`.
4. Human approves contract.
5. Agent updates `src/test/resources/test-cases/{feature}.json`.
6. Run `./gradlew validateCatalog`.

## 2. From Jira story

1. Invoke skill **jira-story-to-catalog** with issue key `PROJ-123`.
2. Agent fetches issue + linked Confluence into `docs/briefs/`.
3. Agent updates catalog with `jira_key` fields.
4. Human triage.

## 3. Generate automation

1. Invoke skill **catalog-to-tests** for cases with `automation_candidate: true`.
2. Agent adds mocks, `MockRouteHandler`, Java tests, page objects.
3. Run `./gradlew test`.
4. Fix failures; UI failures include Playwright trace in Allure.

## 4. CI and reporting

- GitHub Actions: `api-tests` and `ui-tests` jobs (mock only).
- Download Allure artifacts from failed runs.
- `./gradlew exportTestRail` for TestRail CSV.

## 5. Failure → Jira draft

1. Open failed test in Allure (trace + last API call).
2. Invoke skill **ci-failure-to-jira**.
3. Review `build/jira-draft.md`.
4. Approve and ask agent to create Jira via MCP, or paste manually.

## 6. Optional real smoke

Only when staging exists:

```bash
./gradlew test -Dvod.test.target=real -Dvod.base.url=https://staging.example.com -Pgroups=api
```

Tests tagged `real-smoke` use `RealSmokeGuard` (skipped without URL).

Auth: `VOD_BEARER_TOKEN`, `VOD_API_KEY` (never commit).
