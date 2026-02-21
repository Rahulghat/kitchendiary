package com.kitchen.kitchendiary.controller;

import com.kitchen.kitchendiary.dto.CreateExpenseRequest;
import com.kitchen.kitchendiary.dto.ExpenseResponse;
import com.kitchen.kitchendiary.service.ExpenseQueryService;
import com.kitchen.kitchendiary.service.ExpenseService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/businesses/{businessId}/expenses")
public class ExpenseController {

  private final ExpenseService expenseService;
  private final ExpenseQueryService expenseQueryService;

  public ExpenseController(ExpenseService expenseService, ExpenseQueryService expenseQueryService) {
    this.expenseService = expenseService;
    this.expenseQueryService = expenseQueryService;
  }

  @PostMapping
  public ExpenseResponse createExpense(
      @RequestHeader(value = "X-USER-ID", required = false) String userHeader,
      @PathVariable Long businessId,
      @Valid @RequestBody CreateExpenseRequest req) {
    Long ownerUserId = RequestUser.requireUserId(userHeader);
    return expenseService.create(ownerUserId, businessId, req);
  }

  @GetMapping
  public List<ExpenseResponse> listExpenses(
      @RequestHeader(value = "X-USER-ID", required = false) String userHeader,
      @PathVariable Long businessId,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
      @RequestParam(required = false) String category) {
    Long ownerUserId = RequestUser.requireUserId(userHeader);
    return expenseQueryService.list(ownerUserId, businessId, startDate, endDate, category);
  }
}
