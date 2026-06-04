# Tool integrations

Default: **repo-only** (catalog JSON, Gradle tests, CSV export). Optional: **AI agents** via MCP and Cursor skills.

| System | Direction | Mechanism |
|--------|-----------|-----------|
| Confluence | Read | Agent MCP → `docs/briefs/` |
| Jira | Read / write (gated) | Agent MCP; draft only until human approves |
| TestRail | Write | `./gradlew exportTestRail` + MCP or UI import |
| Allure | Write | `./gradlew test` + `allureReport` |
| GitHub | CI + PR | Actions, `gh` CLI |

See [mcp-setup.md](mcp-setup.md), [jira.md](jira.md), [testrail.md](testrail.md).

Credentials live in Cursor user settings or GitHub Secrets — never in this repository.
