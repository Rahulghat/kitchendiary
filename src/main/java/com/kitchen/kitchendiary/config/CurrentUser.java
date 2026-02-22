package com.kitchen.kitchendiary.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

public final class CurrentUser {
  private CurrentUser() {}

  public static Long id() {
    AppUserPrincipal principal = requirePrincipal();
    return principal.getId();
  }

  public static boolean isAdmin() {
    AppUserPrincipal principal = requirePrincipal();
    return "ADMIN".equalsIgnoreCase(principal.getRole());
  }

  public static String email() {
    return requirePrincipal().getEmail();
  }

  public static String role() {
    return requirePrincipal().getRole();
  }

  private static AppUserPrincipal requirePrincipal() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !(auth.getPrincipal() instanceof AppUserPrincipal principal)) {
      throw new ResponseStatusException(UNAUTHORIZED, "Authentication required");
    }
    return principal;
  }
}
