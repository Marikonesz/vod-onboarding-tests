package com.vod.onboarding.common.fixtures;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Shared Gson parsing and JSON helper utilities.
 */
public final class JsonSupport {
  public static final Gson GSON = new Gson();

  private JsonSupport() {}

  /** Parses a JSON string into a {@link JsonObject}. */
  public static JsonObject parseObject(String json) {
    return JsonParser.parseString(json).getAsJsonObject();
  }

  /** Reads UTF-8 JSON from an input stream into a {@link JsonObject}. */
  public static JsonObject parseResource(InputStream in) {
    try (InputStreamReader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
      return JsonParser.parseReader(reader).getAsJsonObject();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  /** Deep-copies a JSON object via serialize/parse. */
  public static JsonObject copy(JsonObject source) {
    return parseObject(GSON.toJson(source));
  }

  /** Extracts a string array field; missing or null fields yield an empty list. */
  public static List<String> stringList(JsonObject obj, String field) {
    if (!obj.has(field) || obj.get(field).isJsonNull()) {
      return List.of();
    }
    JsonArray array = obj.getAsJsonArray(field);
    List<String> values = new ArrayList<>(array.size());
    for (JsonElement element : array) {
      values.add(element.getAsString());
    }
    return values;
  }
}

