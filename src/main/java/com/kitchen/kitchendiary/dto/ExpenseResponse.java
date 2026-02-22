package com.kitchen.kitchendiary.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record ExpenseResponse(
    Long id,
    Long businessId,
    LocalDate expenseDate,
    String category,
    BigDecimal amount,
    String notes,
    Instant createdAt) {}
