package com.kitchen.kitchendiary.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateExpenseRequest(
    @NotNull LocalDate expenseDate,
    @NotBlank @Size(max = 80) String category,
    @NotNull @DecimalMin("0.00") BigDecimal amount,
    String notes) {}
