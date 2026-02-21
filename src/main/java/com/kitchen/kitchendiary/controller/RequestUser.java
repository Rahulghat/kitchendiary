package com.kitchen.kitchendiary.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public final class RequestUser {
  private RequestUser() {}

  public static Long requireUserId(String headerValue) {
    if (headerValue == null || headerValue.isBlank()) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing X-USER-ID header");
    }
    try {
      return Long.parseLong(headerValue.trim());
    } catch (NumberFormatException e) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid X-USER-ID header");
    }
  }
}
