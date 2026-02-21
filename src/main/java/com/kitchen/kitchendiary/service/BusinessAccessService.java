package com.kitchen.kitchendiary.service;


import com.kitchen.kitchendiary.entities.Business;
import com.kitchen.kitchendiary.repositories.BusinessRepository;
import org.springframework.stereotype.Service;

@Service
public class BusinessAccessService {

    private final BusinessRepository businessRepository;

    public BusinessAccessService(BusinessRepository businessRepository) {
        this.businessRepository = businessRepository;
    }

    public Business getBusinessOrThrow(Long ownerUserId, Long businessId) {
        return businessRepository.findByIdAndOwnerId(businessId, ownerUserId)
                .orElseThrow(() -> new IllegalArgumentException("Business not found / access denied"));
    }
}