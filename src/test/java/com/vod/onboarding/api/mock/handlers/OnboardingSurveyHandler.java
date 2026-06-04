package com.vod.onboarding.api.mock.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.vod.onboarding.api.mock.MockRouteHandler;
import com.vod.onboarding.common.domain.ScenarioState;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** GET onboarding survey eligibility (one-time screen per profile). */
public final class OnboardingSurveyHandler implements MockRouteHandler {
  private static final Pattern SURVEY_PATH =
      Pattern.compile("^/v1/profile/([^/]+)/onboarding-survey$");

  @Override
  public boolean matches(String path, String method) {
    return "GET".equals(method) && SURVEY_PATH.matcher(path).matches();
  }

  @Override
  public void handle(HttpExchange exchange, String path, String method) throws IOException {
    String profileId = profileIdFromPath(path);
    if (profileId == null) {
      HttpResponses.sendJson(exchange, 404, "{\"error\":\"profile_not_found\"}");
      return;
    }

    boolean completed = ScenarioState.isSurveyCompleted(profileId);
    boolean showSurvey = !completed;
    HttpResponses.sendJson(
        exchange,
        200,
        String.format(
            "{\"profile_id\":\"%s\",\"show_survey\":%s,\"survey_completed\":%s}",
            profileId, showSurvey, completed));
  }

  private static String profileIdFromPath(String path) {
    Matcher matcher = SURVEY_PATH.matcher(path);
    return matcher.matches() ? matcher.group(1) : null;
  }
}
