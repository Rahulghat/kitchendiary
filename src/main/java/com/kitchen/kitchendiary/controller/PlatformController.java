package com.kitchen.kitchendiary.controller;

import com.kitchen.kitchendiary.dto.CreatePlatformRequest;
import com.kitchen.kitchendiary.dto.PlatformResponse;
import com.kitchen.kitchendiary.entities.Business;
import com.kitchen.kitchendiary.entities.Platform;
import com.kitchen.kitchendiary.repositories.PlatformRepository;
import com.kitchen.kitchendiary.service.BusinessAccessService;
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
public class PlatformController {

    private final BusinessAccessService businessAccessService;
    private final PlatformRepository platformRepository;

    public PlatformController(BusinessAccessService businessAccessService,
                              PlatformRepository platformRepository) {
        this.businessAccessService = businessAccessService;
        this.platformRepository = platformRepository;
    }

    @PostMapping("/api/businesses/{businessId}/platforms")
    public PlatformResponse createPlatform(@RequestParam Long ownerUserId,
                                           @PathVariable Long businessId,
                                           @Valid @RequestBody CreatePlatformRequest request) {
        Business business = businessAccessService.getBusinessOrThrow(ownerUserId, businessId);
        if (platformRepository.existsByBusinessIdAndCode(businessId, request.code())) {
            throw new IllegalArgumentException("Platform code already exists for this business");
        }

        Platform platform = new Platform();
        platform.setBusiness(business);
        platform.setCode(request.code().trim().toUpperCase());
        platform.setName(request.name().trim());
        return toResponse(platformRepository.save(platform), businessId);
    }

    @GetMapping("/api/businesses/{businessId}/platforms")
    @Transactional(readOnly = true)
    public List<PlatformResponse> listPlatforms(@RequestParam Long ownerUserId,
                                                @PathVariable Long businessId) {
        businessAccessService.getBusinessOrThrow(ownerUserId, businessId);
        return platformRepository.findAllByBusinessId(businessId)
                .stream()
                .map(platform -> toResponse(platform, businessId))
                .toList();
    }

    private PlatformResponse toResponse(Platform platform, Long businessId) {
        return new PlatformResponse(
                platform.getId(),
                businessId,
                platform.getCode(),
                platform.getName(),
                platform.getCreatedAt()
        );
    }
}
