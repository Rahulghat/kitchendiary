package com.kitchen.kitchendiary.service;

import com.kitchen.kitchendiary.dto.CreateExpenseRequest;
import com.kitchen.kitchendiary.dto.ExpenseResponse;
import com.kitchen.kitchendiary.entities.Expense;
import com.kitchen.kitchendiary.repositories.ExpenseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ExpenseService {

  private final BusinessAccessService businessAccessService;
  private final ExpenseRepository expenseRepository;

  public ExpenseService(
      BusinessAccessService businessAccessService, ExpenseRepository expenseRepository) {
    this.businessAccessService = businessAccessService;
    this.expenseRepository = expenseRepository;
  }

  @Transactional
  public ExpenseResponse create(Long ownerUserId, Long businessId, CreateExpenseRequest req) {
    var business = businessAccessService.getBusinessOrThrow(ownerUserId, businessId);

    Expense e = new Expense();
    e.setBusiness(business);
    e.setExpenseDate(req.expenseDate());
    e.setCategory(req.category());
    e.setAmount(req.amount());
    e.setNotes(req.notes());

    Expense saved = expenseRepository.save(e);

    return new ExpenseResponse(
        saved.getId(),
        saved.getBusiness().getId(),
        saved.getExpenseDate(),
        saved.getCategory(),
        saved.getAmount(),
        saved.getNotes());
  }
}
