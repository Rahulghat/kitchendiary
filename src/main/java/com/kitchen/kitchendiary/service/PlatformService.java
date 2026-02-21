package com.kitchen.kitchendiary.service;

import com.kitchen.kitchendiary.dto.CreatePlatformRequest;
import com.kitchen.kitchendiary.entities.Business;
import com.kitchen.kitchendiary.entities.Platform;
import com.kitchen.kitchendiary.repositories.PlatformRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PlatformService {

  private final BusinessAccessService businessAccessService;
  private final PlatformRepository platformRepository;

  public PlatformService(
      BusinessAccessService businessAccessService, PlatformRepository platformRepository) {
    this.businessAccessService = businessAccessService;
    this.platformRepository = platformRepository;
  }

  @Transactional
  public Platform create(Long ownerUserId, Long businessId, CreatePlatformRequest req) {
    Business business = businessAccessService.getBusinessOrThrow(ownerUserId, businessId);

    String code = req.code().trim().toUpperCase();

    if (platformRepository.existsByBusinessIdAndCode(businessId, code)) {
      throw new IllegalArgumentException("Platform code already exists for this business");
    }

    Platform p = new Platform();
    p.setBusiness(business);
    p.setCode(code);
    p.setName(req.name());

    return platformRepository.save(p);
  }

  @Transactional(readOnly = true)
  public List<Platform> list(Long ownerUserId, Long businessId) {
    businessAccessService.getBusinessOrThrow(ownerUserId, businessId);
    return platformRepository.findAllByBusinessId(businessId);
  }
}
