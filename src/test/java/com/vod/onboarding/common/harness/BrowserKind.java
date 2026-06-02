package com.vod.onboarding.common.harness;

/**
 * Playwright browser engines supported by {@link BrowserFactory}.
 */
public enum BrowserKind {
  CHROMIUM,
  FIREFOX,
  WEBKIT;

  /**
   * Parses {@code vod.browser} / env value; unknown values fall back to {@link #CHROMIUM}.
   *
   * @param value property value (e.g. {@code chromium}, {@code firefox})
   */
  public static BrowserKind fromString(String value) {
    if (value == null || value.isBlank()) {
      return CHROMIUM;
    }
    return switch (value.trim().toLowerCase()) {
      case "firefox", "ff" -> FIREFOX;
      case "webkit", "safari" -> WEBKIT;
      default -> CHROMIUM;
    };
  }
}
