# VOD profile onboarding — AI-first test prototype (Java)

Standalone **Java** project for API-first and minimal UI regression of the “Персоналізація профілю” onboarding flow. API and onboarding page are served by an embedded in-process localhost mock server (no WireMock, no TypeScript test suite).

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
./gradlew test --tests "com.vod.onboarding.api.VodPreferencesApiTest"
./gradlew test --tests "com.vod.onboarding.ui.OnboardingUiTest"
```

## Allure report

```bash
./gradlew test
./gradlew allureReport
```

Open `build/reports/allure-report/allureReport/index.html`. Tests link to TMS via `@TmsLink("VP-001")` etc.

## TestRail export

Canonical catalog: `src/test/resources/test-cases/vod-preferences.json`

```bash
./gradlew exportTestRail
```

CSV output: `build/testrail/vod-preferences.csv`

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

`PreferencesState` applies only in mock mode (in-memory consistency for GET after POST).

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

- [PROMPTS.md](PROMPTS.md) — prompt log and AI fixes
- [AI-STRATEGY.md](AI-STRATEGY.md) — scaling to promo/start-screen regression
- [src/test/resources/test-cases/vod-preferences.json](src/test/resources/test-cases/vod-preferences.json) — test catalog

## Note on prototype UI

`src/test/resources/public/onboarding.html` mirrors the survey state machine for demo purposes. Replace URL/selectors when wiring to the real app; keep `data-testid` hooks where possible.
