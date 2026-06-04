package com.vod.onboarding.common.domain;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * In-memory store for mocked scenario state (preferences per profile; extensible for campaigns).
 *
 * <p>Thread-local so API tests can run in parallel ({@code maxParallelForks} and/or JUnit
 * concurrent execution) without cross-test interference.
 */
public final class ScenarioState {
  private static final ThreadLocal<State> LOCAL = ThreadLocal.withInitial(State::new);

  private ScenarioState() {}

  /** Clears state for the current thread (call once per test). */
  public static void reset() {
    LOCAL.get().clear();
  }

  /** Persists preferences for a profile in the mock store. */
  public static void savePreferences(String profileId, VodPreferencesBody body) {
    LOCAL.get().savePreferences(profileId, body);
  }

  /** Whether the one-time onboarding survey was already completed (save or skip). */
  public static boolean isSurveyCompleted(String profileId) {
    return LOCAL.get().isSurveyCompleted(profileId);
  }

  /** Marks survey completed without saving preferences (tests only). */
  public static void markSurveyCompleted(String profileId) {
    LOCAL.get().markSurveyCompleted(profileId);
  }

  /** Returns saved preferences, or {@code null} if none were stored. */
  public static VodPreferencesBody getPreferences(String profileId) {
    return LOCAL.get().getPreferences(profileId);
  }

  /**
   * Whether recommendations should use the personalized fixture (at least 3 genres and not skipped).
   */
  public static boolean hasPersonalizedRecommendations(String profileId) {
    return LOCAL.get().hasPersonalizedRecommendations(profileId);
  }

  /** Parsed POST body for vod-preferences. */
  public record VodPreferencesBody(List<String> genreIds, List<String> movieIds, boolean skipped) {}

  private static final class State {
    private final Map<String, VodPreferencesBody> preferences = new HashMap<>();
    private final Set<String> surveyCompleted = new HashSet<>();

    void clear() {
      preferences.clear();
      surveyCompleted.clear();
    }

    void savePreferences(String profileId, VodPreferencesBody body) {
      preferences.put(profileId, body);
      markSurveyCompleted(profileId);
    }

    boolean isSurveyCompleted(String profileId) {
      return surveyCompleted.contains(profileId);
    }

    void markSurveyCompleted(String profileId) {
      surveyCompleted.add(profileId);
    }

    VodPreferencesBody getPreferences(String profileId) {
      return preferences.get(profileId);
    }

    boolean hasPersonalizedRecommendations(String profileId) {
      VodPreferencesBody prefs = preferences.get(profileId);
      if (prefs == null) {
        return false;
      }
      if (prefs.skipped()) {
        return false;
      }
      return prefs.genreIds() != null && prefs.genreIds().size() >= 3;
    }
  }
}
