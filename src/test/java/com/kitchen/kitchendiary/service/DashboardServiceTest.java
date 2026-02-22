package com.kitchen.kitchendiary.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.kitchen.kitchendiary.repositories.ExpenseRepository;
import com.kitchen.kitchendiary.repositories.OrderRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

  @Mock private BusinessAccessService businessAccessService;
  @Mock private OrderRepository orderRepository;
  @Mock private ExpenseRepository expenseRepository;
  @InjectMocks private DashboardService service;

  @Test
  void get_shouldComputeProfitFromSums() {
    LocalDate start = LocalDate.of(2026, 2, 1);
    LocalDate end = LocalDate.of(2026, 2, 28);
    when(orderRepository.sumGrossAmount(3L, start, end)).thenReturn(new BigDecimal("1000"));
    when(orderRepository.sumCommissionAmount(3L, start, end)).thenReturn(new BigDecimal("200"));
    when(orderRepository.sumGstOnCommission(3L, start, end)).thenReturn(new BigDecimal("36"));
    when(orderRepository.sumNetExpected(3L, start, end)).thenReturn(new BigDecimal("764"));
    when(orderRepository.sumNetReceived(3L, start, end)).thenReturn(new BigDecimal("760"));
    when(orderRepository.sumMismatchAmount(3L, start, end)).thenReturn(new BigDecimal("-4"));
    when(expenseRepository.sumAmount(3L, start, end)).thenReturn(new BigDecimal("500"));

    var dashboard = service.get(1L, 3L, start, end);

    assertEquals(new BigDecimal("760"), dashboard.totalNetReceived());
    assertEquals(new BigDecimal("500"), dashboard.totalExpenses());
    assertEquals(new BigDecimal("260"), dashboard.estimatedProfit());
  }
}

