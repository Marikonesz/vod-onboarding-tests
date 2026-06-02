package com.vod.onboarding.api.apiTests;

import com.google.gson.JsonObject;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.options.RequestOptions;
import com.vod.onboarding.api.ApiTestBase;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("VOD onboarding")
@Feature("Vod preferences API")
@Tag("api")
/**
 * API regression tests for VOD preferences and recommendation behavior.
 */
class VodPreferencesApiTest extends ApiTestBase {

  @Test
  @TmsLink("VP-001")
 // @Severity(SeverityLevel.CRITICAL)
  @DisplayName("VP-001 POST valid preferences returns 201")
  void postValidPreferences_returns201() {
    String profileId = "profile-vp-001";
    APIResponse response = vodApi.postPreferences(profileId, """
        {"genre_ids":["genre-action","genre-comedy","genre-drama"],
         "movie_ids":["movie-1","movie-2","movie-3","movie-4","movie-5"],
         "skipped":false}
        """);

    assertThat(response.status()).isEqualTo(201);
    JsonObject body = vodApi.parseJson(response);
    assertThat(body.get("saved").getAsBoolean()).isTrue();
    assertThat(body.get("profile_id").getAsString()).isEqualTo(profileId);
  }

  @Test
  @TmsLink("VP-002")
  @Severity(SeverityLevel.CRITICAL)
  @DisplayName("VP-002 GET after POST returns saved genre and movie ids")
  void getAfterPost_returnsSavedData() {
    String profileId = "profile-vp-002";
    vodApi.postPreferences(profileId, """
        {"genre_ids":["genre-action","genre-comedy","genre-scifi"],
         "movie_ids":["movie-1"],"skipped":false}
        """);

    APIResponse get = vodApi.getPreferences(profileId);
    assertThat(get.status()).isEqualTo(200);
    JsonObject body = vodApi.parseJson(get);
    assertThat(body.getAsJsonArray("genre_ids")).hasSize(3);
    assertThat(body.getAsJsonArray("movie_ids").get(0).getAsString()).isEqualTo("movie-1");
  }

  @Test
  @TmsLink("VP-003")
 // @Severity(SeverityLevel.NORMAL)
  @DisplayName("VP-003 POST with fewer than 3 genres returns 400")
  void postTwoGenres_returns400() {
    APIResponse response = vodApi.postPreferences("profile-vp-003", """
        {"genre_ids":["genre-action","genre-comedy"],"movie_ids":[],"skipped":false}
        """);

    assertThat(response.status()).isEqualTo(400);
    JsonObject body = vodApi.parseJson(response);
    assertThat(body.get("error").getAsString()).isEqualTo("validation_error");
    assertThat(body.get("min_required").getAsInt()).isEqualTo(3);
  }

  @Test
  @TmsLink("VP-004")
  @DisplayName("VP-004 POST with zero genres returns 400")
  void postZeroGenres_returns400() {
    APIResponse response = vodApi.postPreferences("profile-vp-004", """
        {"genre_ids":[],"movie_ids":[],"skipped":false}
        """);

    assertThat(response.status()).isEqualTo(400);
  }

  @Test
  @TmsLink("VP-005")
  @Story("Boundary validation")
  @DisplayName("VP-005 POST exactly 3 genres is accepted (boundary)")
  void postExactlyThreeGenres_returns201() {
    APIResponse response = vodApi.postPreferences("profile-vp-005", """
        {"genre_ids":["genre-action","genre-comedy","genre-drama"],
         "movie_ids":[],"skipped":false}
        """);

    assertThat(response.status()).isEqualTo(201);
  }

  @Test
  @TmsLink("VP-006")
  @DisplayName("VP-006 POST skipped true accepts empty genres")
  void postSkipped_returns201() {
    APIResponse response = vodApi.postPreferences("profile-vp-006", """
        {"genre_ids":[],"movie_ids":[],"skipped":true}
        """);

    assertThat(response.status()).isEqualTo(201);
  }

  @Test
  @TmsLink("VP-007")
  @DisplayName("VP-007 recommendations default before preferences saved")
  void recommendations_beforeSave_returnsDefault() {
    String profileId = "profile-vp-007";
    APIResponse response = vodApi.getRecommendations(profileId);

    assertThat(response.status()).isEqualTo(200);
    JsonObject body = vodApi.parseJson(response);
    assertThat(body.get("source").getAsString()).isEqualTo("default");
  }

  @Test
  @TmsLink("VP-008")
  @Severity(SeverityLevel.CRITICAL)
  @DisplayName("VP-008 recommendations personalized after valid save")
  void recommendations_afterSave_returnsPersonalized() {
    String profileId = "profile-vp-008";
    vodApi.postPreferences(profileId, """
        {"genre_ids":["genre-horror","genre-romance","genre-scifi"],
         "movie_ids":["movie-1","movie-2","movie-3","movie-4","movie-5"],
         "skipped":false}
        """);

    APIResponse response = vodApi.getRecommendations(profileId);
    JsonObject body = vodApi.parseJson(response);
    assertThat(body.get("source").getAsString()).isEqualTo("personalized");
    assertThat(body.get("profile_id").getAsString()).isEqualTo(profileId);
  }

  @Test
  @TmsLink("VP-009")
  @DisplayName("VP-009 recommendations stay default when survey skipped")
  void recommendations_afterSkip_returnsDefault() {
    String profileId = "profile-vp-009";
    vodApi.postPreferences(profileId, """
        {"genre_ids":[],"movie_ids":[],"skipped":true}
        """);

    APIResponse response = vodApi.getRecommendations(profileId);
    JsonObject body = vodApi.parseJson(response);
    assertThat(body.get("source").getAsString()).isEqualTo("default");
  }

  @Test
  @TmsLink("VP-010")
  @DisplayName("VP-010 GET empty profile returns empty preferences")
  void getWithoutPost_returnsEmptyPreferences() {
    APIResponse response = vodApi.getPreferences("profile-vp-010");
    assertThat(response.status()).isEqualTo(200);
    JsonObject body = vodApi.parseJson(response);
    assertThat(body.getAsJsonArray("genre_ids")).isEmpty();
    assertThat(body.get("skipped").getAsBoolean()).isFalse();
  }

  @Test
  @TmsLink("VP-011")
  @DisplayName("VP-011 POST invalid JSON returns 400")
  void postInvalidJson_returns400() {
    APIResponse response = apiRequest.post(
        vodApi.vodPreferencesUrl("profile-vp-011"),
        RequestOptions.create().setData("{not-json}").setHeader("Content-Type", "application/json"));

    assertThat(response.status()).isEqualTo(400);
  }

  @Test
  @TmsLink("VP-012")
  @DisplayName("VP-012 overwrite preferences updates GET response")
  void postTwice_secondWinsOnGet() {
    String profileId = "profile-vp-012";
    vodApi.postPreferences(profileId, """
        {"genre_ids":["genre-action","genre-comedy","genre-drama"],"movie_ids":[],"skipped":false}
        """);
    vodApi.postPreferences(profileId, """
        {"genre_ids":["genre-horror","genre-romance","genre-scifi"],
         "movie_ids":["movie-9"],"skipped":false}
        """);

    JsonObject body = vodApi.parseJson(vodApi.getPreferences(profileId));
    assertThat(body.getAsJsonArray("genre_ids").get(0).getAsString()).isEqualTo("genre-horror");
    assertThat(body.getAsJsonArray("movie_ids").get(0).getAsString()).isEqualTo("movie-9");
  }

  @Test
  @TmsLink("VP-013")
  @Story("Boundary validation")
  @DisplayName("VP-013 POST with five movies and three genres (boundary)")
  void postFullMovieSelection_returns201() {
    APIResponse response = vodApi.postPreferences("profile-vp-013", """
        {"genre_ids":["genre-action","genre-comedy","genre-drama"],
         "movie_ids":["movie-1","movie-2","movie-3","movie-4","movie-5"],
         "skipped":false}
        """);

    assertThat(response.status()).isEqualTo(201);
  }
}
