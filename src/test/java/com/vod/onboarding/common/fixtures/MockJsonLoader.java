package com.vod.onboarding.common.fixtures;

import com.google.gson.JsonObject;

import java.io.InputStream;

/**
 * Loads JSON fixtures from classpath mock resources.
 */
public final class MockJsonLoader {
  private MockJsonLoader() {}

  /**
   * Loads a mock JSON fixture from {@code src/test/resources/mocks/}.
   *
   * @param classpathRelative path under {@code /mocks/} or absolute classpath path
   */
  public static JsonObject load(String classpathRelative) {
    String path = classpathRelative.startsWith("/") ? classpathRelative : "/mocks/" + classpathRelative;
    InputStream in = MockJsonLoader.class.getResourceAsStream(path);
    if (in == null) {
      throw new IllegalArgumentException("Missing mock resource: " + path);
    }
    return JsonSupport.parseResource(in);
  }

  /** Loads a fixture and returns its JSON as a string. */
  public static String loadAsString(String classpathRelative) {
    return JsonSupport.GSON.toJson(load(classpathRelative));
  }
}

