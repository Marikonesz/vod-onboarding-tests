package com.vod.onboarding.api;

import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.Playwright;
import com.vod.onboarding.api.client.VodApiClient;
import com.vod.onboarding.common.harness.BaseTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

/**
 * API tests: Playwright {@link APIRequestContext} only (no browser).
 *
 * <p>Shared origin and mock/real target come from {@link BaseTest}. This class adds
 * API-specific Playwright wiring only.
 *
 * <p>Subclasses use {@link #vodApi} for HTTP calls and {@link #apiRequest} for low-level requests.
 */
public abstract class ApiTestBase extends BaseTest {
  /** Playwright instance; closed in {@link #apiTearDown()}. */
  protected Playwright playwright;
  /** HTTP client without launching a browser. */
  protected APIRequestContext apiRequest;
  /** High-level client for preferences and recommendations endpoints. */
  protected VodApiClient vodApi;

  @BeforeEach
  void apiSetUp() {
    playwright = Playwright.create();
    apiRequest = playwright.request().newContext();
    vodApi = new VodApiClient(apiRequest, baseUrl);
  }

  @AfterEach
  void apiTearDown() {
    if (apiRequest != null) {
      apiRequest.dispose();
    }
    if (playwright != null) {
      playwright.close();
    }
  }
}
