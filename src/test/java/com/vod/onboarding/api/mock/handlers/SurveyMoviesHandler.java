package com.vod.onboarding.api.mock.handlers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.vod.onboarding.api.mock.MockRouteHandler;
import com.vod.onboarding.common.fixtures.JsonSupport;
import com.vod.onboarding.common.fixtures.MockJsonLoader;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/** GET survey movie suggestions filtered by selected genre_ids. */
public final class SurveyMoviesHandler implements MockRouteHandler {
  private static final Pattern MOVIES_PATH =
      Pattern.compile("^/v1/profile/([^/]+)/survey-movies$");

  private final JsonObject catalog;

  public SurveyMoviesHandler() {
    this.catalog = MockJsonLoader.load("survey-movies-catalog.json");
  }

  @Override
  public boolean matches(String path, String method) {
    return "GET".equals(method) && MOVIES_PATH.matcher(path).matches();
  }

  @Override
  public void handle(HttpExchange exchange, String path, String method) throws IOException {
    String profileId = profileIdFromPath(path);
    if (profileId == null) {
      HttpResponses.sendJson(exchange, 404, "{\"error\":\"profile_not_found\"}");
      return;
    }

    Set<String> requestedGenres = parseGenreIds(exchange.getRequestURI().getQuery());
    JsonArray moviesOut = new JsonArray();
    for (JsonElement el : catalog.getAsJsonArray("movies")) {
      JsonObject movie = el.getAsJsonObject();
      List<String> movieGenres = JsonSupport.stringList(movie, "genre_ids");
      boolean matches =
          requestedGenres.isEmpty()
              || movieGenres.stream().anyMatch(requestedGenres::contains);
      if (matches) {
        JsonObject item = new JsonObject();
        item.addProperty("id", movie.get("id").getAsString());
        item.addProperty("title", movie.get("title").getAsString());
        moviesOut.add(item);
      }
    }

    JsonObject response = new JsonObject();
    response.addProperty("profile_id", profileId);
    response.add("genre_ids", JsonSupport.GSON.toJsonTree(requestedGenres.stream().sorted().toList()));
    response.add("movies", moviesOut);
    HttpResponses.sendJson(exchange, 200, JsonSupport.GSON.toJson(response));
  }

  private static Set<String> parseGenreIds(String query) {
    if (query == null || query.isBlank()) {
      return Set.of();
    }
    for (String part : query.split("&")) {
      if (!part.startsWith("genre_ids=")) {
        continue;
      }
      String raw = part.substring("genre_ids=".length());
      String decoded = URLDecoder.decode(raw, StandardCharsets.UTF_8);
      if (decoded.isBlank()) {
        return Set.of();
      }
      return Arrays.stream(decoded.split(","))
          .map(String::trim)
          .filter(s -> !s.isEmpty())
          .collect(Collectors.toCollection(HashSet::new));
    }
    return Set.of();
  }

  private static String profileIdFromPath(String path) {
    Matcher matcher = MOVIES_PATH.matcher(path);
    return matcher.matches() ? matcher.group(1) : null;
  }
}
