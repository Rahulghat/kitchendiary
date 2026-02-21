package com.kitchen.kitchendiary.controller;

import jakarta.validation.ConstraintViolationException;
import java.time.Instant;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class ApiExceptionHandler {

  @ExceptionHandler(IllegalArgumentException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map<String, Object> badRequest(IllegalArgumentException ex) {
    return Map.of(
        "timestamp",
        Instant.now().toString(),
        "status",
        400,
        "error",
        "Bad Request",
        "message",
        ex.getMessage());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map<String, Object> validation(MethodArgumentNotValidException ex) {
    String msg =
        ex.getBindingResult().getFieldErrors().stream()
            .findFirst()
            .map(fe -> fe.getField() + " " + fe.getDefaultMessage())
            .orElse("Validation error");

    return Map.of(
        "timestamp",
        Instant.now().toString(),
        "status",
        400,
        "error",
        "Validation Failed",
        "message",
        msg);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map<String, Object> constraint(ConstraintViolationException ex) {
    return Map.of(
        "timestamp",
        Instant.now().toString(),
        "status",
        400,
        "error",
        "Validation Failed",
        "message",
        ex.getMessage());
  }

  @ExceptionHandler(org.springframework.web.server.ResponseStatusException.class)
  public org.springframework.http.ResponseEntity<Map<String, Object>> status(
      org.springframework.web.server.ResponseStatusException ex) {
    return org.springframework.http.ResponseEntity.status(ex.getStatusCode())
        .body(
            Map.of(
                "timestamp", Instant.now().toString(),
                "status", ex.getStatusCode().value(),
                "error", ex.getStatusCode().toString(),
                "message", ex.getReason() == null ? "Error" : ex.getReason()));
  }
}
