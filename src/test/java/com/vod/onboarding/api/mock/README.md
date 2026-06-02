# API mock package

All embedded HTTP mock logic lives here (nothing under `legacy/` or `support/`).

- `EmbeddedMockServer` — binds ephemeral port, routes requests
- `handlers/` — onboarding HTML, vod-preferences, recommendations, HTTP response helpers
- `rules/` — pure validation policy (`VodPreferencesRules`)

Shared in-memory state: `shared.domain.PreferencesState`.
