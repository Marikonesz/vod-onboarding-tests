package com.vod.onboarding.common.harness;

import com.vod.onboarding.api.mock.EmbeddedMockServer;
import com.vod.onboarding.common.domain.PreferencesState;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

/**
 * Common test base for API and UI tests.
 *
 * <p>Default: embedded mock API + onboarding HTML on {@link #baseUrl}. Switch to a real stack
 * with {@code -Dvod.test.target=real -Dvod.base.url=https://...} (or {@code VOD_BASE_URL}) without
 * changing test classes.
 *
 * <p>Extended by {@link com.vod.onboarding.api.ApiTestBase} and
 * {@link com.vod.onboarding.ui.UiTestBase}.
 */
public abstract class BaseTest {
  /** Non-null only when {@link #usingMock} is {@code true}. */
  protected EmbeddedMockServer mockServer;
  /** API and UI origin for the current test. */
  protected String baseUrl;
  /** Whether this test method uses the embedded mock (vs real deployment). */
  protected boolean usingMock;

  @BeforeEach
  void baseSetUp() throws Exception {
    usingMock = TestEnvironment.useMock();
    if (usingMock) {
      PreferencesState.reset();
      mockServer = new EmbeddedMockServer();
      baseUrl = mockServer.baseUrl();
    } else {
      mockServer = null;
      baseUrl = TestEnvironment.requireRealBaseUrl();
    }
  }

  @AfterEach
  void baseTearDown() {
    if (mockServer != null) {
      mockServer.close();
      mockServer = null;
    }
  }
}
