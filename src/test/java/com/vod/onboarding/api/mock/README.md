# API mock (`api/mock`)

Embedded in-process HTTP server for onboarding HTML and VOD API endpoints.

## Layout

- `EmbeddedMockServer` — binds ephemeral port, delegates to `MockRouteRegistry`
- `MockRouteHandler` — implement and register new routes (no monolithic dispatch edits)
- `MockRouteRegistry` — ordered handler list (first match wins)
- `handlers/` — route implementations
- `rules/` — pure validation policy (`VodPreferencesRules`)

## Adding a new feature

1. Add JSON fixtures under `src/test/resources/mocks/{feature}/`.
2. Create a handler class implementing `MockRouteHandler`.
3. Register it in `EmbeddedMockServer.defaultRegistry()` or on a test-specific registry.
4. Update `docs/contract/{feature}.md`.

## State

In-memory scenario state: `common.domain.ScenarioState` (reset in `BaseTest` for mock target).
