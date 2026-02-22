package com.kitchen.kitchendiary.controller;

import com.kitchen.kitchendiary.config.CurrentUser;
import com.kitchen.kitchendiary.dto.CreateBusinessRequest;
import com.kitchen.kitchendiary.entities.Business;
import com.kitchen.kitchendiary.service.BusinessService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/businesses")
public class BusinessController {

  private final BusinessService businessService;

  public BusinessController(BusinessService businessService) {
    this.businessService = businessService;
  }

  @PostMapping
  public Business createBusiness(@Valid @RequestBody CreateBusinessRequest req) {
    Long ownerUserId = CurrentUser.id();
    return businessService.create(ownerUserId, req);
  }

  @GetMapping
  public List<Business> listMyBusinesses() {
    Long ownerUserId = CurrentUser.id();
    return businessService.list(ownerUserId);
  }

  @GetMapping("/{businessId}")
  public Business getBusiness(@PathVariable Long businessId) {
    Long ownerUserId = CurrentUser.id();
    return businessService.get(ownerUserId, businessId);
  }
}
