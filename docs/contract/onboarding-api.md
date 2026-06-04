# Onboarding API contract (prototype / mock)

Source of truth for automation until a real OpenAPI spec is available. Mock handlers and API tests must match this document.

## Base URL

- Mock: ephemeral `http://127.0.0.1:<port>` (see `EmbeddedMockServer`)
- Real (optional): `-Dvod.base.url` / `VOD_BASE_URL`

## Endpoints

### `GET /onboarding` | `GET /onboarding.html`

Serves prototype HTML for UI smoke tests.

| Response | Notes |
|----------|--------|
| 200 | `text/html`; query `profile_id` used by page script |

### `POST /v1/profile/{profileId}/vod-preferences`

Save profile personalization survey.

**Request body (JSON):**

| Field | Type | Rules |
|-------|------|--------|
| `genre_ids` | string[] | Required unless `skipped=true`; if not skipped, length ≥ 3 |
| `movie_ids` | string[] | Required unless `skipped=true`; if not skipped, length ≥ 5 |
| `skipped` | boolean | If `true`, empty `genre_ids` and `movie_ids` allowed |

**Responses:**

| Status | Body |
|--------|------|
| 201 | `{"profile_id":"<id>","saved":true}` |
| 400 | `{"error":"invalid_json"}` — malformed JSON |
| 400 | `{"error":"validation_error","field":"genre_ids","min_required":3,...}` — `mocks/errors/validation-min-genres.json` |
| 400 | `{"error":"validation_error","field":"movie_ids","min_required":5,...}` — `mocks/errors/validation-min-movies.json` |
| 404 | `{"error":"profile_not_found"}` — bad path |
| 405 | `{"error":"method_not_allowed"}` |

### `GET /v1/profile/{profileId}/vod-preferences`

| Status | Body |
|--------|------|
| 200 | `{"profile_id","genre_ids","movie_ids","skipped"}` — empty arrays if never saved |

### `GET /v1/profile/{profileId}/onboarding-survey`

One-time survey eligibility (prototype models “show start screen once per profile”).

| Status | Body |
|--------|------|
| 200 | `{"profile_id","show_survey":boolean,"survey_completed":boolean}` — `show_survey` is false after first successful `POST` vod-preferences |

### `GET /v1/profile/{profileId}/survey-movies`

Movies for step 2 filtered by selected genres.

| Query | Rules |
|-------|--------|
| `genre_ids` | Comma-separated genre ids; returns movies whose `genre_ids` intersect the query |

| Status | Body |
|--------|------|
| 200 | `{"profile_id","genre_ids":[],"movies":[{"id","title"}]}` |

Catalog fixture: `mocks/survey-movies-catalog.json`.

### `GET /v1/profile/{profileId}/recommendations`

| Status | Body |
|--------|------|
| 200 | JSON with `source`: `default` or `personalized` (personalized when ≥3 genres saved and not skipped) |

## UI contract (`data-testid`)

| Element | test id |
|---------|---------|
| Next button | `btn-next` |
| Skip button | `btn-skip` |
| Step label | `step-label` |
| Genre list | `genre-list` |
| Movies step | `movies-step` |
| Movie list | `movie-list` |
| Status | `status` |

Visible labels (English): `'Next'`, `'Skip'`, `'Step 1'`, `'Step 2'`, `'Finish'`.

## Change process

1. Update this file and `src/test/resources/test-cases/*.json`.
2. Update mocks under `src/test/resources/mocks/`.
3. Update handlers under `api/mock/handlers/`.
4. Run `./gradlew test` and `./gradlew validateCatalog`.
