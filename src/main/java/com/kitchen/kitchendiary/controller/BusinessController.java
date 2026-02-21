package com.kitchen.kitchendiary.controller;

import com.kitchen.kitchendiary.dto.BusinessResponse;
import com.kitchen.kitchendiary.dto.CreateBusinessRequest;
import com.kitchen.kitchendiary.entities.Business;
import com.kitchen.kitchendiary.entities.User;
import com.kitchen.kitchendiary.repositories.BusinessRepository;
import com.kitchen.kitchendiary.repositories.UserRepository;
import jakarta.validation.Valid;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class BusinessController {

    private final UserRepository userRepository;
    private final BusinessRepository businessRepository;

    public BusinessController(UserRepository userRepository, BusinessRepository businessRepository) {
        this.userRepository = userRepository;
        this.businessRepository = businessRepository;
    }

    @PostMapping("/api/businesses")
    public BusinessResponse createBusiness(@RequestParam Long ownerUserId,
                                           @Valid @RequestBody CreateBusinessRequest request) {
        User owner = userRepository.findById(ownerUserId)
                .orElseThrow(() -> new IllegalArgumentException("Owner user not found"));

        Business b = new Business();
        b.setOwner(owner);
        b.setName(request.name());
        b.setGstin(request.gstin());
        b.setAddress(request.address());
        b.setCity(request.city());
        b.setState(request.state());

        return toResponse(businessRepository.save(b), ownerUserId);
    }

    @GetMapping("/api/businesses")
    @Transactional(readOnly = true)
    public List<BusinessResponse> listBusinesses(@RequestParam Long ownerUserId) {
        return businessRepository.findAllByOwnerIdOrderByCreatedAtDesc(ownerUserId)
                .stream()
                .map(business -> toResponse(business, ownerUserId))
                .toList();
    }

    @GetMapping("/api/businesses/{businessId}")
    @Transactional(readOnly = true)
    public BusinessResponse getBusiness(@RequestParam Long ownerUserId,
                                        @PathVariable Long businessId) {
        Business b = businessRepository.findByIdAndOwnerId(businessId, ownerUserId)
                .orElseThrow(() -> new IllegalArgumentException("Business not found / access denied"));
        return toResponse(b, ownerUserId);
    }

    private BusinessResponse toResponse(Business business, Long ownerUserId) {
        return new BusinessResponse(
                business.getId(),
                ownerUserId,
                business.getName(),
                business.getGstin(),
                business.getAddress(),
                business.getCity(),
                business.getState(),
                business.getCreatedAt()
        );
    }
}
