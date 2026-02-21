package com.kitchen.kitchendiary.service;

import com.kitchen.kitchendiary.dto.CreateOrderRequest;
import com.kitchen.kitchendiary.dto.OrderResponse;
import com.kitchen.kitchendiary.entities.Business;
import com.kitchen.kitchendiary.entities.Order;
import com.kitchen.kitchendiary.entities.Platform;
import com.kitchen.kitchendiary.repositories.OrderRepository;
import com.kitchen.kitchendiary.repositories.PlatformRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    private final BusinessAccessService businessAccessService;
    private final PlatformRepository platformRepository;
    private final OrderRepository orderRepository;
    private final OrderCalculationService calc;

    public OrderService(BusinessAccessService businessAccessService,
                        PlatformRepository platformRepository,
                        OrderRepository orderRepository,
                        OrderCalculationService calc) {
        this.businessAccessService = businessAccessService;
        this.platformRepository = platformRepository;
        this.orderRepository = orderRepository;
        this.calc = calc;
    }

    @Transactional
    public OrderResponse create(Long ownerUserId, Long businessId, Long platformId, CreateOrderRequest req) {
        Business business = businessAccessService.getBusinessOrThrow(ownerUserId, businessId);

        Platform platform = platformRepository.findByIdAndBusinessId(platformId, businessId)
                .orElseThrow(() -> new IllegalArgumentException("Platform not found for this business"));

        var computed = calc.calculate(
                req.grossAmount(),
                req.commissionRate(),
                req.gstRateOnComm(),
                req.netReceived()
        );

        Order o = new Order();
        o.setBusiness(business);
        o.setPlatform(platform);
        o.setOrderDate(req.orderDate());
        o.setGrossAmount(req.grossAmount());
        o.setCommissionRate(req.commissionRate());
        o.setGstRateOnComm(req.gstRateOnComm());
        o.setCommissionAmount(computed.commissionAmount());
        o.setGstOnCommission(computed.gstOnCommission());
        o.setNetExpected(computed.netExpected());
        o.setNetReceived(req.netReceived());
        o.setMismatchAmount(computed.mismatchAmount());
        o.setNotes(req.notes());

        Order saved = orderRepository.save(o);

        return new OrderResponse(
                saved.getId(),
                saved.getBusiness().getId(),
                saved.getPlatform().getId(),
                saved.getOrderDate(),
                saved.getGrossAmount(),
                saved.getCommissionRate(),
                saved.getGstRateOnComm(),
                saved.getCommissionAmount(),
                saved.getGstOnCommission(),
                saved.getNetExpected(),
                saved.getNetReceived(),
                saved.getMismatchAmount(),
                saved.getNotes()
        );
    }
}
