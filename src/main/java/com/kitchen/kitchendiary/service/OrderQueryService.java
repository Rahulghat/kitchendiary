package com.kitchen.kitchendiary.service;

import com.kitchen.kitchendiary.dto.OrderInvoiceResponse;
import com.kitchen.kitchendiary.dto.OrderResponse;
import com.kitchen.kitchendiary.entities.Order;
import com.kitchen.kitchendiary.repositories.OrderRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderQueryService {

  private final BusinessAccessService businessAccessService;
  private final OrderRepository orderRepository;

  public OrderQueryService(
      BusinessAccessService businessAccessService, OrderRepository orderRepository) {
    this.businessAccessService = businessAccessService;
    this.orderRepository = orderRepository;
  }

  @Transactional(readOnly = true)
  public List<OrderResponse> list(
      Long ownerUserId, Long businessId, LocalDate startDate, LocalDate endDate, Long platformId) {

    businessAccessService.getBusinessOrThrow(ownerUserId, businessId);

    var orders =
        (platformId == null)
            ? orderRepository.findAllByBusinessIdAndOrderDateBetween(businessId, startDate, endDate)
            : orderRepository.findAllByBusinessIdAndPlatformIdAndOrderDateBetween(
                businessId, platformId, startDate, endDate);

    return orders.stream()
        .map(this::toOrderResponse)
        .toList();
  }

  @Transactional(readOnly = true)
  public Page<OrderResponse> listPaged(
      Long ownerUserId,
      Long businessId,
      LocalDate startDate,
      LocalDate endDate,
      Long platformId,
      int page,
      int size) {

    businessAccessService.getBusinessOrThrow(ownerUserId, businessId);
    var pageable = PageRequest.of(Math.max(0, page), Math.max(1, size));

    var ordersPage =
        (platformId == null)
            ? orderRepository.findAllByBusinessIdAndOrderDateBetween(
                businessId, startDate, endDate, pageable)
            : orderRepository.findAllByBusinessIdAndPlatformIdAndOrderDateBetween(
                businessId, platformId, startDate, endDate, pageable);

    return ordersPage.map(this::toOrderResponse);
  }

  @Transactional(readOnly = true)
  public OrderInvoiceResponse getInvoice(Long ownerUserId, Long businessId, Long orderId) {
    businessAccessService.getBusinessOrThrow(ownerUserId, businessId);

    var order =
        orderRepository
            .findByIdAndBusinessId(orderId, businessId)
            .orElseThrow(() -> new IllegalArgumentException("Order not found"));

    var business = order.getBusiness();
    var platform = order.getPlatform();

    return new OrderInvoiceResponse(
        order.getId(),
        business.getId(),
        business.getName(),
        business.getAddress(),
        business.getCity(),
        business.getState(),
        business.getGstin(),
        platform.getName(),
        platform.getCode(),
        order.getOrderDate(),
        order.getGrossAmount(),
        order.getCommissionRate(),
        order.getGstRateOnComm(),
        order.getCommissionAmount(),
        order.getGstOnCommission(),
        order.getNetExpected(),
        order.getNetReceived(),
        order.getMismatchAmount(),
        order.getNotes());
  }

  private OrderResponse toOrderResponse(Order order) {
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
        order.getNotes());
  }
}
