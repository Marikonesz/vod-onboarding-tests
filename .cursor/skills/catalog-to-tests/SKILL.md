---
name: catalog-to-tests
description: >-
  Generate mocks, MockRouteHandler, and JUnit tests from approved catalog.
  Use when implementing automation_candidate cases.
---
# Catalog to tests

Follow `prompts/mocks-from-catalog.md` then `prompts/tests-from-catalog.md`.

Run `./gradlew test` and fix failures. UI failures: check Allure trace attachment.
