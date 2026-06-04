# VOD profile onboarding — AI-first test prototype (Java)

Standalone **Java** project for API-first and minimal UI regression of the profile personalization onboarding flow. API and onboarding page are served by an embedded in-process localhost mock server (no WireMock, no TypeScript test suite).

## Stack

- Java 17, Gradle, JUnit 5
- Playwright for Java (API + browser)
- Gson (JSON fixtures + test catalog)
- AssertJ, Allure

## Structure

```
src/test/java/com/vod/onboarding/
  api/            — API tests (@Tag("api"))
  api/            — VodPreferencesApiTest, ApiTestBase
  api/client/     — VodApiClient
  api/mock/       — embedded mock server (used when vod.test.target=mock)
  ui/             — OnboardingUiTest, UiTestBase
  ui/pages/       — OnboardingPage
  common/harness/ — BaseTest, TestEnvironment, BrowserFactory
  common/         — domain, fixtures, catalog
src/test/resources/
  mocks/              — golden JSON
  test-cases/         — TMS catalog (source of truth for TestRail export)
  public/             — prototype onboarding.html
docs/test-cases/      — mirror / reference copy of catalog
```

## Prerequisites

**JDK 17** (required — Java toolchain 17 and text blocks in tests).

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 17)   # macOS
java -version   # should report 17.x
```

```bash
cd vod-onboarding-tests
./gradlew installPlaywright
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
# CI-style API shards (2 shards; UI single job):
./gradlew test -Pgroups=api -PshardIndex=0 -PshardTotal=2
./gradlew test -Pgroups=api -PshardIndex=1 -PshardTotal=2
./gradlew test -Pgroups=ui
```

**Parallel via JUnit 5:** concurrent test methods in one JVM (`@Execution(CONCURRENT)`, `junit-platform.properties`). **CI shards** use Gradle `includeTestsMatching` per class (out-of-shard tests are not counted as JUnit skipped).

**CI workflow:** one **Test report** job — single pass-rate table + one Allure link to `index.html`.

**IDE:** use **Run tests with Gradle**, or enable JUnit parallel in the run template.

```bash
./gradlew test --tests "com.vod.onboarding.api.VodPreferencesApiTest"
./gradlew test --tests "com.vod.onboarding.ui.OnboardingUiTest"
```

## Allure report

```bash
./gradlew test
./gradlew allureReport
```

Open `build/reports/allure-report/allureReport/index.html`. Tests link to TMS via `@TmsLink("VP-001")` etc.

**CI:** API **2** parallel shards + UI **1** job → **Test report** job (one summary table, one Allure `index.html` link). See `.github/workflows/tests.yml`. For a live URL, open **Settings → Pages → Build and deployment → Source: GitHub Actions** in your repo (`https://github.com/<owner>/<repo>/settings/pages`), then re-run the workflow; the job Summary will show **View Allure report**.

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

Mock handlers implement `POST/GET /v1/profile/*/vod-preferences`, `GET .../recommendations`, and `GET /onboarding`. Real environments should expose the same paths (or adapt page objects separately).

`ScenarioState` applies only in mock mode (in-memory consistency for GET after POST).

Optional auth for real target: `VOD_BEARER_TOKEN`, `VOD_API_KEY` (never commit).

Tests tagged `real-smoke` are skipped unless `vod.test.target=real` and base URL is set.

## Browser selection (UI tests)

Chromium is the default. Switch engine without code changes:

```bash
./gradlew test -Pgroups=ui -Dvod.browser=firefox
./gradlew test -Pgroups=ui -PvodBrowser=webkit -PvodHeadless=false
```

Install extra browsers: `./gradlew installPlaywright` installs Chromium; run `npx playwright install firefox` (or use Playwright CLI from the project classpath) before using non-Chromium engines.

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
- [PROMPTS.md](PROMPTS.md) — prompt log
- [prompts/](prompts/) — templates for agents
- [src/test/resources/test-cases/vod-preferences.json](src/test/resources/test-cases/vod-preferences.json) — test catalog

## Note on prototype UI

`src/test/resources/public/onboarding.html` mirrors the survey state machine for demo purposes. Replace URL/selectors when wiring to the real app; keep `data-testid` hooks where possible.

## AI-generated artifact estimate

Roughly **80%+** of the regression catalog (`vod-preferences.json`), mock handlers, and test code were drafted with Cursor/LLM from the product brief, then corrected via human review (see [PROMPTS.md](PROMPTS.md) mistakes table). UI copy and repo docs are **English-only**; the original assignment brief may be Ukrainian in Confluence.
