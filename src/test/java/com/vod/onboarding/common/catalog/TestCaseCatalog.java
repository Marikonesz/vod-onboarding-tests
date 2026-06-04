package com.vod.onboarding.common.catalog;

import com.vod.onboarding.common.fixtures.JsonSupport;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

/**
 * Typed model and loader for test case catalog JSON files.
 */
public record TestCaseCatalog(String suite, String feature, List<TestCase> cases) {

  /**
   * Loads a catalog from {@code src/test/resources/test-cases/}.
   *
   * @param resourceName file name only (e.g. {@code vod-preferences.json})
   */
  public static TestCaseCatalog load(String resourceName) {
    String path = "/test-cases/" + resourceName;
    try (InputStream in = TestCaseCatalog.class.getResourceAsStream(path)) {
      if (in == null) {
        throw new IllegalArgumentException("Missing test case catalog: " + path);
      }
      try (InputStreamReader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
        return JsonSupport.GSON.fromJson(reader, TestCaseCatalog.class);
      }
    } catch (Exception e) {
      throw new RuntimeException("Failed to load " + path, e);
    }
  }

  /** Finds a test case by id (e.g. {@code VP-001}). */
  public Optional<TestCase> findById(String id) {
    return cases.stream().filter(c -> c.id().equals(id)).findFirst();
  }

  /** One row in the test case catalog. */
  public record TestCase(
      String id,
      String title,
      String type,
      String layer,
      List<String> preconditions,
      List<Step> steps,
      boolean automation_candidate,
      String automated_in,
      String jira_key,
      String confluence_url,
      String testrail_case_id) {}

  /** Single step: action and expected result. */
  public record Step(String action, String expected) {}
}

