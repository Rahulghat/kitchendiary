package com.kitchen.kitchendiary.controller;

import com.kitchen.kitchendiary.config.CurrentUser;
import com.kitchen.kitchendiary.dto.CreateOrderRequest;
import com.kitchen.kitchendiary.dto.OrderResponse;
import com.kitchen.kitchendiary.service.OrderQueryService;
import com.kitchen.kitchendiary.service.OrderService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/businesses/{businessId}/orders")
public class OrderController {

  private final OrderService orderService;
  private final OrderQueryService orderQueryService;

  public OrderController(OrderService orderService, OrderQueryService orderQueryService) {
    this.orderService = orderService;
    this.orderQueryService = orderQueryService;
  }

  @PostMapping("/platform/{platformId}")
  public OrderResponse createOrder(
      @PathVariable Long businessId,
      @PathVariable Long platformId,
      @Valid @RequestBody CreateOrderRequest req) {
    Long ownerUserId = CurrentUser.id();
    return orderService.create(ownerUserId, businessId, platformId, req);
  }

  @GetMapping
  public List<OrderResponse> listOrders(
      @PathVariable Long businessId,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
      @RequestParam(required = false) Long platformId) {
    Long ownerUserId = CurrentUser.id();
    return orderQueryService.list(ownerUserId, businessId, startDate, endDate, platformId);
  }
}
