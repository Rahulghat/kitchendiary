package com.kitchen.kitchendiary.controller;

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
      @RequestHeader(value = "X-USER-ID", required = false) String userHeader,
      @PathVariable Long businessId,
      @Valid @RequestBody CreatePlatformRequest req) {
    Long ownerUserId = RequestUser.requireUserId(userHeader);
    return platformService.create(ownerUserId, businessId, req);
  }

  @GetMapping
  public List<Platform> listPlatforms(
      @RequestHeader(value = "X-USER-ID", required = false) String userHeader,
      @PathVariable Long businessId) {
    Long ownerUserId = RequestUser.requireUserId(userHeader);
    return platformService.list(ownerUserId, businessId);
  }
}
