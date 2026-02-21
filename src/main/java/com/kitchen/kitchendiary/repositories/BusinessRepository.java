package com.kitchen.kitchendiary.repositories;

import com.kitchen.kitchendiary.entities.Business;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BusinessRepository extends JpaRepository<Business, Long> {

  List<Business> findAllByOwnerId(Long ownerUserId);

  Optional<Business> findByIdAndOwnerId(Long businessId, Long ownerUserId);

  boolean existsByIdAndOwnerId(Long businessId, Long ownerUserId);

  List<Business> findAllByOwnerIdOrderByCreatedAtDesc(Long ownerUserId);
}
