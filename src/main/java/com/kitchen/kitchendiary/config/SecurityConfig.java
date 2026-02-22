package com.kitchen.kitchendiary.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers("/health", "/actuator/health", "/error")
                    .permitAll()
                    .requestMatchers(
                        "/login",
                        "/ui.css",
                        "/ui-grid-ajax.js",
                        "/kitchendiary-logo.svg")
                    .permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/api/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/api/**")
                    .hasRole("ADMIN")
                    .requestMatchers("/api/**")
                    .hasAnyRole("USER", "ADMIN")
                    .requestMatchers("/admin/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.POST, "/ui/**")
                    .hasRole("ADMIN")
                    .requestMatchers("/", "/ui/**")
                    .hasAnyRole("USER", "ADMIN")
                    .anyRequest()
                    .authenticated())
        .formLogin(
            form ->
                form.loginPage("/login")
                    .defaultSuccessUrl("/ui", true)
                    .failureUrl("/login?error")
                    .permitAll())
        .logout(logout -> logout.logoutSuccessUrl("/login?logout"))
        .httpBasic(Customizer.withDefaults());

    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
  }
}
