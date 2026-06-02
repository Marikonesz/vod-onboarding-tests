package com.vod.onboarding.common.harness;

import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Tracing;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;
import org.junit.jupiter.api.extension.TestWatcher;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Saves a Playwright trace ZIP when a UI test fails and attaches it to the Allure report.
 *
 * <p>Register on {@link com.vod.onboarding.ui.UiTestBase} with {@code @RegisterExtension}, then call
 * {@link #startTracing(BrowserContext)} after creating the browser context and
 * {@link #finishTracing(BrowserContext, TestInfo)} before closing the context.
 */
public class PlaywrightTraceExtension implements TestWatcher, TestExecutionExceptionHandler {
  private volatile boolean failed;
  private volatile boolean tracingActive;

  @Override
  public void handleTestExecutionException(ExtensionContext context, Throwable throwable)
      throws Throwable {
    failed = true;
    throw throwable;
  }

  @Override
  public void testFailed(ExtensionContext context, Throwable cause) {
    failed = true;
  }

  @Override
  public void testAborted(ExtensionContext context, Throwable cause) {
    failed = true;
  }

  @Override
  public void testSuccessful(ExtensionContext context) {
    failed = false;
  }

  /**
   * Starts tracing on the context when {@link TestEnvironment#traceOnFailure()} is enabled.
   *
   * @param context browser context for the current test
   */
  public void startTracing(BrowserContext context) {
    if (!TestEnvironment.traceOnFailure()) {
      return;
    }
    failed = false;
    tracingActive = true;
    context
        .tracing()
        .start(
            new Tracing.StartOptions()
                .setScreenshots(true)
                .setSnapshots(true)
                .setSources(true));
  }

  /**
   * Stops tracing, writes a ZIP on failure, and attaches it to Allure.
   *
   * <p>Call this from {@code @AfterEach} before {@link BrowserContext#close()}.
   */
  public void finishTracing(BrowserContext context, TestInfo testInfo) {
    if (!tracingActive || context == null) {
      return;
    }
    try {
      if (failed) {
        Path traceFile = TestEnvironment.traceDir().resolve(traceFileName(testInfo));
        Files.createDirectories(traceFile.getParent());
        context.tracing().stop(new Tracing.StopOptions().setPath(traceFile));
        attachTraceToAllure(traceFile);
      } else {
        context.tracing().stop();
      }
    } catch (IOException e) {
      throw new UncheckedIOException("Failed to save Playwright trace", e);
    } finally {
      tracingActive = false;
    }
  }

  private static void attachTraceToAllure(Path traceFile) {
    try {
      Allure.addAttachment(
          "Playwright trace",
          "application/zip",
          Files.newInputStream(traceFile),
          ".zip");
      attachTextToAllure(
          "Playwright trace — open locally",
          "npx playwright show-trace " + traceFile.toAbsolutePath());
    } catch (IOException e) {
      attachTextToAllure(
          "Playwright trace (attach failed)", "Could not attach trace file: " + e.getMessage());
    }
  }

  private static void attachTextToAllure(String name, String text) {
    byte[] body = (text + System.lineSeparator()).getBytes(StandardCharsets.UTF_8);
    Allure.addAttachment(
        name, "text/plain", new ByteArrayInputStream(body), ".txt");
  }

  private static String traceFileName(TestInfo testInfo) {
    String clazz = testInfo.getTestClass().map(Class::getSimpleName).orElse("unknown");
    String method = testInfo.getTestMethod().map(Method::getName).orElse("unknown");
    return clazz + "_" + method + ".zip";
  }
}
