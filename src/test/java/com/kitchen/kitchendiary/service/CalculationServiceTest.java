package com.kitchen.kitchendiary.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class CalculationServiceTest {

  private final CalculationService service = new CalculationService();

  @Test
  void calculate_shouldComputeAmounts() {
    var result =
        service.calculate(
            new BigDecimal("1000.00"),
            new BigDecimal("20.00"),
            new BigDecimal("18.00"),
            new BigDecimal("760.00"));

    assertEquals(new BigDecimal("200.00"), result.commissionAmount());
    assertEquals(new BigDecimal("36.00"), result.gstOnCommission());
    assertEquals(new BigDecimal("764.00"), result.netExpected());
    assertEquals(new BigDecimal("-4.00"), result.mismatchAmount());
  }

  @Test
  void calculate_shouldTreatNullAsZero() {
    var result = service.calculate(null, null, null, null);

    assertEquals(new BigDecimal("0.00"), result.commissionAmount());
    assertEquals(new BigDecimal("0.00"), result.gstOnCommission());
    assertEquals(new BigDecimal("0.00"), result.netExpected());
    assertEquals(new BigDecimal("0.00"), result.mismatchAmount());
  }
}

