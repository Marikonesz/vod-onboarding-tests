package com.vod.onboarding.api.mock.handlers;

import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.vod.onboarding.api.mock.MockRouteHandler;
import com.vod.onboarding.common.domain.ScenarioState;
import com.vod.onboarding.common.fixtures.JsonSupport;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Handles mock recommendations endpoint responses.
 */
public final class RecommendationsHandler implements MockRouteHandler {
  private static final Pattern PROFILE_PATH =
      Pattern.compile("^/v1/profile/([^/]+)/recommendations$");

  private final JsonObject recommendationsDefault;
  private final JsonObject recommendationsPersonalized;

  public RecommendationsHandler(JsonObject recommendationsDefault, JsonObject recommendationsPersonalized) {
    this.recommendationsDefault = recommendationsDefault;
    this.recommendationsPersonalized = recommendationsPersonalized;
  }

  @Override
  public boolean matches(String path, String method) {
    return "GET".equals(method) && PROFILE_PATH.matcher(path).matches();
  }

  @Override
  public void handle(HttpExchange exchange, String path, String method) throws IOException {
    String profileId = profileIdFromPath(path);
    if (profileId == null) {
      HttpResponses.sendJson(exchange, 404, "{\"error\":\"profile_not_found\"}");
      return;
    }

    JsonObject base =
        ScenarioState.hasPersonalizedRecommendations(profileId)
            ? recommendationsPersonalized
            : recommendationsDefault;

    JsonObject payload = JsonSupport.copy(base);
    payload.addProperty("profile_id", profileId);
    HttpResponses.sendJson(exchange, 200, JsonSupport.GSON.toJson(payload));
  }

  private static String profileIdFromPath(String path) {
    Matcher matcher = PROFILE_PATH.matcher(path);
    return matcher.matches() ? matcher.group(1) : null;
  }
}
