# Common packages (domain/fixtures/catalog/harness)

This module contains common code used by API mocks, API tests, and UI tests.
Pure helpers stay in `domain`, `fixtures`, and `catalog`; framework lifecycle lives in `harness`.

## Common test base

`com.vod.onboarding.common.harness.BaseTest` is the shared parent for `ApiTestBase` and `UiTestBase`.

- **Mock (default):** starts `EmbeddedMockServer`, resets `PreferencesState`.
- **Real:** uses `-Dvod.base.url=...` or `VOD_BASE_URL`; no embedded mock.

See `TestEnvironment` for all switches (`vod.test.target`, `vod.browser`, `vod.headless`).

## Where is what?

`com.vod.onboarding.common.domain`
- `PreferencesState`: in-memory per-test persistence (mock target only).

`com.vod.onboarding.common.fixtures`
- `MockJsonLoader`: loads JSON fixtures from `src/test/resources/mocks/*`.
- `JsonSupport`: small Gson helpers (parse/copy/extract arrays).

`com.vod.onboarding.common.catalog`
- `TestCaseCatalog`: loads TMS/TestRail catalog JSON from `src/test/resources/test-cases/*`.
- `TestRailExporter`: exports the catalog to CSV under `build/testrail/`.

`com.vod.onboarding.common.harness`
- `BaseTest`: common JUnit lifecycle (mock or real `baseUrl`).
- `TestEnvironment` / `TestTarget`: mock vs real deployment switch.
- `BrowserKind` / `BrowserFactory`: Playwright browser launch (Chromium default).
- `PlaywrightTraceExtension`: trace-on-failure for UI tests (`@RegisterExtension` on `UiTestBase`).
