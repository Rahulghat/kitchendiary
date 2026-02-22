package com.kitchen.kitchendiary.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.kitchen.kitchendiary.entities.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

class CurrentUserTest {

  @AfterEach
  void tearDown() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void shouldReadAuthenticatedPrincipalFields() {
    User user = new User();
    user.setId(9L);
    user.setEmail("admin@example.com");
    user.setPasswordHash("{noop}x");
    user.setRole("ADMIN");

    var principal = new AppUserPrincipal(user);
    SecurityContextHolder.getContext()
        .setAuthentication(new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));

    assertEquals(9L, CurrentUser.id());
    assertEquals("admin@example.com", CurrentUser.email());
    assertEquals("ADMIN", CurrentUser.role());
    assertTrue(CurrentUser.isAdmin());
  }

  @Test
  void isAdmin_shouldBeFalseForUserRole() {
    User user = new User();
    user.setId(3L);
    user.setEmail("user@example.com");
    user.setPasswordHash("{noop}x");
    user.setRole("USER");

    var principal = new AppUserPrincipal(user);
    SecurityContextHolder.getContext()
        .setAuthentication(new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));

    assertFalse(CurrentUser.isAdmin());
  }

  @Test
  void shouldThrowWhenNotAuthenticated() {
    SecurityContextHolder.clearContext();
    assertThrows(ResponseStatusException.class, CurrentUser::id);
  }
}

