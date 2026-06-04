package com.vod.onboarding.api;

import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.Playwright;
import com.vod.onboarding.api.client.VodApiClient;
import com.vod.onboarding.common.harness.ApiFailureAttachmentExtension;
import com.vod.onboarding.common.harness.ApiLastResponseHolder;
import com.vod.onboarding.common.harness.BaseTest;
import com.vod.onboarding.common.harness.RealSmokeGuard;
import com.vod.onboarding.common.harness.TestEnvironment;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;

/**
 * API tests: Playwright {@link APIRequestContext} only (no browser).
 */
@ExtendWith(RealSmokeGuard.class)
public abstract class ApiTestBase extends BaseTest {
  @RegisterExtension
  final ApiFailureAttachmentExtension apiFailureAttachment = new ApiFailureAttachmentExtension();

  protected Playwright playwright;
  protected APIRequestContext apiRequest;
  protected VodApiClient vodApi;

  @BeforeEach
  void apiSetUp() {
    ApiLastResponseHolder.clear();
    playwright = Playwright.create();
    apiRequest = playwright.request().newContext();
    vodApi = new VodApiClient(apiRequest, baseUrl);
    applyAuthFromEnvironment();
  }

  @AfterEach
  void apiTearDown() {
    ApiLastResponseHolder.clear();
    if (apiRequest != null) {
      apiRequest.dispose();
    }
    if (playwright != null) {
      playwright.close();
    }
  }

  private void applyAuthFromEnvironment() {
    TestEnvironment.bearerToken().ifPresent(vodApi::setBearerToken);
    TestEnvironment.apiKey()
        .ifPresent(key -> vodApi.setExtraHeader(TestEnvironment.API_KEY_HEADER, key));
  }
}
