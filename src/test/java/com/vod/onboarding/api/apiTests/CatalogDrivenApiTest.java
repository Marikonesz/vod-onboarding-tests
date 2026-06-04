package com.vod.onboarding.api.apiTests;

import com.vod.onboarding.api.ApiTestBase;
import com.vod.onboarding.common.catalog.TestCaseCatalog;
import com.vod.onboarding.common.fixtures.ApiAssertions;
import com.vod.onboarding.common.fixtures.PreferencesRequestBuilder;

import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Sample catalog-driven API checks (catalog is source of truth for case ids).
 */
class CatalogDrivenApiTest extends ApiTestBase {

  static Stream<TestCaseCatalog.TestCase> catalogApiCases() {
    return TestCaseCatalog.load("vod-preferences.json").cases().stream()
        .filter(c -> "api".equals(c.layer()))
        .filter(c -> c.automation_candidate())
        .filter(c -> "VP-001".equals(c.id()) || "VP-006".equals(c.id()));
  }

  @ParameterizedTest(name = "{0}: {1}")
  @MethodSource("catalogApiCases")
  @DisplayName("Catalog-driven API smoke")
  void catalogCase_executesMinimalCheck(TestCaseCatalog.TestCase testCase) {
    switch (testCase.id()) {
      case "VP-001" -> {
        var response =
            vodApi.postPreferences(
                "profile-catalog-001",
                PreferencesRequestBuilder.create()
                    .genreIds("genre-action", "genre-comedy", "genre-drama")
                    .movieIds("movie-1", "movie-2", "movie-3", "movie-4", "movie-5")
                    .skipped(false)
                    .toJson());
        var body = ApiAssertions.assertStatusAndParse(vodApi, response, 201);
        ApiAssertions.assertSaved(body, "profile-catalog-001");
      }
      case "VP-006" -> {
        var response =
            vodApi.postPreferences(
                "profile-catalog-006",
                PreferencesRequestBuilder.create().skipped(true).toJson());
        assertThat(response.status()).isEqualTo(201);
      }
      default -> throw new IllegalStateException("Unhandled catalog id: " + testCase.id());
    }
  }
}
