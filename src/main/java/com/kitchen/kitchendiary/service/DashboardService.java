package com.kitchen.kitchendiary.service;

import com.kitchen.kitchendiary.dto.DashboardResponse;
import com.kitchen.kitchendiary.repositories.ExpenseRepository;
import com.kitchen.kitchendiary.repositories.OrderRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DashboardService {

  private final BusinessAccessService businessAccessService;
  private final OrderRepository orderRepository;
  private final ExpenseRepository expenseRepository;

  public DashboardService(
      BusinessAccessService businessAccessService,
      OrderRepository orderRepository,
      ExpenseRepository expenseRepository) {
    this.businessAccessService = businessAccessService;
    this.orderRepository = orderRepository;
    this.expenseRepository = expenseRepository;
  }

  @Transactional(readOnly = true)
  public DashboardResponse get(
      Long ownerUserId, Long businessId, LocalDate startDate, LocalDate endDate) {
    businessAccessService.getBusinessOrThrow(ownerUserId, businessId);

    BigDecimal gross = orderRepository.sumGrossAmount(businessId, startDate, endDate);
    BigDecimal comm = orderRepository.sumCommissionAmount(businessId, startDate, endDate);
    BigDecimal gst = orderRepository.sumGstOnCommission(businessId, startDate, endDate);
    BigDecimal netExp = orderRepository.sumNetExpected(businessId, startDate, endDate);
    BigDecimal netRec = orderRepository.sumNetReceived(businessId, startDate, endDate);
    BigDecimal mismatch = orderRepository.sumMismatchAmount(businessId, startDate, endDate);

    BigDecimal expenses = expenseRepository.sumAmount(businessId, startDate, endDate);

    BigDecimal profit = netRec.subtract(expenses);

    return new DashboardResponse(
        businessId,
        startDate,
        endDate,
        gross,
        comm,
        gst,
        netExp,
        netRec,
        mismatch,
        expenses,
        profit);
  }
}
