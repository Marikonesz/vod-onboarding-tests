# Common packages

Shared code for API mocks, API tests, and UI tests.

## Test base

`com.vod.onboarding.common.harness.BaseTest` — mock/real `baseUrl`, `ScenarioState.reset()` in mock mode.

## Where is what?

`common.domain` — `ScenarioState` (in-memory mock state)

`common.fixtures` — `JsonSupport`, `MockJsonLoader`, `PreferencesRequestBuilder`, `ApiAssertions`

`common.catalog` — `TestCaseCatalog`, `TestRailExporter`, `CatalogValidator`

`common.harness` — `BaseTest`, `TestEnvironment`, `BrowserFactory`, `PlaywrightTraceExtension`, `ApiFailureAttachmentExtension`, `RealSmokeGuard`

## Integrations

Agent-driven (MCP/skills): see [docs/integrations/README.md](../../../docs/integrations/README.md).
