package com.vod.onboarding.api.mock.handlers;

import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.vod.onboarding.api.mock.MockRouteHandler;
import com.vod.onboarding.api.mock.rules.VodPreferencesRules;
import com.vod.onboarding.common.domain.ScenarioState;
import com.vod.onboarding.common.fixtures.JsonSupport;
import com.vod.onboarding.common.fixtures.MockJsonLoader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Handles mock VOD preferences POST and GET endpoints.
 */
public final class VodPreferencesHandler implements MockRouteHandler {
  private static final Pattern PROFILE_PATH =
      Pattern.compile("^/v1/profile/([^/]+)/vod-preferences$");

  private final JsonObject validationErrorGenres;
  private final JsonObject validationErrorMovies;

  public VodPreferencesHandler() {
    this.validationErrorGenres = MockJsonLoader.load("errors/validation-min-genres.json");
    this.validationErrorMovies = MockJsonLoader.load("errors/validation-min-movies.json");
  }

  @Override
  public boolean matches(String path, String method) {
    return ("GET".equals(method) || "POST".equals(method)) && PROFILE_PATH.matcher(path).matches();
  }

  @Override
  public void handle(HttpExchange exchange, String path, String method) throws IOException {
    String profileId = profileIdFromPath(path);
    if (profileId == null) {
      HttpResponses.sendJson(exchange, 404, "{\"error\":\"profile_not_found\"}");
      return;
    }

    if ("POST".equals(method)) {
      handlePost(exchange, profileId);
      return;
    }

    if ("GET".equals(method)) {
      handleGet(exchange, profileId);
      return;
    }

    HttpResponses.sendJson(exchange, 405, "{\"error\":\"method_not_allowed\"}");
  }

  private void handlePost(HttpExchange exchange, String profileId) throws IOException {
    ScenarioState.VodPreferencesBody body;
    try {
      String raw = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
      body =
          raw.isBlank()
              ? new ScenarioState.VodPreferencesBody(List.of(), List.of(), false)
              : parseBody(raw);
    } catch (Exception e) {
      HttpResponses.sendJson(exchange, 400, "{\"error\":\"invalid_json\"}");
      return;
    }

    VodPreferencesRules.ValidationFailure failure = VodPreferencesRules.validateForSave(body);
    if (failure == VodPreferencesRules.ValidationFailure.GENRES) {
      HttpResponses.sendJson(exchange, 400, JsonSupport.GSON.toJson(validationErrorGenres));
      return;
    }
    if (failure == VodPreferencesRules.ValidationFailure.MOVIES) {
      HttpResponses.sendJson(exchange, 400, JsonSupport.GSON.toJson(validationErrorMovies));
      return;
    }

    ScenarioState.savePreferences(profileId, body);
    HttpResponses.sendJson(exchange, 201, String.format("{\"profile_id\":\"%s\",\"saved\":true}", profileId));
  }

  private void handleGet(HttpExchange exchange, String profileId) throws IOException {
    ScenarioState.VodPreferencesBody saved = ScenarioState.getPreferences(profileId);
    if (saved == null) {
      HttpResponses.sendJson(
          exchange,
          200,
          String.format(
              "{\"profile_id\":\"%s\",\"genre_ids\":[],\"movie_ids\":[],\"skipped\":false}",
              profileId));
      return;
    }

    JsonObject node = new JsonObject();
    node.addProperty("profile_id", profileId);
    node.add("genre_ids", JsonSupport.GSON.toJsonTree(saved.genreIds()));
    node.add("movie_ids", JsonSupport.GSON.toJsonTree(saved.movieIds()));
    node.addProperty("skipped", saved.skipped());
    HttpResponses.sendJson(exchange, 200, JsonSupport.GSON.toJson(node));
  }

  private static ScenarioState.VodPreferencesBody parseBody(String raw) {
    JsonObject node = JsonSupport.parseObject(raw);
    return new ScenarioState.VodPreferencesBody(
        JsonSupport.stringList(node, "genre_ids"),
        JsonSupport.stringList(node, "movie_ids"),
        node.has("skipped") && node.get("skipped").getAsBoolean());
  }

  private static String profileIdFromPath(String path) {
    Matcher matcher = PROFILE_PATH.matcher(path);
    return matcher.matches() ? matcher.group(1) : null;
  }
}
