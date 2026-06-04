# Prompt: mocks from catalog

**Input:** Approved catalog JSON + contract doc

**Output:**
- Golden JSON under `src/test/resources/mocks/{feature}/`
- `MockRouteHandler` implementation under `api/mock/handlers/`
- Register handler in `EmbeddedMockServer` default registry (or feature-specific registry)
- Use `ScenarioState` for per-test mutable state; reset in `BaseTest`

Match status codes and error bodies in contract. No Playwright route interception.
