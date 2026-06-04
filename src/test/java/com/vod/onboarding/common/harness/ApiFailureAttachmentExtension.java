package com.vod.onboarding.common.harness;

import io.qameta.allure.Allure;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;
import org.junit.jupiter.api.extension.TestWatcher;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

/**
 * Attaches last API request/response to Allure when an API test fails.
 *
 * <p>Register on {@link com.vod.onboarding.api.ApiTestBase}.
 */
public class ApiFailureAttachmentExtension implements TestWatcher, TestExecutionExceptionHandler {
  private volatile boolean failed;

  @Override
  public void handleTestExecutionException(ExtensionContext context, Throwable throwable)
      throws Throwable {
    failed = true;
    attachLastApiCall();
    throw throwable;
  }

  @Override
  public void testFailed(ExtensionContext context, Throwable cause) {
    failed = true;
    attachLastApiCall();
  }

  @Override
  public void testSuccessful(ExtensionContext context) {
    failed = false;
  }

  @Override
  public void testAborted(ExtensionContext context, Throwable cause) {
    failed = true;
    attachLastApiCall();
  }

  private static void attachLastApiCall() {
    ApiLastResponseHolder.LastCall last = ApiLastResponseHolder.get();
    if (last == null) {
      return;
    }
    String text =
        "Method: "
            + last.method()
            + "\nURL: "
            + last.url()
            + "\nStatus: "
            + last.status()
            + "\n\nBody:\n"
            + last.body();
    Allure.addAttachment(
        "Last API call",
        "text/plain",
        new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8)),
        ".txt");
  }
}
