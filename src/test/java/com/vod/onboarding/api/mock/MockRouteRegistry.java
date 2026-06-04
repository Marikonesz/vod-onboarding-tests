package com.vod.onboarding.api.mock;

import com.sun.net.httpserver.HttpExchange;

import com.vod.onboarding.api.mock.handlers.HttpResponses;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Ordered list of mock route handlers (first match wins). */
public final class MockRouteRegistry {
  private final List<MockRouteHandler> handlers = new ArrayList<>();

  public MockRouteRegistry register(MockRouteHandler handler) {
    handlers.add(handler);
    return this;
  }

  public List<MockRouteHandler> handlers() {
    return Collections.unmodifiableList(handlers);
  }

  /** Dispatches to the first matching handler; otherwise 404 JSON. */
  public void dispatch(HttpExchange exchange) throws IOException {
    String path = exchange.getRequestURI().getPath();
    String method = exchange.getRequestMethod();
    for (MockRouteHandler handler : handlers) {
      if (handler.matches(path, method)) {
        handler.handle(exchange, path, method);
        return;
      }
    }
    HttpResponses.sendJson(exchange, 404, "{\"error\":\"not_found\"}");
  }
}
