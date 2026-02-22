package com.kitchen.kitchendiary.controller;

import com.kitchen.kitchendiary.config.CurrentUser;
import com.kitchen.kitchendiary.dto.CreatePlatformRequest;
import com.kitchen.kitchendiary.entities.Platform;
import com.kitchen.kitchendiary.service.PlatformService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/businesses/{businessId}/platforms")
public class PlatformController {

  private final PlatformService platformService;

  public PlatformController(PlatformService platformService) {
    this.platformService = platformService;
  }

  @PostMapping
  public Platform createPlatform(
      @PathVariable Long businessId,
      @Valid @RequestBody CreatePlatformRequest req) {
    Long ownerUserId = CurrentUser.id();
    return platformService.create(ownerUserId, businessId, req);
  }

  @GetMapping
  public List<Platform> listPlatforms(@PathVariable Long businessId) {
    Long ownerUserId = CurrentUser.id();
    return platformService.list(ownerUserId, businessId);
  }
}
