# Prompt log — VOD onboarding tests

This file stores your prompts to the AI in chronological order.

## Prompt index

| # | Prompt |
|---|--------|
| 1 | Main task brief (onboarding personalization flow) |
| 2 | `so - refactror this framework 1 use pageobjects 2 use request interceptors 3 keep testcases in jsons to export on testrail 4 create github action workflow file 5 add allure` |
| 3 | `all code must be compilable` |
| 4 | `change maven to gradle` |
| 5 | `prompts.md have to contain my prompts to you due to task` |
| 6 | `Implement the plan as specified...` |
| 7 | `1. keep all my follow promts in PROMPTS.md 2. check compile and fix if needed after any code changes` |
| 8 | `refactor @src/test/java/com/vod/onboarding/pages/OnboardingPage.java it shouldn have any assertations - only locators and actions user can do. also we can use it in chain dont forgot to write my prompts in PROMPTS.md` |
| 9 | `update all tests where pageobject uses with chain.dont forget log prompts` |
| 10 | `but why InterceptorRegistry doesnt work?` |
| 11 | `@PROMPTS.md correct` |
| 12 | `yes - fix. dont use LocalStaticServer only playwright features` |
| 13 | `Target Architecture (No LocalStaticServer) ... Implement the plan as specified` |
| 14 | `Implement Embedded In-Process Mock Server Runtime ...` |
| 15 | `Verification Gates + all test have to be passed` |
| 16 | `suggest what can we change in Architecture - i guess support folder contain a lot diff functional it can be kept more clearly` |
| 17 | `it is not clear. i folder like helpers api clients - relates to api pages relates to ui base test it is not a support etc` |
| 18 | `Support Folder Reorganization (By Concern) — Implement the plan as specified...` |
| 19 | `1/ why do we have legasy folder? 2 why do we have empty uibase test? why do we have api client in support folder 3. what VodPreferencesRules why@.../HttpResponses.java send requests?` |
| 20 | `5.4. mock logic must be incapsulate in one folder 1. legasy and all unused should be removed 2 splitting has to be done 3 finish moving` |
| 21 | `Finalize Concern-Based Refactor — Implement the plan as specified...` |
| 22 | `update @PROMPTS.md with last prompts and mistakes - also add mistake - you didnt remove extra folders and didnt move pages foled to ui` |
| 23 | `ok what about one folder common instead of shared and harness?` |
| 24 | `do it and note into prompts` |
| 25 | `yep` (add JavaDoc to key public methods) |
| 26 | `@ApiTestBase @UiTestBase where is common basetest? if you remove it - add note to mistakes in prompts` |
| 27 | `basetest cannot be called MockServerTestBase - we dont test mockserver we crete a prototype of testing framework with possibility a quick switch to real back and frontend. add this possibilty and add browser fatory instead of srong chromium use(chromium have to be default)` |
| 28 | `do it` (close assignment gaps: one-time survey API, min 5 movies validation, genre-filtered movies, VP-UI-005/006, README 80% note) |
| 29 | `update gitignore if needed` |
| 30 | `resolve conflicts without commit` |
| 31 | `update github action to see passrate in job run` |
| 32 | `add step to allure reporting job run should contain a link to see allure report` / `try to fix` (Pages 404) |
| 33 | `tests should be run in parallel also use sharding on ci` |
| 34 | `lokaly i still face 1 thread` / `you misunderstood - tests should be run in parallel localy as well` |
| 35 | `i meen paralell via junit!!!` |
| 36 | Fix invalid workflow: `matrix.shard + 1` and `env.*` in job `name` |

---

## Full prompt log

### Prompt 1

Full initial task brief (Ukrainian) with requirements for:
- one-time onboarding
- min 3 genres before Next
- choose 5 movies
- skip => default recommendations
- POST `/v1/profile/{profile_id}/vod-preferences`
- impact on recommendations endpoint
- AI-first generation strategy and reporting.

### Prompt 2

```text
so - refactror this framework 1 use pageobjects 2 use request interceptors 3 keep testcases in jsons to export on testrail 4 create github action workflow file 5 add allure
```

### Prompt 3

```text
all code must be compilable
```

### Prompt 4

```text
change maven to gradle
```

### Prompt 5

```text
prompts.md have to contain my prompts to you due to task
```

### Prompt 6

```text
Implement the plan as specified, it is attached for your reference. Do NOT edit the plan file itself.

To-do's from the plan have already been created. Do not create them again. Mark them as in_progress as you work, starting with the first one. Don't stop until you have completed all the to-dos.
```

### Prompt 7

```text
1. keep all my follow promts in PROMPTS.md 2. check compile and fix if needed after any code changes
```

### Prompt 8

```text
refactor @src/test/java/com/vod/onboarding/pages/OnboardingPage.java it shouldn have any assertations - only locators and actions user can do. also we can use it in chain dont forgot to write my prompts in PROMPTS.md
```

### Prompt 9

```text
update all tests where pageobject uses with chain.dont forget log prompts
```

### Prompt 10

```text
but why InterceptorRegistry doesnt work?
```

### Prompt 11

```text
@PROMPTS.md correct
```

### Prompt 12

```text
yes - fix. dont use LocalStaticServer only playwright features
```

### Prompt 13

```text
Target Architecture (No LocalStaticServer)

Implement the plan as specified, it is attached for your reference. Do NOT edit the plan file itself.

To-do's from the plan have already been created. Do not create them again. Mark them as in_progress as you work, starting with the first one. Don't stop until you have completed all the to-dos.
```

### Prompt 14

```text
Implement Embedded In-Process Mock Server Runtime

Implement the plan as specified, it is attached for your reference. Do NOT edit the plan file itself.

To-do's from the plan have already been created. Do not create them again. Mark them as in_progress as you work, starting with the first one. Don't stop until you have completed all the to-dos.
```

### Prompt 15

```text
Verification Gates + all test have to be passed
```

### Prompt 16

```text
suggest what can we change in Architecture - i guess support folder contain a lot diff functional it can be kept more clearly
```

### Prompt 17

```text
it is not clear. i folder like helpers api clients - relates to api pages relates to ui base test it is not a support etc
```

### Prompt 18

```text
Support Folder Reorganization (By Concern)

Implement the plan as specified, it is attached for your reference. Do NOT edit the plan file itself.

To-do's from the plan have already been created. Do not create them again. Mark them as in_progress as you work, starting with the first one. Don't stop until you have completed all the to-dos.
```

### Prompt 19

```text
1/ why do we have legasy folder? 2 why do we have empty uibase test? why do we have api client in support folder 3. what VodPreferencesRules why@src/test/java/com/vod/onboarding/api/mock/handlers/HttpResponses.java send requests?
```

### Prompt 20

```text
5.4. mock logic must be incapsulate in one folder 1. legasy and all unused should be removed 2 splitting has to be done 3 finish moving
```

### Prompt 21

```text
Finalize Concern-Based Refactor

Implement the plan as specified, it is attached for your reference. Do NOT edit the plan file itself.

To-do's from the plan have already been created. Do not create them again. Mark them as in_progress as you work, starting with the first one. Don't stop until you have completed all the to-dos.
```

### Prompt 22

```text
update @PROMPTS.md with last prompts and mistakes - also add mistake - you didnt remove extra folders and didnt move pages foled to ui
```

### Prompt 23

```text
ok what about one folder common instead of shared and harness?
```

### Prompt 24

```text
do it and note into prompts
```

### Prompt 25

```text
yep
```

(Context: add JavaDoc to key public methods on infrastructure classes.)

### Prompt 26

```text
@ApiTestBase @UiTestBase where is common basetest? if you remove it - add note to mistakes in prompts
```

### Prompt 27

```text
basetest cannot be called MockServerTestBase - we dont test mockserver we crete a prototype of testing framework with possibility a quick switch to real back and frontend. add this possibilty and add browser fatory instead of srong chromium use(chromium have to be default)
```

### Prompt 28

```text
do it
```

(Context: implement assignment gap closure — one-time survey endpoint, API min 5 movies, genre-filtered survey-movies, UI tests VP-UI-005/006, contract/catalog updates.)

### Prompt 29–36 (CI, parallel, workflow)

See index rows 29–36. Key user correction (Prompt 35): **parallel via JUnit 5**, not Gradle `maxParallelForks` or extra Gradle shard tasks for local runs.

## AI-generated share (~80%)

Most of `vod-preferences.json`, mock route handlers, and JUnit/Playwright tests were LLM-drafted from the onboarding brief; stabilizing edits are logged in the mistakes table below and in git history. Human gates: `./gradlew test`, `validateCatalog`, PR review.

## Note about AI mistake (server mocking)

- I made a server-mocking architecture mistake by mixing a virtual base host (`app.vod-platform.local`) with API calls that still required real DNS resolution in this environment.
- This led to `ENOTFOUND` / 404 behavior that looked like interceptor failures, even though interceptor logic itself was mostly correct.
- Correct direction: avoid external DNS dependency for test runtime and keep a single deterministic mocking entry (`context.route("**/*", dispatcher)`), serving onboarding HTML and API mocks from Playwright routing with fallback for unrelated requests.

### AI mistakes table

| Area | Mistake | Symptom | Correction |
|---|---|---|---|
| Server mocking | Mixed virtual DNS host with APIRequestContext network resolution | `getaddrinfo ENOTFOUND app.vod-platform.local` and misleading 404s | Use one Playwright dispatcher route (`context.route("**/*", ...)`) and avoid external DNS dependency for test runtime |
| Runtime strategy correction | Over-optimized for route-only mode without stable APIRequestContext networking | `ENOTFOUND`/`ECONNREFUSED` depending on base URL assumptions | Use embedded localhost in-process mock server (`127.0.0.1` + ephemeral port) as single origin for API + onboarding |
| Package refactor (incomplete) | Left `legacy/interceptors/` as archived dead code instead of deleting | Confusing “why legacy?” questions; unused classes still in repo | Remove `legacy/` entirely once `api/mock` is the single mock path |
| Package refactor (incomplete) | `ApiTestBase` / `UiTestBase` were empty wrappers extending one fat `PlaywrightTestBase` | Looked like a split but all setup stayed mixed in parent | Real split: `ApiTestBase` = APIRequestContext only; `UiTestBase` = browser + page helpers; shared mock lifecycle in `harness/` |
| Package refactor (incomplete) | `VodApiClient` remained under `support/` after plan said `api/client/` | API client not discoverable under `api/` | Move to `com.vod.onboarding.api.client.VodApiClient` |
| Package refactor (incomplete) | Did not remove extra/obsolete folders (`support/`, empty `legacy/`) | Stale package map in README; mixed responsibilities | Delete empty `support/`; remove `legacy/`; document final layout |
| Package refactor (incomplete) | Did not move `pages/` under `ui/` (e.g. `ui/pages/OnboardingPage`) | UI page objects still at repo root next to `api/` and `ui/` | Move page objects to `ui/pages/` for symmetry with plan (“pages relates to ui”) |
| Naming / docs | `HttpResponses` sounds like it sends HTTP **requests** | User assumed client-side request code lived in mock handlers | Clarify: it writes HTTP **responses** on `HttpExchange` for the embedded mock server (server → client), not Playwright/API client calls |
| Package organization preference | Initially kept two cross-cutting folders (`shared` + `harness`) after user asked about one `common` folder | Extra mental overhead when locating common code | Consolidate to `common/{domain,fixtures,catalog,harness}` and update imports/docs |
| Test base naming | Deleted `support.PlaywrightTestBase` without an obvious “common base test” next to `ApiTestBase` / `UiTestBase` | Looked like there is no shared parent; only API/UI bases visible in `api/` and `ui/` packages | Shared parent: `common.harness.BaseTest`; document inheritance in both bases + README |
| Test base naming | Named shared base `MockServerTestBase` | Implies tests are *about* the mock server; framework is a prototype with mock/real switch | Rename to `BaseTest`; add `TestEnvironment` (mock vs real) and `BrowserFactory` (Chromium default) |
| Assignment gaps (2025) | API allowed POST with 3 genres and 0 movies; no one-time survey endpoint | Product requires 5 movies when not skipped; survey once per profile | Add `onboarding-survey`, `survey-movies`, `min_required=5` validation, VP-014…017 and VP-UI-005/006 |
| Dynamic movies UI | Static `movie-1`…`movie-6` in HTML broke flow after genre filter | VP-UI-004 could not pick 5 movies for action+comedy+drama | Load movies from `survey-movies`; extend catalog with movie-7/8; UI uses `selectFirstMovies(5)` |
| Parallel execution (wrong tool) | Treated “parallel locally” as **Gradle** `maxParallelForks` / multi-JVM forks and briefly planned extra Gradle shard tasks; user meant **JUnit 5** concurrent test methods | `./gradlew test` still looked like one thread; user had to repeat “parallel via junit” | **One JVM** (`maxParallelForks = 1`); enable JUnit parallel: `@Execution(CONCURRENT)` on `BaseTest`, `junit-platform.properties`, Gradle `systemProperty` for `fixed.parallelism`; `ScenarioState` thread-local. **CI** parallel = matrix jobs + `-PshardIndex`/`-PshardTotal`, not Gradle forks |
| GitHub Actions workflow | Used `matrix.shard + 1` in job `name` and step titles | Workflow invalid: `Unexpected symbol: '+'` in expression | Job/step labels use 0-based `${{ matrix.shard }}` only; no arithmetic in `jobs.<id>.name` |
| GitHub Actions workflow | Used `${{ env.API_SHARD_TOTAL }}` in job `name` | Workflow invalid: `Unrecognized named-value: 'env'` in job name | `env` is not allowed in `jobs.<id>.name`; put `shard_total` in matrix `include` and use `${{ matrix.shard_total }}`; keep workflow-level `env` for steps only (e.g. test-summary) |

## Note about concern-based refactor (follow-up)

After Prompt 20–21, the structure was corrected toward:

- `api/mock/` — all embedded mock server logic
- `api/client/` — `VodApiClient`
- `api/ApiTestBase` + `ui/UiTestBase` — layer-specific harness split
- `common/harness/BaseTest` — common base (`TestEnvironment`: mock default or real `vod.base.url`)
- `common/harness/BrowserFactory` — UI browser launch (Chromium default)
- `common/` — domain, fixtures, catalog
- `legacy/` and `support/` removed

**Test base inheritance:**

```text
BaseTest                    (common.harness)
    ├── ApiTestBase         (api)
    └── UiTestBase          (ui)
```

**Still open vs original plan:** `pages/OnboardingPage` remains at `com.vod.onboarding.ui.pages` path under `ui/pages/` package (moved) — verify README matches.