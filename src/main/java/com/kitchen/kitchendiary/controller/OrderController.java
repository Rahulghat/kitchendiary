package com.kitchen.kitchendiary.controller;

import com.kitchen.kitchendiary.dto.CreateOrderRequest;
import com.kitchen.kitchendiary.dto.OrderResponse;
import com.kitchen.kitchendiary.entities.Order;
import com.kitchen.kitchendiary.repositories.OrderRepository;
import com.kitchen.kitchendiary.service.BusinessAccessService;
import com.kitchen.kitchendiary.service.OrderService;
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
public class OrderController {

    private final BusinessAccessService businessAccessService;
    private final OrderRepository orderRepository;
    private final OrderService orderService;

    public OrderController(BusinessAccessService businessAccessService,
                           OrderRepository orderRepository,
                           OrderService orderService) {
        this.businessAccessService = businessAccessService;
        this.orderRepository = orderRepository;
        this.orderService = orderService;
    }

    @PostMapping("/api/businesses/{businessId}/orders")
    public OrderResponse createOrder(@RequestParam Long ownerUserId,
                                     @PathVariable Long businessId,
                                     @RequestParam Long platformId,
                                     @Valid @RequestBody CreateOrderRequest request) {
        return orderService.create(ownerUserId, businessId, platformId, request);
    }

    @GetMapping("/api/businesses/{businessId}/orders")
    @Transactional(readOnly = true)
    public List<OrderResponse> listOrders(@RequestParam Long ownerUserId,
                                          @PathVariable Long businessId,
                                          @RequestParam LocalDate startDate,
                                          @RequestParam LocalDate endDate,
                                          @RequestParam(required = false) Long platformId) {
        businessAccessService.getBusinessOrThrow(ownerUserId, businessId);

        List<Order> orders = platformId == null
                ? orderRepository.findAllByBusinessIdAndOrderDateBetween(businessId, startDate, endDate)
                : orderRepository.findAllByBusinessIdAndPlatformIdAndOrderDateBetween(
                businessId, platformId, startDate, endDate
        );

        return orders.stream().map(this::toResponse).toList();
    }

    private OrderResponse toResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getBusiness().getId(),
                order.getPlatform().getId(),
                order.getOrderDate(),
                order.getGrossAmount(),
                order.getCommissionRate(),
                order.getGstRateOnComm(),
                order.getCommissionAmount(),
                order.getGstOnCommission(),
                order.getNetExpected(),
                order.getNetReceived(),
                order.getMismatchAmount(),
                order.getNotes()
        );
    }
}
