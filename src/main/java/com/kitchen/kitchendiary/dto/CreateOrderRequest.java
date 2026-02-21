package com.kitchen.kitchendiary.dto;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateOrderRequest(
        @NotNull LocalDate orderDate,

        @NotNull @DecimalMin("0.00") BigDecimal grossAmount,

        @NotNull @DecimalMin("0.00") BigDecimal commissionRate,   // percent
        @NotNull @DecimalMin("0.00") BigDecimal gstRateOnComm,    // percent

        @NotNull @DecimalMin("0.00") BigDecimal netReceived,

        String notes
) {}