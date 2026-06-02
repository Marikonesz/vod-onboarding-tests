package com.vod.onboarding.api.mock.handlers;

import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;

import com.vod.onboarding.api.mock.rules.VodPreferencesRules;
import com.vod.onboarding.common.domain.PreferencesState;
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
public final class VodPreferencesHandler {
  private static final Pattern PROFILE_ID = Pattern.compile("^/v1/profile/([^/]+)/");

  private final JsonObject validationError;

  /** Loads validation error fixture used for sub-minimum genre POST bodies. */
  public VodPreferencesHandler() {
    this.validationError = MockJsonLoader.load("errors/validation-min-genres.json");
  }

  /**
   * Dispatches {@code /v1/profile/{id}/vod-preferences} for GET and POST.
   *
   * @param method HTTP method
   * @param path request path (must contain profile id)
   */
  public void handle(HttpExchange exchange, String method, String path) throws IOException {
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
    PreferencesState.VodPreferencesBody body;
    try {
      String raw = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
      body = raw.isBlank()
          ? new PreferencesState.VodPreferencesBody(List.of(), List.of(), false)
          : parseBody(raw);
    } catch (Exception e) {
      HttpResponses.sendJson(exchange, 400, "{\"error\":\"invalid_json\"}");
      return;
    }

    if (!VodPreferencesRules.shouldAcceptForSave(body)) {
      HttpResponses.sendJson(exchange, 400, JsonSupport.GSON.toJson(validationError));
      return;
    }

    PreferencesState.save(profileId, body);
    HttpResponses.sendJson(exchange, 201, String.format("{\"profile_id\":\"%s\",\"saved\":true}", profileId));
  }

  private void handleGet(HttpExchange exchange, String profileId) throws IOException {
    PreferencesState.VodPreferencesBody saved = PreferencesState.get(profileId);
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

  private static PreferencesState.VodPreferencesBody parseBody(String raw) {
    JsonObject node = JsonSupport.parseObject(raw);
    return new PreferencesState.VodPreferencesBody(
        JsonSupport.stringList(node, "genre_ids"),
        JsonSupport.stringList(node, "movie_ids"),
        node.has("skipped") && node.get("skipped").getAsBoolean());
  }

  private static String profileIdFromPath(String path) {
    Matcher matcher = PROFILE_ID.matcher(path);
    return matcher.find() ? matcher.group(1) : null;
  }
}

