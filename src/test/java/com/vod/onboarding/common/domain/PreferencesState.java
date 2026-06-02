package com.vod.onboarding.common.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * In-memory store for mocked profile preferences state.
 */
public final class PreferencesState {
  private static final Map<String, VodPreferencesBody> STORE = new HashMap<>();

  private PreferencesState() {}

  /** Clears all in-memory preferences (call once per test). */
  public static void reset() {
    STORE.clear();
  }

  /** Persists preferences for a profile in the mock store. */
  public static void save(String profileId, VodPreferencesBody body) {
    STORE.put(profileId, body);
  }

  /** Returns saved preferences, or {@code null} if none were stored. */
  public static VodPreferencesBody get(String profileId) {
    return STORE.get(profileId);
  }

  /**
   * Whether recommendations should use the personalized fixture (at least 3 genres and not skipped).
   */
  public static boolean hasPersonalized(String profileId) {
    VodPreferencesBody prefs = STORE.get(profileId);
    if (prefs == null) {
      return false;
    }
    if (prefs.skipped()) {
      return false;
    }
    return prefs.genreIds() != null && prefs.genreIds().size() >= 3;
  }

  /** Parsed POST body for vod-preferences. */
  public record VodPreferencesBody(List<String> genreIds, List<String> movieIds, boolean skipped) {}
}
