package com.kitchen.kitchendiary.controller;

import com.kitchen.kitchendiary.config.CurrentUser;
import com.kitchen.kitchendiary.dto.DashboardResponse;
import com.kitchen.kitchendiary.service.DashboardService;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/businesses/{businessId}/dashboard")
public class DashboardController {

  private final DashboardService dashboardService;

  public DashboardController(DashboardService dashboardService) {
    this.dashboardService = dashboardService;
  }

  @GetMapping
  public DashboardResponse getDashboard(
      @PathVariable Long businessId,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
    Long ownerUserId = CurrentUser.id();
    return dashboardService.get(ownerUserId, businessId, startDate, endDate);
  }
}
