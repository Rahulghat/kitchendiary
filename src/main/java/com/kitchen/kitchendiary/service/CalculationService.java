package com.kitchen.kitchendiary.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.stereotype.Service;

@Service
public class CalculationService {

  public record OrderCalc(
      BigDecimal commissionAmount,
      BigDecimal gstOnCommission,
      BigDecimal netExpected,
      BigDecimal mismatchAmount) {}

  public OrderCalc calculate(
      BigDecimal gross,
      BigDecimal commissionRate,
      BigDecimal gstRateOnComm,
      BigDecimal netReceived) {
    gross = nz(gross);
    commissionRate = nz(commissionRate);
    gstRateOnComm = nz(gstRateOnComm);
    netReceived = nz(netReceived);

    BigDecimal commission =
        gross.multiply(commissionRate).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

    BigDecimal gstOnComm =
        commission.multiply(gstRateOnComm).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

    BigDecimal netExpected =
        gross.subtract(commission).subtract(gstOnComm).setScale(2, RoundingMode.HALF_UP);

    BigDecimal mismatch = netReceived.subtract(netExpected).setScale(2, RoundingMode.HALF_UP);

    return new OrderCalc(commission, gstOnComm, netExpected, mismatch);
  }

  private BigDecimal nz(BigDecimal v) {
    return v == null ? BigDecimal.ZERO : v;
  }
}
