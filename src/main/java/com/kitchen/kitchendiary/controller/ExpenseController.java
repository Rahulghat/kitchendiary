package com.kitchen.kitchendiary.controller;

import com.kitchen.kitchendiary.dto.CreateExpenseRequest;
import com.kitchen.kitchendiary.dto.ExpenseResponse;
import com.kitchen.kitchendiary.entities.Expense;
import com.kitchen.kitchendiary.repositories.ExpenseRepository;
import com.kitchen.kitchendiary.service.BusinessAccessService;
import com.kitchen.kitchendiary.service.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
public class ExpenseController {

    private final BusinessAccessService businessAccessService;
    private final ExpenseRepository expenseRepository;
    private final ExpenseService expenseService;

    public ExpenseController(BusinessAccessService businessAccessService,
                             ExpenseRepository expenseRepository,
                             ExpenseService expenseService) {
        this.businessAccessService = businessAccessService;
        this.expenseRepository = expenseRepository;
        this.expenseService = expenseService;
    }

    @PostMapping("/api/businesses/{businessId}/expenses")
    public ExpenseResponse createExpense(@RequestParam Long ownerUserId,
                                         @PathVariable Long businessId,
                                         @Valid @RequestBody CreateExpenseRequest request) {
        return expenseService.create(ownerUserId, businessId, request);
    }

    @GetMapping("/api/businesses/{businessId}/expenses")
    @Transactional(readOnly = true)
    public List<ExpenseResponse> listExpenses(@RequestParam Long ownerUserId,
                                              @PathVariable Long businessId,
                                              @RequestParam LocalDate startDate,
                                              @RequestParam LocalDate endDate,
                                              @RequestParam(required = false) String category) {
        businessAccessService.getBusinessOrThrow(ownerUserId, businessId);

        List<Expense> expenses = (category == null || category.isBlank())
                ? expenseRepository.findAllByBusinessIdAndExpenseDateBetween(businessId, startDate, endDate)
                : expenseRepository.findAllByBusinessIdAndCategoryIgnoreCaseAndExpenseDateBetween(
                businessId, category.trim(), startDate, endDate
        );

        return expenses.stream().map(this::toResponse).toList();
    }

    private ExpenseResponse toResponse(Expense expense) {
        return new ExpenseResponse(
                expense.getId(),
                expense.getBusiness().getId(),
                expense.getExpenseDate(),
                expense.getCategory(),
                expense.getAmount(),
                expense.getNotes()
        );
    }
}
