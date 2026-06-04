# Prompt: Jira draft from failure

**Input:** Failed test name, `@TmsLink`, Allure report excerpt, Playwright trace summary (UI), last API call attachment (API), expected vs actual from assertion.

**Output:** `build/jira-draft.md` with:
- Title
- Steps to reproduce
- Expected / Actual
- Environment (mock CI / real + URL)
- Links (build URL, Allure, trace path)
- Suggested severity (not P1 by default)

Do **not** create Jira issue until user explicitly approves. Use Jira MCP only after approval.
