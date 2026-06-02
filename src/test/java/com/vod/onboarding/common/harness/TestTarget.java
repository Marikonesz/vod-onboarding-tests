package com.vod.onboarding.common.harness;

/**
 * Where tests run: embedded mock stack (default) or a real deployed backend/frontend.
 */
public enum TestTarget {
  /** In-process mock server serves API + onboarding HTML. */
  MOCK,
  /** Tests hit {@code vod.base.url} / {@code VOD_BASE_URL} (no embedded mock). */
  REAL
}
