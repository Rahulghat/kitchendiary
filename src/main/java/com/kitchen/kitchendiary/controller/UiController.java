package com.kitchen.kitchendiary.controller;

import com.kitchen.kitchendiary.dto.CreateBusinessRequest;
import com.kitchen.kitchendiary.dto.CreateExpenseRequest;
import com.kitchen.kitchendiary.dto.CreateOrderRequest;
import com.kitchen.kitchendiary.dto.CreatePlatformRequest;
import com.kitchen.kitchendiary.service.BusinessService;
import com.kitchen.kitchendiary.service.DashboardService;
import com.kitchen.kitchendiary.service.ExpenseQueryService;
import com.kitchen.kitchendiary.service.ExpenseService;
import com.kitchen.kitchendiary.service.OrderQueryService;
import com.kitchen.kitchendiary.service.OrderService;
import com.kitchen.kitchendiary.service.PlatformService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;

@Controller
public class UiController {

  private final BusinessService businessService;
  private final PlatformService platformService;
  private final OrderService orderService;
  private final OrderQueryService orderQueryService;
  private final ExpenseService expenseService;
  private final ExpenseQueryService expenseQueryService;
  private final DashboardService dashboardService;

  public UiController(
      BusinessService businessService,
      PlatformService platformService,
      OrderService orderService,
      OrderQueryService orderQueryService,
      ExpenseService expenseService,
      ExpenseQueryService expenseQueryService,
      DashboardService dashboardService) {
    this.businessService = businessService;
    this.platformService = platformService;
    this.orderService = orderService;
    this.orderQueryService = orderQueryService;
    this.expenseService = expenseService;
    this.expenseQueryService = expenseQueryService;
    this.dashboardService = dashboardService;
  }

  @GetMapping("/")
  public String homeRedirect() {
    return "redirect:/ui";
  }

  @GetMapping("/ui")
  public String ui(
      @RequestParam(defaultValue = "1") Long userId,
      @RequestParam(required = false) Long businessId,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate startDate,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate endDate,
      @RequestParam(required = false) Long platformId,
      @RequestParam(required = false) String category,
      @RequestParam(required = false) String message,
      @RequestParam(required = false) String error,
      Model model) {
    return renderUi(
        "en", userId, businessId, startDate, endDate, platformId, category, message, error, model);
  }

  @GetMapping("/ui/mr")
  public String uiMarathi(
      @RequestParam(defaultValue = "1") Long userId,
      @RequestParam(required = false) Long businessId,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate startDate,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate endDate,
      @RequestParam(required = false) Long platformId,
      @RequestParam(required = false) String category,
      @RequestParam(required = false) String message,
      @RequestParam(required = false) String error,
      Model model) {
    return renderUi(
        "mr", userId, businessId, startDate, endDate, platformId, category, message, error, model);
  }

  private String renderUi(
      String lang,
      Long userId,
      Long businessId,
      LocalDate startDate,
      LocalDate endDate,
      Long platformId,
      String category,
      String message,
      String error,
      Model model) {
    LocalDate effectiveStart = startDate == null ? YearMonth.now().atDay(1) : startDate;
    LocalDate effectiveEnd = endDate == null ? LocalDate.now() : endDate;

    model.addAttribute("lang", lang);
    model.addAttribute("userId", userId);
    model.addAttribute("startDate", effectiveStart);
    model.addAttribute("endDate", effectiveEnd);
    model.addAttribute("selectedPlatformId", platformId);
    model.addAttribute("category", category == null ? "" : category);
    model.addAttribute("message", message);
    model.addAttribute("error", error);

    List<com.kitchen.kitchendiary.entities.Business> businesses = businessService.list(userId);
    model.addAttribute("businesses", businesses);

    Long effectiveBusinessId = businessId;
    if (effectiveBusinessId == null && !businesses.isEmpty()) {
      effectiveBusinessId = businesses.get(0).getId();
    }
    model.addAttribute("selectedBusinessId", effectiveBusinessId);

    if (effectiveBusinessId == null) {
      model.addAttribute("platforms", List.of());
      model.addAttribute("orders", List.of());
      model.addAttribute("expenses", List.of());
      model.addAttribute("dashboard", null);
      return "mr".equals(lang) ? "ui/index-mr" : "ui/index";
    }

    try {
      var selectedBusiness = businessService.get(userId, effectiveBusinessId);
      var platforms = platformService.list(userId, effectiveBusinessId);
      var orders =
          orderQueryService.list(
              userId, effectiveBusinessId, effectiveStart, effectiveEnd, platformId);
      var expenses =
          expenseQueryService.list(
              userId, effectiveBusinessId, effectiveStart, effectiveEnd, category);
      var dashboard =
          dashboardService.get(userId, effectiveBusinessId, effectiveStart, effectiveEnd);

      model.addAttribute("selectedBusiness", selectedBusiness);
      model.addAttribute("platforms", platforms);
      model.addAttribute("orders", orders);
      model.addAttribute("expenses", expenses);
      model.addAttribute("dashboard", dashboard);
    } catch (IllegalArgumentException ex) {
      model.addAttribute("error", ex.getMessage());
      model.addAttribute("platforms", List.of());
      model.addAttribute("orders", List.of());
      model.addAttribute("expenses", List.of());
      model.addAttribute("dashboard", null);
    }

    return "mr".equals(lang) ? "ui/index-mr" : "ui/index";
  }

  @PostMapping("/ui/businesses")
  public String createBusiness(
      @RequestParam Long userId,
      @RequestParam String name,
      @RequestParam(required = false) String gstin,
      @RequestParam(required = false) String address,
      @RequestParam(required = false) String city,
      @RequestParam(required = false) String state,
      @RequestParam(defaultValue = "en") String lang) {
    try {
      var created =
          businessService.create(
              userId, new CreateBusinessRequest(name, gstin, address, city, state));
      return redirectToUi(
          lang, userId, created.getId(), null, null, null, null, "Business created", null);
    } catch (Exception ex) {
      return redirectToUi(lang, userId, null, null, null, null, null, null, ex.getMessage());
    }
  }

  @PostMapping("/ui/platforms")
  public String createPlatform(
      @RequestParam Long userId,
      @RequestParam Long businessId,
      @RequestParam String code,
      @RequestParam String name,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate startDate,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate endDate,
      @RequestParam(defaultValue = "en") String lang) {
    try {
      platformService.create(userId, businessId, new CreatePlatformRequest(code, name));
      return redirectToUi(
          lang, userId, businessId, startDate, endDate, null, null, "Platform created", null);
    } catch (Exception ex) {
      return redirectToUi(
          lang, userId, businessId, startDate, endDate, null, null, null, ex.getMessage());
    }
  }

  @PostMapping("/ui/orders")
  public String createOrder(
      @RequestParam Long userId,
      @RequestParam Long businessId,
      @RequestParam Long platformId,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate orderDate,
      @RequestParam BigDecimal grossAmount,
      @RequestParam BigDecimal commissionRate,
      @RequestParam BigDecimal gstRateOnComm,
      @RequestParam BigDecimal netReceived,
      @RequestParam(required = false) String notes,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate startDate,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate endDate,
      @RequestParam(defaultValue = "en") String lang) {
    try {
      var created =
          orderService.create(
              userId,
              businessId,
              platformId,
              new CreateOrderRequest(
                  orderDate, grossAmount, commissionRate, gstRateOnComm, netReceived, notes));
      return redirectToInvoice(lang, userId, businessId, created.id(), startDate, endDate, true);
    } catch (Exception ex) {
      return redirectToUi(
          lang, userId, businessId, startDate, endDate, null, null, null, ex.getMessage());
    }
  }

  @PostMapping("/ui/expenses")
  public String createExpense(
      @RequestParam Long userId,
      @RequestParam Long businessId,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate expenseDate,
      @RequestParam String category,
      @RequestParam BigDecimal amount,
      @RequestParam(required = false) String notes,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate startDate,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate endDate,
      @RequestParam(defaultValue = "en") String lang) {
    try {
      expenseService.create(
          userId, businessId, new CreateExpenseRequest(expenseDate, category, amount, notes));
      return redirectToUi(
          lang, userId, businessId, startDate, endDate, null, null, "Expense created", null);
    } catch (Exception ex) {
      return redirectToUi(
          lang, userId, businessId, startDate, endDate, null, null, null, ex.getMessage());
    }
  }

  @GetMapping("/ui/orders/{orderId}/invoice")
  public String invoice(
      @PathVariable Long orderId,
      @RequestParam Long userId,
      @RequestParam Long businessId,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate startDate,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate endDate,
      @RequestParam(defaultValue = "en") String lang,
      @RequestParam(defaultValue = "false") boolean autoPrint,
      Model model) {
    try {
      var invoice = orderQueryService.getInvoice(userId, businessId, orderId);
      model.addAttribute("invoice", invoice);
      model.addAttribute("autoPrint", autoPrint);
      model.addAttribute("lang", lang);
      model.addAttribute("userId", userId);
      model.addAttribute("businessId", businessId);
      model.addAttribute("startDate", startDate);
      model.addAttribute("endDate", endDate);
      return "ui/invoice";
    } catch (Exception ex) {
      return redirectToUi(
          lang, userId, businessId, startDate, endDate, null, null, null, ex.getMessage());
    }
  }

  private String redirectToUi(
      String lang,
      Long userId,
      Long businessId,
      LocalDate startDate,
      LocalDate endDate,
      Long platformId,
      String category,
      String message,
      String error) {
    UriComponentsBuilder b =
        UriComponentsBuilder.fromPath("mr".equals(lang) ? "/ui/mr" : "/ui")
            .queryParam("userId", userId);
    if (businessId != null) {
      b.queryParam("businessId", businessId);
    }
    if (startDate != null) {
      b.queryParam("startDate", startDate);
    }
    if (endDate != null) {
      b.queryParam("endDate", endDate);
    }
    if (platformId != null) {
      b.queryParam("platformId", platformId);
    }
    if (category != null && !category.isBlank()) {
      b.queryParam("category", category);
    }
    if (message != null && !message.isBlank()) {
      b.queryParam("message", message);
    }
    if (error != null && !error.isBlank()) {
      b.queryParam("error", error);
    }
    return "redirect:" + b.toUriString();
  }

  private String redirectToInvoice(
      String lang,
      Long userId,
      Long businessId,
      Long orderId,
      LocalDate startDate,
      LocalDate endDate,
      boolean autoPrint) {
    UriComponentsBuilder b =
        UriComponentsBuilder.fromPath("/ui/orders/{orderId}/invoice")
            .queryParam("userId", userId)
            .queryParam("businessId", businessId)
            .queryParam("lang", lang)
            .queryParam("autoPrint", autoPrint);
    if (startDate != null) {
      b.queryParam("startDate", startDate);
    }
    if (endDate != null) {
      b.queryParam("endDate", endDate);
    }
    return "redirect:" + b.buildAndExpand(orderId).toUriString();
  }
}
