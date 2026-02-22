package com.kitchen.kitchendiary.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.kitchen.kitchendiary.entities.Business;
import com.kitchen.kitchendiary.entities.Expense;
import com.kitchen.kitchendiary.repositories.ExpenseRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class ExpenseQueryServiceTest {

  @Mock private BusinessAccessService businessAccessService;
  @Mock private ExpenseRepository expenseRepository;
  @InjectMocks private ExpenseQueryService service;

  @Test
  void list_shouldUseDateRangeQueryWhenCategoryBlank() {
    Expense expense = sampleExpense();
    when(expenseRepository.findAllByBusinessIdAndExpenseDateBetween(
            4L, LocalDate.of(2026, 2, 1), LocalDate.of(2026, 2, 28)))
        .thenReturn(List.of(expense));

    var result = service.list(1L, 4L, LocalDate.of(2026, 2, 1), LocalDate.of(2026, 2, 28), " ");

    assertEquals(1, result.size());
    assertEquals("Packaging", result.getFirst().category());
  }

  @Test
  void listPaged_shouldUseTrimmedCategoryAndValidSortFallback() {
    Expense expense = sampleExpense();
    when(expenseRepository.findAllByBusinessIdAndCategoryIgnoreCaseAndExpenseDateBetween(
            org.mockito.ArgumentMatchers.eq(4L),
            org.mockito.ArgumentMatchers.eq("Packaging"),
            org.mockito.ArgumentMatchers.eq(LocalDate.of(2026, 2, 1)),
            org.mockito.ArgumentMatchers.eq(LocalDate.of(2026, 2, 28)),
            any(Pageable.class)))
        .thenReturn(new PageImpl<>(List.of(expense)));

    var page =
        service.listPaged(
            1L,
            4L,
            LocalDate.of(2026, 2, 1),
            LocalDate.of(2026, 2, 28),
            " Packaging ",
            "badSort",
            "asc",
            -2,
            0);

    assertEquals(1, page.getTotalElements());
    assertEquals(expense.getId(), page.getContent().getFirst().id());

    ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
    verify(expenseRepository)
        .findAllByBusinessIdAndCategoryIgnoreCaseAndExpenseDateBetween(
            org.mockito.ArgumentMatchers.eq(4L),
            org.mockito.ArgumentMatchers.eq("Packaging"),
            org.mockito.ArgumentMatchers.eq(LocalDate.of(2026, 2, 1)),
            org.mockito.ArgumentMatchers.eq(LocalDate.of(2026, 2, 28)),
            pageableCaptor.capture());

    Pageable pageable = pageableCaptor.getValue();
    assertEquals(0, pageable.getPageNumber());
    assertEquals(1, pageable.getPageSize());
    assertEquals("expenseDate: ASC", pageable.getSort().toString());
  }

  private static Expense sampleExpense() {
    Business business = new Business();
    business.setId(4L);

    Expense expense = new Expense();
    expense.setId(77L);
    expense.setBusiness(business);
    expense.setExpenseDate(LocalDate.of(2026, 2, 12));
    expense.setCategory("Packaging");
    expense.setAmount(new BigDecimal("220.00"));
    expense.setNotes("Box");
    expense.setCreatedAt(Instant.parse("2026-02-12T08:00:00Z"));
    return expense;
  }
}

