package com.vod.onboarding.common.harness;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Playwright;

/**
 * Launches a Playwright browser from {@link BrowserKind} / {@link TestEnvironment} config.
 */
public final class BrowserFactory {
  private BrowserFactory() {}

  /** Uses {@link TestEnvironment#browser()} and {@link TestEnvironment#headless()}. */
  public static Browser launch(Playwright playwright) {
    return launch(playwright, TestEnvironment.browser(), TestEnvironment.headless());
  }

  /**
   * @param playwright active Playwright instance
   * @param kind engine to launch (Chromium is the framework default)
   * @param headless headless launch when {@code true}
   */
  public static Browser launch(Playwright playwright, BrowserKind kind, boolean headless) {
    BrowserType.LaunchOptions options = new BrowserType.LaunchOptions().setHeadless(headless);
    return switch (kind) {
      case CHROMIUM -> playwright.chromium().launch(options);
      case FIREFOX -> playwright.firefox().launch(options);
      case WEBKIT -> playwright.webkit().launch(options);
    };
  }
}
