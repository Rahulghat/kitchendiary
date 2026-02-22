package com.kitchen.kitchendiary.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class OrderCalculationServiceTest {

  private final OrderCalculationService service = new OrderCalculationService();

  @Test
  void calculate_shouldReturnExpectedComputedValues() {
    var result =
        service.calculate(
            new BigDecimal("2400.00"),
            new BigDecimal("22.00"),
            new BigDecimal("18.00"),
            new BigDecimal("1768.64"));

    assertEquals(new BigDecimal("528.00"), result.commissionAmount());
    assertEquals(new BigDecimal("95.04"), result.gstOnCommission());
    assertEquals(new BigDecimal("1776.96"), result.netExpected());
    assertEquals(new BigDecimal("-8.32"), result.mismatchAmount());
  }
}

