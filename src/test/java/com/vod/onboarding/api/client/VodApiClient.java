package com.vod.onboarding.api.client;

import com.google.gson.JsonObject;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.options.RequestOptions;
import com.vod.onboarding.common.fixtures.JsonSupport;
import com.vod.onboarding.common.harness.ApiLastResponseHolder;
import io.qameta.allure.Step;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Client wrapper for VOD preferences and recommendations API endpoints.
 */
public final class VodApiClient {
  private final APIRequestContext api;
  private final String baseUrl;

  /**
   * Auth state that should be preserved across multiple API calls inside the same test.
   *
   * <p>In Playwright, {@link APIRequestContext} can already maintain cookies. However, this client
   * keeps an explicit lightweight “token/cookie jar” so the prototype can be quickly switched
   * from embedded mock stack to a real backend that may require auth headers + cookie-based
   * sessions.
   */
  private String bearerToken;
  private final Map<String, String> cookies = new LinkedHashMap<>();
  private final Map<String, String> extraHeaders = new LinkedHashMap<>();

  /**
   * @param api Playwright API request context used for HTTP calls
   * @param baseUrl mock server origin (e.g. {@code http://127.0.0.1:<port>})
   */
  public VodApiClient(APIRequestContext api, String baseUrl) {
    this.api = api;
    this.baseUrl = baseUrl;
  }

  /** Sets/updates bearer token (sent as {@code Authorization: Bearer <token>}). */
  public void setBearerToken(String token) {
    this.bearerToken = token;
  }

  /** Sets an additional header applied to every request (except request-specific overrides). */
  public void setExtraHeader(String name, String value) {
    if (value == null) {
      extraHeaders.remove(name);
      return;
    }
    extraHeaders.put(name, value);
  }

  /** Manually sets a cookie in the client-side jar (sent as {@code Cookie: ...}). */
  public void setCookie(String name, String value) {
    cookies.put(name, value);
  }

  /** Absolute URL for {@code GET|POST /v1/profile/{profileId}/vod-preferences}. */
  public String vodPreferencesUrl(String profileId) {
    return baseUrl + "/v1/profile/" + profileId + "/vod-preferences";
  }

  /** Absolute URL for {@code GET /v1/profile/{profileId}/recommendations}. */
  public String recommendationsUrl(String profileId) {
    return baseUrl + "/v1/profile/" + profileId + "/recommendations";
  }

  /** Absolute URL for {@code GET /v1/profile/{profileId}/onboarding-survey}. */
  public String onboardingSurveyUrl(String profileId) {
    return baseUrl + "/v1/profile/" + profileId + "/onboarding-survey";
  }

  /** Absolute URL for {@code GET /v1/profile/{profileId}/survey-movies}. */
  public String surveyMoviesUrl(String profileId, String... genreIds) {
    StringBuilder url = new StringBuilder(baseUrl)
        .append("/v1/profile/")
        .append(profileId)
        .append("/survey-movies");
    if (genreIds != null && genreIds.length > 0) {
      url.append("?genre_ids=").append(String.join(",", genreIds));
    }
    return url.toString();
  }

  /** POST preferences JSON for the given profile. */
  @Step("POST vod-preferences for profile {profileId}")
  public APIResponse postPreferences(String profileId, String json) {
    RequestOptions options =
        RequestOptions.create()
            .setData(json)
            .setHeader("Content-Type", "application/json");
    applyAuth(options);
    String url = vodPreferencesUrl(profileId);
    APIResponse response = api.post(url, options);
    ApiLastResponseHolder.record("POST", url, response);
    updateCookiesFromResponse(response);
    return response;
  }

  /** GET saved preferences for the given profile. */
  @Step("GET vod-preferences for profile {profileId}")
  public APIResponse getPreferences(String profileId) {
    RequestOptions options = RequestOptions.create();
    applyAuth(options);
    String url = vodPreferencesUrl(profileId);
    APIResponse response = api.get(url, options);
    ApiLastResponseHolder.record("GET", url, response);
    updateCookiesFromResponse(response);
    return response;
  }

  /** GET one-time survey eligibility for the given profile. */
  @Step("GET onboarding-survey for profile {profileId}")
  public APIResponse getOnboardingSurvey(String profileId) {
    RequestOptions options = RequestOptions.create();
    applyAuth(options);
    String url = onboardingSurveyUrl(profileId);
    APIResponse response = api.get(url, options);
    ApiLastResponseHolder.record("GET", url, response);
    updateCookiesFromResponse(response);
    return response;
  }

  /** GET movie suggestions filtered by genre ids (query {@code genre_ids}). */
  @Step("GET survey-movies for profile {profileId}")
  public APIResponse getSurveyMovies(String profileId, String... genreIds) {
    RequestOptions options = RequestOptions.create();
    applyAuth(options);
    String url = surveyMoviesUrl(profileId, genreIds);
    APIResponse response = api.get(url, options);
    ApiLastResponseHolder.record("GET", url, response);
    updateCookiesFromResponse(response);
    return response;
  }

  /** GET recommendations for the given profile. */
  @Step("GET recommendations for profile {profileId}")
  public APIResponse getRecommendations(String profileId) {
    RequestOptions options = RequestOptions.create();
    applyAuth(options);
    String url = recommendationsUrl(profileId);
    APIResponse response = api.get(url, options);
    ApiLastResponseHolder.record("GET", url, response);
    updateCookiesFromResponse(response);
    return response;
  }

  /** Parses response body as a JSON object. */
  public JsonObject parseJson(APIResponse response) {
    return JsonSupport.parseObject(response.text());
  }

  private void applyAuth(RequestOptions options) {
    // Extra headers (e.g. X-API-Key) from the test harness/config.
    for (Map.Entry<String, String> e : extraHeaders.entrySet()) {
      options.setHeader(e.getKey(), e.getValue());
    }

    // Token-based auth.
    if (bearerToken != null && !bearerToken.isBlank()) {
      options.setHeader("Authorization", "Bearer " + bearerToken.trim());
    }

    // Cookie-based auth/session.
    if (!cookies.isEmpty()) {
      options.setHeader("Cookie", cookieHeaderValue());
    }
  }

  private String cookieHeaderValue() {
    StringBuilder sb = new StringBuilder();
    boolean first = true;
    for (Map.Entry<String, String> e : cookies.entrySet()) {
      if (!first) {
        sb.append("; ");
      }
      sb.append(e.getKey()).append('=').append(e.getValue());
      first = false;
    }
    return sb.toString();
  }

  /**
   * Best-effort extraction of cookies from {@code Set-Cookie} response header.
   *
   * <p>Prototype note: parsing is intentionally simple (good enough for local mock server and
   * quick backend switching). If your real backend uses complex cookie attributes, keep the
   * cookie jar populated via {@link #setCookie(String, String)} and/or real login flow.
   */
  private void updateCookiesFromResponse(APIResponse response) {
    if (response == null) {
      return;
    }

    // Playwright normalizes header names to lowercase.
    String setCookie = response.headers().get("set-cookie");
    if (setCookie == null || setCookie.isBlank()) {
      return;
    }

    // Naive split by comma; may break for cookies with Expires using comma.
    // This is acceptable for this prototype stage.
    String[] parts = setCookie.split(",");
    for (String part : parts) {
      String trimmed = part.trim();
      int semiIdx = trimmed.indexOf(';');
      if (semiIdx >= 0) {
        trimmed = trimmed.substring(0, semiIdx);
      }
      int eqIdx = trimmed.indexOf('=');
      if (eqIdx <= 0) {
        continue;
      }
      String name = trimmed.substring(0, eqIdx).trim();
      String value = trimmed.substring(eqIdx + 1).trim();
      if (!name.isEmpty()) {
        cookies.put(name, value);
      }
    }
  }
}
