package com.kitchen.kitchendiary.repositories;

import com.kitchen.kitchendiary.entities.Platform;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlatformRepository extends JpaRepository<Platform, Long> {

  List<Platform> findAllByBusinessId(Long businessId);

  Optional<Platform> findByBusinessIdAndCode(Long businessId, String code);

  boolean existsByBusinessIdAndCode(Long businessId, String code);

  Optional<Platform> findByIdAndBusinessId(Long platformId, Long businessId);
}
