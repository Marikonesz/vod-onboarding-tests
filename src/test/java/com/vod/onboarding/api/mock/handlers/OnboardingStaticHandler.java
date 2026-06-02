package com.vod.onboarding.api.mock.handlers;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;

import static com.vod.onboarding.api.mock.handlers.HttpResponses.sendBytes;
import static com.vod.onboarding.api.mock.handlers.HttpResponses.sendText;

/**
 * Serves onboarding static HTML from test resources.
 */
public final class OnboardingStaticHandler {

  /** Serves {@code /onboarding} and {@code /onboarding.html} from classpath. */
  public void handle(HttpExchange exchange) throws IOException {
    try (InputStream in = getClass().getResourceAsStream("/public/onboarding.html")) {
      if (in == null) {
        sendText(exchange, 404, "onboarding.html not found", "text/plain; charset=utf-8");
        return;
      }
      byte[] body = in.readAllBytes();
      sendBytes(exchange, 200, body, "text/html; charset=utf-8");
    }
  }
}

