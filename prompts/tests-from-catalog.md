# Prompt: tests from catalog

**Input:** Catalog rows with `automation_candidate: true`

**Output:**
- API: class under `api/apiTests/`, extend `ApiTestBase`, `@Tag("api")`, `@TmsLink`
- UI: class under `ui/uiTests/`, extend `UiTestBase`, `@Tag("ui")`
- Page objects under `ui/pages/` — locators and actions only, no assertions
- Use `PreferencesRequestBuilder`, `ApiAssertions` for API
- Run `./gradlew test`

Do not add Java REST clients for Confluence/Jira/TestRail.
