package com.vod.onboarding.api.mock;

import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import com.vod.onboarding.api.mock.handlers.OnboardingStaticHandler;
import com.vod.onboarding.api.mock.handlers.OnboardingSurveyHandler;
import com.vod.onboarding.api.mock.handlers.RecommendationsHandler;
import com.vod.onboarding.api.mock.handlers.SurveyMoviesHandler;
import com.vod.onboarding.api.mock.handlers.VodPreferencesHandler;
import com.vod.onboarding.common.fixtures.MockJsonLoader;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Embedded in-process mock server for onboarding HTML + VOD API endpoints.
 */
public final class EmbeddedMockServer implements AutoCloseable {
  private final HttpServer server;
  private final String baseUrl;
  private final MockRouteRegistry registry;

  public EmbeddedMockServer() throws IOException {
    registry = defaultRegistry();
    server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
    server.createContext("/", this::dispatch);
    server.setExecutor(null);
    server.start();
    baseUrl = "http://127.0.0.1:" + server.getAddress().getPort();
  }

  /** Registry used by this server (for tests that add custom handlers). */
  public MockRouteRegistry routeRegistry() {
    return registry;
  }

  public String baseUrl() {
    return baseUrl;
  }

  private void dispatch(HttpExchange exchange) throws IOException {
    registry.dispatch(exchange);
  }

  private static MockRouteRegistry defaultRegistry() {
    JsonObject recommendationsDefault = MockJsonLoader.load("recommendations-default.json");
    JsonObject recommendationsPersonalized = MockJsonLoader.load("recommendations-personalized.json");
    return new MockRouteRegistry()
        .register(new OnboardingStaticHandler())
        .register(new OnboardingSurveyHandler())
        .register(new SurveyMoviesHandler())
        .register(new VodPreferencesHandler())
        .register(new RecommendationsHandler(recommendationsDefault, recommendationsPersonalized));
  }

  @Override
  public void close() {
    server.stop(0);
  }
}
