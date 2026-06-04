# Prompt: catalog from brief

**Input:** Product brief or `docs/briefs/{feature}.md` + `docs/contract/{feature}.md`

**Output:** `src/test/resources/test-cases/{feature}.json`

Rules:
- English only; UI controls quoted as `'Next'` or `<btn-next>`
- Unique ids (VP-xxx or VP-UI-xxx)
- Fields: `layer` (api|ui), `type`, `automation_candidate`, `automated_in` (ClassName.methodName)
- Optional: `jira_key`, `confluence_url`, `testrail_case_id`
- Steps: action + expected per row

Run `./gradlew validateCatalog` after edits.
