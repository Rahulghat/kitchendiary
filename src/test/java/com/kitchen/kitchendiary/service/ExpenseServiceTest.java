package com.kitchen.kitchendiary.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import com.kitchen.kitchendiary.dto.CreateExpenseRequest;
import com.kitchen.kitchendiary.entities.Business;
import com.kitchen.kitchendiary.entities.Expense;
import com.kitchen.kitchendiary.repositories.ExpenseRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {

  @Mock private BusinessAccessService businessAccessService;
  @Mock private ExpenseRepository expenseRepository;
  @InjectMocks private ExpenseService service;

  @Test
  void create_shouldMapAndReturnResponse() {
    Business business = new Business();
    business.setId(4L);
    when(businessAccessService.getBusinessOrThrow(1L, 4L)).thenReturn(business);
    when(expenseRepository.save(org.mockito.ArgumentMatchers.any(Expense.class)))
        .thenAnswer(
            invocation -> {
              Expense e = invocation.getArgument(0);
              e.setId(100L);
              if (e.getCreatedAt() == null) {
                e.setCreatedAt(Instant.parse("2026-02-22T00:00:00Z"));
              }
              return e;
            });

    var response =
        service.create(
            1L,
            4L,
            new CreateExpenseRequest(
                LocalDate.of(2026, 2, 22), "Packaging", new BigDecimal("250.00"), "boxes"));

    assertEquals(100L, response.id());
    assertEquals(4L, response.businessId());
    assertEquals("Packaging", response.category());
    assertNotNull(response.createdAt());
  }

  @Test
  void create_withCreatedAtOverride_shouldUseOverride() {
    Business business = new Business();
    business.setId(4L);
    when(businessAccessService.getBusinessOrThrow(1L, 4L)).thenReturn(business);
    when(expenseRepository.save(org.mockito.ArgumentMatchers.any(Expense.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    Instant override = Instant.parse("2026-02-01T01:02:03Z");
    var response =
        service.create(
            1L,
            4L,
            new CreateExpenseRequest(
                LocalDate.of(2026, 2, 1), "Gas", new BigDecimal("500.00"), null),
            override);

    assertEquals(override, response.createdAt());
  }
}

