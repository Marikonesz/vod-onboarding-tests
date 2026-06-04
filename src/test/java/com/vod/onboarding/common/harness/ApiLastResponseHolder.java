package com.vod.onboarding.common.harness;

import com.microsoft.playwright.APIResponse;

/** Thread-local last API response for Allure attachment on failure. */
public final class ApiLastResponseHolder {
  private static final ThreadLocal<LastCall> LAST = new ThreadLocal<>();

  private ApiLastResponseHolder() {}

  public static void record(String method, String url, APIResponse response) {
    if (response == null) {
      return;
    }
    LAST.set(new LastCall(method, url, response.status(), safeText(response)));
  }

  public static LastCall get() {
    return LAST.get();
  }

  public static void clear() {
    LAST.remove();
  }

  private static String safeText(APIResponse response) {
    try {
      return response.text();
    } catch (Exception e) {
      return "<unreadable: " + e.getMessage() + ">";
    }
  }

  public record LastCall(String method, String url, int status, String body) {}
}
