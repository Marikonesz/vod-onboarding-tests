# VOD profile onboarding — AI-first test prototype (Java)

Standalone **Java** project for API-first and minimal UI regression of the profile personalization onboarding flow. API and onboarding page are served by an embedded in-process localhost mock server (no WireMock, no TypeScript test suite).

## Stack

- Java 17, Gradle 8.11, JUnit 5
- Playwright for Java (API + browser)
- Gson (JSON fixtures + test catalog)
- AssertJ, Allure 2.29 (Gradle plugin 3.2)

## Structure

```
src/test/java/com/vod/onboarding/
  api/
    apiTests/       — VodPreferencesApiTest, OnboardingSurveyApiTest, CatalogDrivenApiTest
    client/         — VodApiClient
    mock/           — embedded mock server (vod.test.target=mock)
    ApiTestBase.java
  ui/
    uiTests/        — OnboardingUiTest
    pages/          — OnboardingPage
    UiTestBase.java
  common/
    harness/        — BaseTest, TestEnvironment, BrowserFactory, extensions
    domain/         — ScenarioState (mock, thread-local)
    fixtures/       — builders, assertions, JSON helpers
    catalog/        — validateCatalog, TestRail export
src/test/resources/
  mocks/            — golden JSON
  test-cases/       — TMS catalog (source of truth for TestRail export)
  public/           — prototype onboarding.html
config/allure/      — categories.json (Allure report categories)
docs/
  test-cases/       — mirror of catalog
  contract/         — onboarding-api.md
```

## Prerequisites

**JDK 17** (toolchain 17; text blocks in tests).

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 17)   # macOS
java -version   # should report 17.x
```

```bash
cd vod-onboarding-tests
./gradlew installPlaywright   # Chromium (default UI browser)
```

Compile only (no browser):

```bash
./gradlew compileTestJava
```

## Run tests

```bash
./gradlew test
./gradlew test -Pgroups=api
./gradlew test -Pgroups=ui
# Sequential (debug): ./gradlew test -PsingleThread
# CI-style API shards (2 shards; UI in one job):
./gradlew test -Pgroups=api -PshardIndex=0 -PshardTotal=2
./gradlew test -Pgroups=api -PshardIndex=1 -PshardTotal=2
./gradlew test -Pgroups=ui
```

**Parallel (local):** JUnit 5 concurrent methods in one JVM (`@Execution(CONCURRENT)` on `BaseTest`, `junit-platform.properties`). Use **Run with Gradle** in the IDE, or enable JUnit parallel in the run template.

**CI sharding:** Gradle `includeTestsMatching` assigns whole test **classes** to shards (no fake JUnit “skipped” for other shards).

**IDE — run one class:**

```bash
./gradlew test --tests "com.vod.onboarding.api.apiTests.VodPreferencesApiTest"
./gradlew test --tests "com.vod.onboarding.ui.uiTests.OnboardingUiTest"
```

## CI (GitHub Actions)

**Source of truth:** [`.github/workflows/tests.yml`](.github/workflows/tests.yml) — standard `actions/checkout`, `setup-java`, `setup-gradle`, and `upload-artifact` only (no local composite actions).

| Job | What it does |
|-----|----------------|
| **Validate catalog** | `./gradlew validateCatalog` |
| **API shard 0/2, 1/2** | `./gradlew test -Pgroups=api -PshardIndex=… -PshardTotal=2`; uploads `allure-results-api-shard-*` and `junit-results-api-shard-*` |
| **UI tests** | Playwright Chromium + `./gradlew test -Pgroups=ui`; uploads Allure/JUnit + Playwright traces on failure |
| **Test report** | Download Allure artifacts → install Allure CLI 2.27 → curl `history/*.json` from GitHub Pages → `allure generate --clean` → deploy to **Pages** |

After a run, open the **Test report** job summary for the **Allure** link (`deployment.page_url`).

**Pages setup (once per repo):** Settings → Pages → Build and deployment → Source: **GitHub Actions**.

**Local vs CI Allure:** local = `./gradlew allureReport`; CI = Allure command-line in the **Test report** job only.

## Allure report (local)

```bash
./gradlew test
./gradlew allureReport
```

Open `build/reports/allure-report/allureReport/index.html`. Tests link to TMS via `@TmsLink("VP-001")` etc.

Categories: `config/allure/categories.json` (wired in `build.gradle.kts` — do not duplicate under `src/test/resources`).

**Trends / history (local):**

```bash
./gradlew test
cp -R build/reports/allure-report/allureReport/history build/allure-results/ 2>/dev/null || true
./gradlew allureReport
```

## TestRail export

Canonical catalog: `src/test/resources/test-cases/vod-preferences.json`

```bash
./gradlew exportTestRail
```

CSV output: `build/testrail/vod-preferences.csv`

Agent import: see [docs/integrations/testrail.md](docs/integrations/testrail.md) and skill `catalog-to-testrail`.

## Validate catalog

```bash
./gradlew validateCatalog
./gradlew syncCatalogDocs
```

## Test target: mock (default) vs real

`BaseTest` (via `ApiTestBase` / `UiTestBase`) resolves the origin from `TestEnvironment`:

| Mode | How | `baseUrl` |
|------|-----|-----------|
| **mock** (default) | Embedded `EmbeddedMockServer` on `127.0.0.1` + ephemeral port | `http://127.0.0.1:<port>` |
| **real** | No mock server; hit deployed stack | `-Dvod.base.url` or `VOD_BASE_URL` |

```bash
# Default — full suite against embedded mock
./gradlew test

# Real backend + frontend (same origin; paths must match mock contract)
./gradlew test -Dvod.test.target=real -Dvod.base.url=https://staging.example.com
# or
./gradlew test -PvodTestTarget=real -PvodBaseUrl=https://staging.example.com
```

Mock implements `POST/GET /v1/profile/{id}/vod-preferences`, `GET .../recommendations`, `GET .../onboarding-survey`, `GET .../survey-movies`, and `GET /onboarding` (HTML). Details: [docs/contract/onboarding-api.md](docs/contract/onboarding-api.md).

`ScenarioState` applies only in mock mode (in-memory consistency for GET after POST).

Optional auth for real target: `VOD_BEARER_TOKEN`, `VOD_API_KEY` (never commit).

Tests tagged `real-smoke` are skipped unless `vod.test.target=real` and base URL is set.

## Browser selection (UI tests)

Chromium is the default. Switch engine without code changes:

```bash
./gradlew test -Pgroups=ui -Dvod.browser=firefox
./gradlew test -Pgroups=ui -PvodBrowser=webkit -PvodHeadless=false
```

Install extra browsers: `./gradlew installPlaywright` installs Chromium; run `npx playwright install firefox` (or Playwright CLI from the project classpath) before using non-Chromium engines.

## Playwright trace on failure (UI)

UI tests extend `UiTestBase`, which registers `PlaywrightTraceExtension` via `@RegisterExtension`. By default, each UI test records a trace and, **on failure**, saves a ZIP and attaches it to the Allure report:

- Allure attachment: `Playwright trace` (`.zip`) on the failed test case
- Local copy: `build/playwright-traces/<TestClass>_<method>.zip`
- Disable: `-Dvod.trace.on.failure=false`
- Custom dir: `-Dvod.trace.dir=/path/to/traces`

```bash
# After a failed UI test:
npx playwright show-trace build/playwright-traces/OnboardingUiTest_nextButton_disabledUntilThreeGenres.zip
```

## Docs

- [AI-STRATEGY.md](AI-STRATEGY.md) — framework, AI pipeline, integrations
- [docs/ai-workflow.md](docs/ai-workflow.md) — agent operator steps
- [docs/integrations/](docs/integrations/) — Confluence, Jira, TestRail via MCP
- [docs/contract/onboarding-api.md](docs/contract/onboarding-api.md) — API contract stub
- [PROMPTS.md](PROMPTS.md) — prompt log and AI mistakes table
- [prompts/](prompts/) — templates for agents
- [src/test/resources/test-cases/vod-preferences.json](src/test/resources/test-cases/vod-preferences.json) — test catalog

## Note on prototype UI

`src/test/resources/public/onboarding.html` mirrors the survey state machine for demo purposes. Replace URL/selectors when wiring to the real app; keep `data-testid` hooks where possible.

## AI-generated artifact estimate

Roughly **80%+** of the regression catalog (`vod-preferences.json`), mock handlers, and test code were drafted with Cursor/LLM from the product brief, then corrected via human review (see [PROMPTS.md](PROMPTS.md) mistakes table). UI copy and repo docs are **English-only**; the original assignment brief may be Ukrainian in Confluence.
