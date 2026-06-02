package com.vod.onboarding.api.mock.rules;

import com.vod.onboarding.common.domain.PreferencesState;

/**
 * Validation rules for saving VOD preferences payloads.
 */
public final class VodPreferencesRules {
  private static final int MIN_REQUIRED_GENRES = 3;

  private VodPreferencesRules() {}

  /** Minimum genre count required when {@code skipped} is false. */
  public static int minRequiredGenres() {
    return MIN_REQUIRED_GENRES;
  }

  /**
   * "Skip" bypasses genre requirements.
   *
   * <p>This is pure policy logic (no HTTP, no JSON IO) so it can be unit-tested easily.
   */
  public static boolean shouldAcceptForSave(PreferencesState.VodPreferencesBody body) {
    if (body == null) {
      return false;
    }
    return body.skipped() || (body.genreIds() != null && body.genreIds().size() >= MIN_REQUIRED_GENRES);
  }
}

