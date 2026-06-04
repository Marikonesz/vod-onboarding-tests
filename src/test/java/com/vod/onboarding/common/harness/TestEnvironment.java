package com.vod.onboarding.common.harness;

/**
 * Framework runtime switches (system properties and environment variables).
 *
 * <p>Mock mode (default): embedded server + in-memory {@code ScenarioState}.
 *
 * <p>Real mode: point tests at staging/production-like stack without code changes.
 *
 * <pre>
 *   # default — mock on localhost
 *   ./gradlew test
 *
 *   # real backend + frontend (same origin)
 *   ./gradlew test -Dvod.test.target=real -Dvod.base.url=https://staging.example.com
 *
 *   # Firefox instead of Chromium (default)
 *   ./gradlew test -Pgroups=ui -Dvod.browser=firefox
 * </pre>
 */
public final class TestEnvironment {
  /** {@code mock} (default) or {@code real}. */
  public static final String PROP_TARGET = "vod.test.target";
  /** Origin when {@link TestTarget#REAL} (trailing slash stripped). */
  public static final String PROP_BASE_URL = "vod.base.url";
  /** Same as {@link #PROP_BASE_URL} when property unset. */
  public static final String ENV_BASE_URL = "VOD_BASE_URL";
  /** {@code chromium} (default), {@code firefox}, or {@code webkit}. */
  public static final String PROP_BROWSER = "vod.browser";
  /** {@code true} (default) or {@code false}. */
  public static final String PROP_HEADLESS = "vod.headless";
  /**
   * When {@code true} (default), UI tests record a trace and save it only on failure under
   * {@link #PROP_TRACE_DIR}.
   */
  public static final String PROP_TRACE_ON_FAILURE = "vod.trace.on.failure";
  /** Output directory for failed-test trace ZIPs (default {@code build/playwright-traces}). */
  public static final String PROP_TRACE_DIR = "vod.trace.dir";
  /** Bearer token for real-target API calls (optional). */
  public static final String PROP_BEARER_TOKEN = "vod.bearer.token";
  public static final String ENV_BEARER_TOKEN = "VOD_BEARER_TOKEN";
  /** API key header value for real-target (optional). */
  public static final String PROP_API_KEY = "vod.api.key";
  public static final String ENV_API_KEY = "VOD_API_KEY";
  public static final String API_KEY_HEADER = "X-API-Key";

  private TestEnvironment() {}

  /** {@link TestTarget#MOCK} unless {@code vod.test.target=real}. */
  public static TestTarget target() {
    String raw = System.getProperty(PROP_TARGET, "mock").trim().toLowerCase();
    return switch (raw) {
      case "real", "live", "integration", "prod", "production" -> TestTarget.REAL;
      default -> TestTarget.MOCK;
    };
  }

  public static boolean useMock() {
    return target() == TestTarget.MOCK;
  }

  /**
   * Base URL for {@link TestTarget#REAL}.
   *
   * @throws IllegalStateException if property and env are missing
   */
  public static String requireRealBaseUrl() {
    String url = System.getProperty(PROP_BASE_URL);
    if (url == null || url.isBlank()) {
      url = System.getenv(ENV_BASE_URL);
    }
    if (url == null || url.isBlank()) {
      throw new IllegalStateException(
          "Real test target requires -D" + PROP_BASE_URL + "=<origin> or " + ENV_BASE_URL);
    }
    return stripTrailingSlash(url.trim());
  }

  /** {@link BrowserKind#CHROMIUM} unless {@code vod.browser} is set. */
  public static BrowserKind browser() {
    return BrowserKind.fromString(System.getProperty(PROP_BROWSER, "chromium"));
  }

  /** Headless unless {@code vod.headless=false}. */
  public static boolean headless() {
    return !"false".equalsIgnoreCase(System.getProperty(PROP_HEADLESS, "true"));
  }

  /**
   * Record Playwright traces for UI tests and persist ZIP only when the test fails.
   *
   * <p>Disable with {@code -Dvod.trace.on.failure=false}.
   */
  public static boolean traceOnFailure() {
    return !"false".equalsIgnoreCase(System.getProperty(PROP_TRACE_ON_FAILURE, "true"));
  }

  /** Directory for trace archives produced on failure. */
  public static java.nio.file.Path traceDir() {
    return java.nio.file.Path.of(System.getProperty(PROP_TRACE_DIR, "build/playwright-traces"));
  }

  /** Bearer token from property or env (empty if unset). */
  public static java.util.Optional<String> bearerToken() {
    return optionalNonBlank(System.getProperty(PROP_BEARER_TOKEN), System.getenv(ENV_BEARER_TOKEN));
  }

  /** API key from property or env (empty if unset). */
  public static java.util.Optional<String> apiKey() {
    return optionalNonBlank(System.getProperty(PROP_API_KEY), System.getenv(ENV_API_KEY));
  }

  private static java.util.Optional<String> optionalNonBlank(String primary, String fallback) {
    if (primary != null && !primary.isBlank()) {
      return java.util.Optional.of(primary.trim());
    }
    if (fallback != null && !fallback.isBlank()) {
      return java.util.Optional.of(fallback.trim());
    }
    return java.util.Optional.empty();
  }

  private static String stripTrailingSlash(String url) {
    while (url.endsWith("/")) {
      url = url.substring(0, url.length() - 1);
    }
    return url;
  }
}
