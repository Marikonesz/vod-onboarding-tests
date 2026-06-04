package com.vod.onboarding.common.fixtures;

import com.google.gson.JsonObject;

import java.util.Arrays;
import java.util.List;

/** Builds JSON bodies for POST /vod-preferences. */
public final class PreferencesRequestBuilder {
  private List<String> genreIds = List.of();
  private List<String> movieIds = List.of();
  private boolean skipped;

  private PreferencesRequestBuilder() {}

  public static PreferencesRequestBuilder create() {
    return new PreferencesRequestBuilder();
  }

  public PreferencesRequestBuilder genreIds(String... ids) {
    this.genreIds = Arrays.asList(ids);
    return this;
  }

  public PreferencesRequestBuilder movieIds(String... ids) {
    this.movieIds = Arrays.asList(ids);
    return this;
  }

  public PreferencesRequestBuilder skipped(boolean skipped) {
    this.skipped = skipped;
    return this;
  }

  public String toJson() {
    JsonObject node = new JsonObject();
    node.add("genre_ids", JsonSupport.GSON.toJsonTree(genreIds));
    node.add("movie_ids", JsonSupport.GSON.toJsonTree(movieIds));
    node.addProperty("skipped", skipped);
    return JsonSupport.GSON.toJson(node);
  }
}
