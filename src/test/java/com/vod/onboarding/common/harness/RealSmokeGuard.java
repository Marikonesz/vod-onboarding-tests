package com.vod.onboarding.common.harness;

import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * Skips tests tagged {@code real-smoke} unless {@code vod.test.target=real} and base URL is set.
 */
public final class RealSmokeGuard implements ExecutionCondition {

  @Override
  public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
    boolean realSmoke =
        context.getTags().contains("real-smoke") || context.getTags().contains("real_smoke");
    if (!realSmoke) {
      return ConditionEvaluationResult.enabled("not a real-smoke test");
    }
    if (!TestEnvironment.useMock() && hasRealBaseUrlConfigured()) {
      return ConditionEvaluationResult.enabled("real target configured");
    }
    return ConditionEvaluationResult.disabled(
        "real-smoke requires -Dvod.test.target=real and -Dvod.base.url or VOD_BASE_URL");
  }

  private static boolean hasRealBaseUrlConfigured() {
    String url = System.getProperty(TestEnvironment.PROP_BASE_URL);
    if (url != null && !url.isBlank()) {
      return true;
    }
    url = System.getenv(TestEnvironment.ENV_BASE_URL);
    return url != null && !url.isBlank();
  }
}
