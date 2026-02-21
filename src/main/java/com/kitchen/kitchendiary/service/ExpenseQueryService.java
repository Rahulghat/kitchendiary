package com.kitchen.kitchendiary.service;

import com.kitchen.kitchendiary.dto.ExpenseResponse;
import com.kitchen.kitchendiary.repositories.ExpenseRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ExpenseQueryService {

  private final BusinessAccessService businessAccessService;
  private final ExpenseRepository expenseRepository;

  public ExpenseQueryService(
      BusinessAccessService businessAccessService, ExpenseRepository expenseRepository) {
    this.businessAccessService = businessAccessService;
    this.expenseRepository = expenseRepository;
  }

  @Transactional(readOnly = true)
  public List<ExpenseResponse> list(
      Long ownerUserId, Long businessId, LocalDate startDate, LocalDate endDate, String category) {

    businessAccessService.getBusinessOrThrow(ownerUserId, businessId);

    var expenses =
        (category == null || category.isBlank())
            ? expenseRepository.findAllByBusinessIdAndExpenseDateBetween(
                businessId, startDate, endDate)
            : expenseRepository.findAllByBusinessIdAndCategoryIgnoreCaseAndExpenseDateBetween(
                businessId, category.trim(), startDate, endDate);

    return expenses.stream()
        .map(
            e ->
                new ExpenseResponse(
                    e.getId(),
                    e.getBusiness().getId(),
                    e.getExpenseDate(),
                    e.getCategory(),
                    e.getAmount(),
                    e.getNotes()))
        .toList();
  }
}
