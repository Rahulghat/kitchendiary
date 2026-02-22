package com.kitchen.kitchendiary.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.kitchen.kitchendiary.entities.Business;
import com.kitchen.kitchendiary.entities.Order;
import com.kitchen.kitchendiary.entities.Platform;
import com.kitchen.kitchendiary.repositories.OrderRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class OrderQueryServiceTest {

  @Mock private BusinessAccessService businessAccessService;
  @Mock private OrderRepository orderRepository;
  @InjectMocks private OrderQueryService service;

  @Test
  void list_shouldUseBusinessDateQueryWhenPlatformNotProvided() {
    Order order = sampleOrder();
    when(orderRepository.findAllByBusinessIdAndOrderDateBetween(
            4L, LocalDate.of(2026, 2, 1), LocalDate.of(2026, 2, 28)))
        .thenReturn(List.of(order));

    var result = service.list(1L, 4L, LocalDate.of(2026, 2, 1), LocalDate.of(2026, 2, 28), null);

    assertEquals(1, result.size());
    assertEquals(order.getId(), result.getFirst().id());
  }

  @Test
  void listPaged_shouldFallbackSortAndReturnMappedPage() {
    Order order = sampleOrder();
    when(orderRepository.findAllByBusinessIdAndOrderDateBetween(
            org.mockito.ArgumentMatchers.eq(4L),
            org.mockito.ArgumentMatchers.eq(LocalDate.of(2026, 2, 1)),
            org.mockito.ArgumentMatchers.eq(LocalDate.of(2026, 2, 28)),
            any(Pageable.class)))
        .thenReturn(new PageImpl<>(List.of(order)));

    var page =
        service.listPaged(
            1L,
            4L,
            LocalDate.of(2026, 2, 1),
            LocalDate.of(2026, 2, 28),
            null,
            "badSort",
            "weird",
            -5,
            0);

    assertEquals(1, page.getTotalElements());
    assertEquals(order.getId(), page.getContent().getFirst().id());

    ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
    verify(orderRepository)
        .findAllByBusinessIdAndOrderDateBetween(
            org.mockito.ArgumentMatchers.eq(4L),
            org.mockito.ArgumentMatchers.eq(LocalDate.of(2026, 2, 1)),
            org.mockito.ArgumentMatchers.eq(LocalDate.of(2026, 2, 28)),
            pageableCaptor.capture());

    Pageable pageable = pageableCaptor.getValue();
    assertEquals(0, pageable.getPageNumber());
    assertEquals(1, pageable.getPageSize());
    assertEquals("orderDate: DESC", pageable.getSort().toString());
  }

  @Test
  void getInvoice_shouldReturnInvoiceDetails() {
    Order order = sampleOrder();
    when(orderRepository.findByIdAndBusinessId(99L, 4L)).thenReturn(Optional.of(order));

    var invoice = service.getInvoice(1L, 4L, 99L);

    assertEquals(order.getId(), invoice.orderId());
    assertEquals(order.getBusiness().getName(), invoice.businessName());
    assertEquals(order.getPlatform().getCode(), invoice.platformCode());
  }

  @Test
  void getInvoice_shouldThrowWhenOrderMissing() {
    when(orderRepository.findByIdAndBusinessId(99L, 4L)).thenReturn(Optional.empty());
    assertThrows(IllegalArgumentException.class, () -> service.getInvoice(1L, 4L, 99L));
  }

  private static Order sampleOrder() {
    Business b = new Business();
    b.setId(4L);
    b.setName("Demo Kitchen");
    b.setAddress("Addr");
    b.setCity("Mumbai");
    b.setState("MH");
    b.setGstin("27ABCDE1234F1Z5");

    Platform p = new Platform();
    p.setId(7L);
    p.setBusiness(b);
    p.setName("Zomato");
    p.setCode("ZOMATO");

    Order o = new Order();
    o.setId(99L);
    o.setBusiness(b);
    o.setPlatform(p);
    o.setOrderDate(LocalDate.of(2026, 2, 10));
    o.setGrossAmount(new BigDecimal("1000.00"));
    o.setCommissionRate(new BigDecimal("20.00"));
    o.setGstRateOnComm(new BigDecimal("18.00"));
    o.setCommissionAmount(new BigDecimal("200.00"));
    o.setGstOnCommission(new BigDecimal("36.00"));
    o.setNetExpected(new BigDecimal("764.00"));
    o.setNetReceived(new BigDecimal("760.00"));
    o.setMismatchAmount(new BigDecimal("-4.00"));
    o.setNotes("note");
    o.setCreatedAt(Instant.parse("2026-02-10T10:00:00Z"));
    return o;
  }
}

