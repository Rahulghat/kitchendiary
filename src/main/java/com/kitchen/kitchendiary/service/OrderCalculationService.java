package com.kitchen.kitchendiary.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class OrderCalculationService {

    private static final int SCALE = 2;
    private static final RoundingMode ROUND = RoundingMode.HALF_UP;

    public CalculatedAmounts calculate(BigDecimal gross,
                                       BigDecimal commissionRatePercent,
                                       BigDecimal gstRatePercent,
                                       BigDecimal netReceived) {

        BigDecimal hundred = BigDecimal.valueOf(100);

        BigDecimal commissionAmount = gross
                .multiply(commissionRatePercent)
                .divide(hundred, SCALE, ROUND);

        BigDecimal gstOnCommission = commissionAmount
                .multiply(gstRatePercent)
                .divide(hundred, SCALE, ROUND);

        BigDecimal netExpected = gross
                .subtract(commissionAmount)
                .subtract(gstOnCommission)
                .setScale(SCALE, ROUND);

        BigDecimal mismatchAmount = netReceived
                .subtract(netExpected)
                .setScale(SCALE, ROUND);

        return new CalculatedAmounts(
                commissionAmount.setScale(SCALE, ROUND),
                gstOnCommission.setScale(SCALE, ROUND),
                netExpected,
                mismatchAmount
        );
    }

    public record CalculatedAmounts(
            BigDecimal commissionAmount,
            BigDecimal gstOnCommission,
            BigDecimal netExpected,
            BigDecimal mismatchAmount
    ) {}
}