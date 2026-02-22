package com.kitchen.kitchendiary.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record OrderResponse(
    Long id,
    Long businessId,
    Long platformId,
    LocalDate orderDate,
    BigDecimal grossAmount,
    BigDecimal commissionRate,
    BigDecimal gstRateOnComm,
    BigDecimal commissionAmount,
    BigDecimal gstOnCommission,
    BigDecimal netExpected,
    BigDecimal netReceived,
    BigDecimal mismatchAmount,
    String notes,
    Instant createdAt) {}
