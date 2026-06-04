package com.vod.onboarding.api.mock.rules;

import com.vod.onboarding.common.domain.ScenarioState;

/**
 * Validation rules for saving VOD preferences payloads.
 */
public final class VodPreferencesRules {
  private static final int MIN_REQUIRED_GENRES = 3;
  private static final int MIN_REQUIRED_MOVIES = 5;

  public enum ValidationFailure {
    NONE,
    GENRES,
    MOVIES
  }

  private VodPreferencesRules() {}

  public static int minRequiredGenres() {
    return MIN_REQUIRED_GENRES;
  }

  public static int minRequiredMovies() {
    return MIN_REQUIRED_MOVIES;
  }

  public static ValidationFailure validateForSave(ScenarioState.VodPreferencesBody body) {
    if (body == null) {
      return ValidationFailure.GENRES;
    }
    if (body.skipped()) {
      return ValidationFailure.NONE;
    }
    int genres = body.genreIds() == null ? 0 : body.genreIds().size();
    if (genres < MIN_REQUIRED_GENRES) {
      return ValidationFailure.GENRES;
    }
    int movies = body.movieIds() == null ? 0 : body.movieIds().size();
    if (movies < MIN_REQUIRED_MOVIES) {
      return ValidationFailure.MOVIES;
    }
    return ValidationFailure.NONE;
  }

  public static boolean shouldAcceptForSave(ScenarioState.VodPreferencesBody body) {
    return validateForSave(body) == ValidationFailure.NONE;
  }
}
