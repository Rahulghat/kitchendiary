package com.kitchen.kitchendiary.service;

import com.kitchen.kitchendiary.dto.CreateBusinessRequest;
import com.kitchen.kitchendiary.entities.Business;
import com.kitchen.kitchendiary.entities.User;
import com.kitchen.kitchendiary.repositories.BusinessRepository;
import com.kitchen.kitchendiary.repositories.UserRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BusinessService {

  private final BusinessRepository businessRepository;
  private final UserRepository userRepository;

  public BusinessService(BusinessRepository businessRepository, UserRepository userRepository) {
    this.businessRepository = businessRepository;
    this.userRepository = userRepository;
  }

  @Transactional
  public Business create(Long ownerUserId, CreateBusinessRequest req) {
    User owner =
        userRepository
            .findById(ownerUserId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

    Business b = new Business();
    b.setOwner(owner);
    b.setName(req.name());
    b.setGstin(req.gstin());
    b.setAddress(req.address());
    b.setCity(req.city());
    b.setState(req.state());

    return businessRepository.save(b);
  }

  @Transactional(readOnly = true)
  public List<Business> list(Long ownerUserId) {
    return businessRepository.findAllByOwnerIdOrderByCreatedAtDesc(ownerUserId);
  }

  @Transactional(readOnly = true)
  public Business get(Long ownerUserId, Long businessId) {
    return businessRepository
        .findByIdAndOwnerId(businessId, ownerUserId)
        .orElseThrow(() -> new IllegalArgumentException("Business not found / access denied"));
  }
}
