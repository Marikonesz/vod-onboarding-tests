package com.vod.onboarding.ui;

import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.vod.onboarding.api.client.VodApiClient;
import com.vod.onboarding.common.harness.BaseTest;
import com.vod.onboarding.common.harness.BrowserFactory;
import com.vod.onboarding.common.harness.PlaywrightTraceExtension;
import com.vod.onboarding.ui.pages.OnboardingPage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import com.vod.onboarding.common.harness.RealSmokeGuard;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;

/**
 * UI tests: browser and onboarding page helpers.
 *
 * <p>Shared origin and mock/real target come from {@link BaseTest}. Browser engine is selected via
 * {@link com.vod.onboarding.common.harness.TestEnvironment} (Chromium by default).
 *
 * <p>Failed UI tests automatically save a Playwright trace ZIP and attach it to Allure when
 * {@link com.vod.onboarding.common.harness.TestEnvironment#traceOnFailure()} is enabled (default).
 *
 * <p>Use {@link #newPage()} and {@link #openOnboarding(Page, String)} to drive the survey UI.
 */
@ExtendWith(RealSmokeGuard.class)
public abstract class UiTestBase extends BaseTest {
  @RegisterExtension
  final PlaywrightTraceExtension playwrightTrace = new PlaywrightTraceExtension();

  /** Playwright instance; closed in {@link #uiTearDown(TestInfo)}. */
  protected Playwright playwright;
  /** Browser from {@link BrowserFactory} (default Chromium). */
  protected Browser browser;
  /** Isolated browser context for each test. */
  protected BrowserContext context;

  @BeforeEach
  void uiSetUp() {
    playwright = Playwright.create();
    browser = BrowserFactory.launch(playwright);
    context = browser.newContext();
    playwrightTrace.startTracing(context);
  }

  @AfterEach
  void uiTearDown(TestInfo testInfo) {
    playwrightTrace.finishTracing(context, testInfo);
    if (context != null) {
      context.close();
    }
    if (browser != null) {
      browser.close();
    }
    if (playwright != null) {
      playwright.close();
    }
  }

  /** Opens a new browser tab for a UI test. */
  protected Page newPage() {
    return context.newPage();
  }

  /** Wraps a Playwright page in the onboarding page object. */
  protected OnboardingPage onboardingPage(Page page) {
    return new OnboardingPage(page);
  }

  /**
   * Starts fluent chain: {@code openOnboarding(newPage(), profileId).selectThreeGenres()...}
   *
   * @param page browser page from {@link #newPage()}
   * @param profileId profile id query parameter for onboarding
   */
  protected OnboardingPage openOnboarding(Page page, String profileId) {
    return onboardingPage(page).open(baseUrl, profileId);
  }

  /** Seeds survey completion via API (e.g. skip) before opening the UI. */
  protected void seedSurveySkipped(String profileId) {
    Playwright pw = Playwright.create();
    try {
      APIRequestContext api = pw.request().newContext();
      new VodApiClient(api, baseUrl)
          .postPreferences(profileId, "{\"genre_ids\":[],\"movie_ids\":[],\"skipped\":true}");
      api.dispose();
    } finally {
      pw.close();
    }
  }
}
