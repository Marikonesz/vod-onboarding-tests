package com.vod.onboarding.api.apiTests;

import com.google.gson.JsonObject;
import com.microsoft.playwright.APIResponse;
import com.vod.onboarding.api.ApiTestBase;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("VOD onboarding")
@Feature("Onboarding survey API")
@Tag("api")
class OnboardingSurveyApiTest extends ApiTestBase {

  @Test
  @TmsLink("VP-015")
  @DisplayName("VP-015 new profile shows one-time survey")
  void onboardingSurvey_newProfile_showSurveyTrue() {
    APIResponse response = vodApi.getOnboardingSurvey("profile-vp-015");

    assertThat(response.status()).isEqualTo(200);
    JsonObject body = vodApi.parseJson(response);
    assertThat(body.get("show_survey").getAsBoolean()).isTrue();
    assertThat(body.get("survey_completed").getAsBoolean()).isFalse();
  }

  @Test
  @TmsLink("VP-016")
  @DisplayName("VP-016 survey not shown again after preferences saved")
  void onboardingSurvey_afterSave_showSurveyFalse() {
    String profileId = "profile-vp-016";
    vodApi.postPreferences(profileId, """
        {"genre_ids":[],"movie_ids":[],"skipped":true}
        """);

    APIResponse response = vodApi.getOnboardingSurvey(profileId);
    JsonObject body = vodApi.parseJson(response);
    assertThat(body.get("show_survey").getAsBoolean()).isFalse();
    assertThat(body.get("survey_completed").getAsBoolean()).isTrue();
  }

  @Test
  @TmsLink("VP-017")
  @DisplayName("VP-017 survey movies filtered by selected genres")
  void surveyMovies_filteredByGenres() {
    APIResponse response =
        vodApi.getSurveyMovies("profile-vp-017", "genre-action", "genre-comedy");

    assertThat(response.status()).isEqualTo(200);
    JsonObject body = vodApi.parseJson(response);
    var ids =
        body.getAsJsonArray("movies").asList().stream()
            .map(el -> el.getAsJsonObject().get("id").getAsString())
            .toList();
    assertThat(ids).contains("movie-1", "movie-2", "movie-3");
    assertThat(ids).doesNotContain("movie-4");
  }
}
