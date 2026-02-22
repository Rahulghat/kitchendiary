package com.kitchen.kitchendiary.service;

import com.kitchen.kitchendiary.dto.ExpenseResponse;
import com.kitchen.kitchendiary.entities.Expense;
import com.kitchen.kitchendiary.repositories.ExpenseRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
        .map(this::toExpenseResponse)
        .toList();
  }

  @Transactional(readOnly = true)
  public Page<ExpenseResponse> listPaged(
      Long ownerUserId,
      Long businessId,
      LocalDate startDate,
      LocalDate endDate,
      String category,
      String sortBy,
      String sortDir,
      int page,
      int size) {

    businessAccessService.getBusinessOrThrow(ownerUserId, businessId);
    var allowedSorts = Set.of("id", "expenseDate", "category", "amount", "createdAt");
    String effectiveSortBy = allowedSorts.contains(sortBy) ? sortBy : "expenseDate";
    Sort.Direction direction =
        "asc".equalsIgnoreCase(sortDir) ? Sort.Direction.ASC : Sort.Direction.DESC;
    var pageable =
        PageRequest.of(Math.max(0, page), Math.max(1, size), Sort.by(direction, effectiveSortBy));

    var expensesPage =
        (category == null || category.isBlank())
            ? expenseRepository.findAllByBusinessIdAndExpenseDateBetween(
                businessId, startDate, endDate, pageable)
            : expenseRepository.findAllByBusinessIdAndCategoryIgnoreCaseAndExpenseDateBetween(
                businessId, category.trim(), startDate, endDate, pageable);

    return expensesPage.map(this::toExpenseResponse);
  }

  private ExpenseResponse toExpenseResponse(Expense expense) {
    return new ExpenseResponse(
        expense.getId(),
        expense.getBusiness().getId(),
        expense.getExpenseDate(),
        expense.getCategory(),
        expense.getAmount(),
        expense.getNotes(),
        expense.getCreatedAt());
  }
}
