package com.kitchen.kitchendiary.controller;

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
  public Business createBusiness(
      @RequestHeader(value = "X-USER-ID", required = false) String userHeader,
      @Valid @RequestBody CreateBusinessRequest req) {
    Long ownerUserId = RequestUser.requireUserId(userHeader);
    return businessService.create(ownerUserId, req);
  }

  @GetMapping
  public List<Business> listMyBusinesses(
      @RequestHeader(value = "X-USER-ID", required = false) String userHeader) {
    Long ownerUserId = RequestUser.requireUserId(userHeader);
    return businessService.list(ownerUserId);
  }

  @GetMapping("/{businessId}")
  public Business getBusiness(
      @RequestHeader(value = "X-USER-ID", required = false) String userHeader,
      @PathVariable Long businessId) {
    Long ownerUserId = RequestUser.requireUserId(userHeader);
    return businessService.get(ownerUserId, businessId);
  }
}
