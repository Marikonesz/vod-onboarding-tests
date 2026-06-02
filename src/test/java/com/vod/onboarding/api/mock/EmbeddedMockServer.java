package com.vod.onboarding.api.mock;

import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import com.vod.onboarding.api.mock.handlers.OnboardingStaticHandler;
import com.vod.onboarding.api.mock.handlers.RecommendationsHandler;
import com.vod.onboarding.api.mock.handlers.VodPreferencesHandler;
import com.vod.onboarding.api.mock.handlers.HttpResponses;
import com.vod.onboarding.common.fixtures.MockJsonLoader;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Embedded in-process mock server for onboarding HTML + VOD API endpoints.
 */
public final class EmbeddedMockServer implements AutoCloseable {
  private final HttpServer server;
  private final String baseUrl;

  private final OnboardingStaticHandler onboardingHandler = new OnboardingStaticHandler();
  private final VodPreferencesHandler vodPreferencesHandler = new VodPreferencesHandler();
  private final RecommendationsHandler recommendationsHandler;

  /** Starts an HTTP server on {@code 127.0.0.1} with an ephemeral port. */
  public EmbeddedMockServer() throws IOException {
    server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
    server.createContext("/", this::dispatch);
    server.setExecutor(null);
    server.start();

    int port = server.getAddress().getPort();
    baseUrl = "http://127.0.0.1:" + port;

    JsonObject recommendationsDefault = MockJsonLoader.load("recommendations-default.json");
    JsonObject recommendationsPersonalized = MockJsonLoader.load("recommendations-personalized.json");
    recommendationsHandler = new RecommendationsHandler(
        recommendationsDefault, recommendationsPersonalized);
  }

  /** Base URL for API and UI tests (e.g. {@code http://127.0.0.1:54321}). */
  public String baseUrl() {
    return baseUrl;
  }

  private void dispatch(HttpExchange exchange) throws IOException {
    String path = exchange.getRequestURI().getPath();
    String method = exchange.getRequestMethod();

    if ("/onboarding".equals(path) || "/onboarding.html".equals(path)) {
      onboardingHandler.handle(exchange);
      return;
    }

    if (path.startsWith("/v1/profile/") && path.endsWith("/vod-preferences")) {
      vodPreferencesHandler.handle(exchange, method, path);
      return;
    }

    if (path.startsWith("/v1/profile/") && path.endsWith("/recommendations")) {
      recommendationsHandler.handle(exchange, path);
      return;
    }

    HttpResponses.sendJson(exchange, 404, "{\"error\":\"not_found\"}");
  }

  /** Stops the embedded server. */
  @Override
  public void close() {
    server.stop(0);
  }
}

