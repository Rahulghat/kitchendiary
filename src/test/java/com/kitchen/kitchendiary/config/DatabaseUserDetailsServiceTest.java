package com.kitchen.kitchendiary.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.kitchen.kitchendiary.entities.User;
import com.kitchen.kitchendiary.repositories.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith(MockitoExtension.class)
class DatabaseUserDetailsServiceTest {

  @Mock private UserRepository userRepository;
  @InjectMocks private DatabaseUserDetailsService service;

  @Test
  void loadUserByUsername_shouldReturnPrincipal() {
    User user = new User();
    user.setId(1L);
    user.setEmail("rahul@example.com");
    user.setPasswordHash("{noop}rahul123");
    user.setRole("ADMIN");
    when(userRepository.findByEmailIgnoreCase("rahul@example.com")).thenReturn(Optional.of(user));

    var details = service.loadUserByUsername("rahul@example.com");
    assertEquals("rahul@example.com", details.getUsername());
    assertEquals("{noop}rahul123", details.getPassword());
  }

  @Test
  void loadUserByUsername_shouldThrowWhenMissing() {
    when(userRepository.findByEmailIgnoreCase("missing@example.com")).thenReturn(Optional.empty());
    assertThrows(
        UsernameNotFoundException.class, () -> service.loadUserByUsername("missing@example.com"));
  }
}

