package com.kitchen.kitchendiary.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.kitchen.kitchendiary.dto.CreateOrderRequest;
import com.kitchen.kitchendiary.entities.Business;
import com.kitchen.kitchendiary.entities.Order;
import com.kitchen.kitchendiary.entities.Platform;
import com.kitchen.kitchendiary.repositories.OrderRepository;
import com.kitchen.kitchendiary.repositories.PlatformRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

  @Mock private BusinessAccessService businessAccessService;
  @Mock private PlatformRepository platformRepository;
  @Mock private OrderRepository orderRepository;
  @Mock private OrderCalculationService calc;
  @InjectMocks private OrderService service;

  @Test
  void create_shouldBuildOrderAndReturnResponse() {
    Business business = new Business();
    business.setId(11L);
    Platform platform = new Platform();
    platform.setId(7L);
    platform.setBusiness(business);

    when(businessAccessService.getBusinessOrThrow(2L, 11L)).thenReturn(business);
    when(platformRepository.findByIdAndBusinessId(7L, 11L)).thenReturn(Optional.of(platform));
    when(calc.calculate(
            new BigDecimal("1000.00"),
            new BigDecimal("20.00"),
            new BigDecimal("18.00"),
            new BigDecimal("760.00")))
        .thenReturn(
            new OrderCalculationService.CalculatedAmounts(
                new BigDecimal("200.00"),
                new BigDecimal("36.00"),
                new BigDecimal("764.00"),
                new BigDecimal("-4.00")));

    when(orderRepository.save(org.mockito.ArgumentMatchers.any(Order.class)))
        .thenAnswer(
            invocation -> {
              Order o = invocation.getArgument(0);
              o.setId(500L);
              if (o.getCreatedAt() == null) {
                o.setCreatedAt(Instant.parse("2026-02-22T00:00:00Z"));
              }
              return o;
            });

    var response =
        service.create(
            2L,
            11L,
            7L,
            new CreateOrderRequest(
                LocalDate.of(2026, 2, 22),
                new BigDecimal("1000.00"),
                new BigDecimal("20.00"),
                new BigDecimal("18.00"),
                new BigDecimal("760.00"),
                "test"));

    assertEquals(500L, response.id());
    assertEquals(new BigDecimal("764.00"), response.netExpected());
    assertEquals(new BigDecimal("-4.00"), response.mismatchAmount());
  }

  @Test
  void create_shouldThrowWhenPlatformMissing() {
    Business business = new Business();
    when(businessAccessService.getBusinessOrThrow(2L, 11L)).thenReturn(business);
    when(platformRepository.findByIdAndBusinessId(7L, 11L)).thenReturn(Optional.empty());

    assertThrows(
        IllegalArgumentException.class,
        () ->
            service.create(
                2L,
                11L,
                7L,
                new CreateOrderRequest(
                    LocalDate.of(2026, 2, 22),
                    new BigDecimal("1000.00"),
                    new BigDecimal("20.00"),
                    new BigDecimal("18.00"),
                    new BigDecimal("760.00"),
                    null)));
  }
}

