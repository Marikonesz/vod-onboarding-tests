package com.vod.onboarding.api.mock;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

/** Single route handler registered on {@link EmbeddedMockServer}. */
public interface MockRouteHandler {

  /** @return true if this handler owns the request */
  boolean matches(String path, String method);

  /** Handles the request; called only when {@link #matches} is true. */
  void handle(HttpExchange exchange, String path, String method) throws IOException;
}
