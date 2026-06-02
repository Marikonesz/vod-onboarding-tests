package com.vod.onboarding.api.mock.handlers;

import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;

import com.vod.onboarding.common.domain.PreferencesState;
import com.vod.onboarding.common.fixtures.JsonSupport;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Handles mock recommendations endpoint responses.
 */
public final class RecommendationsHandler {
  private static final Pattern PROFILE_ID = Pattern.compile("^/v1/profile/([^/]+)/");

  private final JsonObject recommendationsDefault;
  private final JsonObject recommendationsPersonalized;

  /**
   * @param recommendationsDefault fixture when profile has no personalized preferences
   * @param recommendationsPersonalized fixture when profile saved valid preferences
   */
  public RecommendationsHandler(JsonObject recommendationsDefault, JsonObject recommendationsPersonalized) {
    this.recommendationsDefault = recommendationsDefault;
    this.recommendationsPersonalized = recommendationsPersonalized;
  }

  /** Handles GET {@code /v1/profile/{id}/recommendations}. */
  public void handle(HttpExchange exchange, String path) throws IOException {
    String profileId = profileIdFromPath(path);
    if (profileId == null) {
      HttpResponses.sendJson(exchange, 404, "{\"error\":\"profile_not_found\"}");
      return;
    }

    JsonObject base = PreferencesState.hasPersonalized(profileId)
        ? recommendationsPersonalized
        : recommendationsDefault;

    JsonObject payload = JsonSupport.copy(base);
    payload.addProperty("profile_id", profileId);
    HttpResponses.sendJson(exchange, 200, JsonSupport.GSON.toJson(payload));
  }

  private static String profileIdFromPath(String path) {
    Matcher matcher = PROFILE_ID.matcher(path);
    return matcher.find() ? matcher.group(1) : null;
  }
}

