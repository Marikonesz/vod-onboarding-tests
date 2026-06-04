package com.vod.onboarding.common.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * In-memory store for mocked scenario state (preferences per profile; extensible for campaigns).
 */
public final class ScenarioState {
  private static final Map<String, VodPreferencesBody> PREFERENCES = new HashMap<>();
  private static final java.util.Set<String> SURVEY_COMPLETED = new java.util.HashSet<>();

  private ScenarioState() {}

  /** Clears all in-memory state (call once per test). */
  public static void reset() {
    PREFERENCES.clear();
    SURVEY_COMPLETED.clear();
  }

  /** Persists preferences for a profile in the mock store. */
  public static void savePreferences(String profileId, VodPreferencesBody body) {
    PREFERENCES.put(profileId, body);
    markSurveyCompleted(profileId);
  }

  /** Whether the one-time onboarding survey was already completed (save or skip). */
  public static boolean isSurveyCompleted(String profileId) {
    return SURVEY_COMPLETED.contains(profileId);
  }

  /** Marks survey completed without saving preferences (tests only). */
  public static void markSurveyCompleted(String profileId) {
    SURVEY_COMPLETED.add(profileId);
  }

  /** Returns saved preferences, or {@code null} if none were stored. */
  public static VodPreferencesBody getPreferences(String profileId) {
    return PREFERENCES.get(profileId);
  }

  /**
   * Whether recommendations should use the personalized fixture (at least 3 genres and not skipped).
   */
  public static boolean hasPersonalizedRecommendations(String profileId) {
    VodPreferencesBody prefs = PREFERENCES.get(profileId);
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
