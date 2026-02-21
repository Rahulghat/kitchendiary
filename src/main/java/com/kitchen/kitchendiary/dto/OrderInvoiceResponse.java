package com.kitchen.kitchendiary.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record OrderInvoiceResponse(
    Long orderId,
    Long businessId,
    String businessName,
    String businessAddress,
    String businessCity,
    String businessState,
    String businessGstin,
    String platformName,
    String platformCode,
    LocalDate orderDate,
    BigDecimal grossAmount,
    BigDecimal commissionRate,
    BigDecimal gstRateOnComm,
    BigDecimal commissionAmount,
    BigDecimal gstOnCommission,
    BigDecimal netExpected,
    BigDecimal netReceived,
    BigDecimal mismatchAmount,
    String notes) {}
