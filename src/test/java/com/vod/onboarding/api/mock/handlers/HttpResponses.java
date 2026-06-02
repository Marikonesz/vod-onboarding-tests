package com.vod.onboarding.api.mock.handlers;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * Utility helpers for writing HTTP responses in mock handlers.
 */
public final class HttpResponses {
  private HttpResponses() {}

  /** Sends a JSON response with {@code application/json} content type. */
  public static void sendJson(HttpExchange exchange, int status, String json) throws IOException {
    sendText(exchange, status, json, "application/json");
  }

  /** Sends a text response with the given content type. */
  public static void sendText(
      HttpExchange exchange, int status, String body, String contentType) throws IOException {
    sendBytes(exchange, status, body.getBytes(StandardCharsets.UTF_8), contentType);
  }

  /** Sends a raw byte response with the given status and content type. */
  public static void sendBytes(
      HttpExchange exchange, int status, byte[] bytes, String contentType) throws IOException {
    exchange.getResponseHeaders().set("Content-Type", contentType);
    exchange.sendResponseHeaders(status, bytes.length);
    try (OutputStream out = exchange.getResponseBody()) {
      out.write(bytes);
    }
  }
}

