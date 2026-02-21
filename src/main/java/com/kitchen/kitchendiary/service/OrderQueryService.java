package com.kitchen.kitchendiary.service;

import com.kitchen.kitchendiary.dto.OrderInvoiceResponse;
import com.kitchen.kitchendiary.dto.OrderResponse;
import com.kitchen.kitchendiary.repositories.OrderRepository;
import java.time.LocalDate;
import java.util.List;
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
        .map(
            o ->
                new OrderResponse(
                    o.getId(),
                    o.getBusiness().getId(),
                    o.getPlatform().getId(),
                    o.getOrderDate(),
                    o.getGrossAmount(),
                    o.getCommissionRate(),
                    o.getGstRateOnComm(),
                    o.getCommissionAmount(),
                    o.getGstOnCommission(),
                    o.getNetExpected(),
                    o.getNetReceived(),
                    o.getMismatchAmount(),
                    o.getNotes()))
        .toList();
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
}
