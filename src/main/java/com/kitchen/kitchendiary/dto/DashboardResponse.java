package com.kitchen.kitchendiary.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DashboardResponse(
    Long businessId,
    LocalDate startDate,
    LocalDate endDate,
    BigDecimal totalGross,
    BigDecimal totalCommission,
    BigDecimal totalGstOnCommission,
    BigDecimal totalNetExpected,
    BigDecimal totalNetReceived,
    BigDecimal totalMismatch,
    BigDecimal totalExpenses,
    BigDecimal estimatedProfit) {}
