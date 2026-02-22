package com.kitchen.kitchendiary.controller;

import com.kitchen.kitchendiary.config.CurrentUser;
import com.kitchen.kitchendiary.dto.CreateBusinessRequest;
import com.kitchen.kitchendiary.dto.CreateExpenseRequest;
import com.kitchen.kitchendiary.dto.CreateOrderRequest;
import com.kitchen.kitchendiary.dto.CreatePlatformRequest;
import com.kitchen.kitchendiary.dto.ExpenseResponse;
import com.kitchen.kitchendiary.dto.OrderResponse;
import com.kitchen.kitchendiary.service.BusinessService;
import com.kitchen.kitchendiary.service.DashboardService;
import com.kitchen.kitchendiary.service.ExpenseQueryService;
import com.kitchen.kitchendiary.service.ExpenseService;
import com.kitchen.kitchendiary.service.OrderQueryService;
import com.kitchen.kitchendiary.service.OrderService;
import com.kitchen.kitchendiary.service.PlatformService;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.YearMonth;
import java.util.List;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

@Controller
public class UiController {
  private static final int MIN_PAGE_SIZE = 5;
  private static final int MAX_PAGE_SIZE = 100;
  private static final String XLSX_CONTENT_TYPE =
      "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
  private static final String TEMPLATE_EN = "ui/index";
  private static final String TEMPLATE_MR = "ui/index-mr";
  private static final String TEMPLATE_HI = "ui/index-hi";
  private static final String PATH_EN = "/ui";
  private static final String PATH_MR = "/ui/mr";
  private static final String PATH_HI = "/ui/hi";
  private static final String DEFAULT_MESSAGE = "Operation failed";
  private static final String STATUS_INTERNAL_ERROR = "Internal Server Error";

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
      @RequestParam(required = false) Long businessId,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate startDate,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate endDate,
      @RequestParam(required = false) Long platformId,
      @RequestParam(required = false) String category,
      @RequestParam(defaultValue = "orderDate") String ordersSortBy,
      @RequestParam(defaultValue = "desc") String ordersSortDir,
      @RequestParam(defaultValue = "expenseDate") String expensesSortBy,
      @RequestParam(defaultValue = "desc") String expensesSortDir,
      @RequestParam(defaultValue = "0") int ordersPage,
      @RequestParam(defaultValue = "0") int expensesPage,
      @RequestParam(defaultValue = "10") int pageSize,
      @RequestParam(required = false) String message,
      @RequestParam(required = false) String error,
      Model model) {
    return renderUi("en",
        businessId,
        startDate,
        endDate,
        platformId,
        category,
        ordersSortBy,
        ordersSortDir,
        expensesSortBy,
        expensesSortDir,
        ordersPage,
        expensesPage,
        pageSize,
        message,
        error,
        model);
  }

  @GetMapping("/ui/mr")
  public String uiMarathi(
      @RequestParam(required = false) Long businessId,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate startDate,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate endDate,
      @RequestParam(required = false) Long platformId,
      @RequestParam(required = false) String category,
      @RequestParam(defaultValue = "orderDate") String ordersSortBy,
      @RequestParam(defaultValue = "desc") String ordersSortDir,
      @RequestParam(defaultValue = "expenseDate") String expensesSortBy,
      @RequestParam(defaultValue = "desc") String expensesSortDir,
      @RequestParam(defaultValue = "0") int ordersPage,
      @RequestParam(defaultValue = "0") int expensesPage,
      @RequestParam(defaultValue = "10") int pageSize,
      @RequestParam(required = false) String message,
      @RequestParam(required = false) String error,
      Model model) {
    return renderUi("mr",
        businessId,
        startDate,
        endDate,
        platformId,
        category,
        ordersSortBy,
        ordersSortDir,
        expensesSortBy,
        expensesSortDir,
        ordersPage,
        expensesPage,
        pageSize,
        message,
        error,
        model);
  }

  @GetMapping("/ui/hi")
  public String uiHindi(
      @RequestParam(required = false) Long businessId,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate startDate,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate endDate,
      @RequestParam(required = false) Long platformId,
      @RequestParam(required = false) String category,
      @RequestParam(defaultValue = "orderDate") String ordersSortBy,
      @RequestParam(defaultValue = "desc") String ordersSortDir,
      @RequestParam(defaultValue = "expenseDate") String expensesSortBy,
      @RequestParam(defaultValue = "desc") String expensesSortDir,
      @RequestParam(defaultValue = "0") int ordersPage,
      @RequestParam(defaultValue = "0") int expensesPage,
      @RequestParam(defaultValue = "10") int pageSize,
      @RequestParam(required = false) String message,
      @RequestParam(required = false) String error,
      Model model) {
    return renderUi("hi",
        businessId,
        startDate,
        endDate,
        platformId,
        category,
        ordersSortBy,
        ordersSortDir,
        expensesSortBy,
        expensesSortDir,
        ordersPage,
        expensesPage,
        pageSize,
        message,
        error,
        model);
  }

  @GetMapping("/ui/lists")
  public String listsWindow(
      @RequestParam(defaultValue = "en") String lang,
      @RequestParam(required = false) Long businessId,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate startDate,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate endDate,
      @RequestParam(required = false) Long platformId,
      @RequestParam(required = false) String category,
      @RequestParam(defaultValue = "orderDate") String ordersSortBy,
      @RequestParam(defaultValue = "desc") String ordersSortDir,
      @RequestParam(defaultValue = "expenseDate") String expensesSortBy,
      @RequestParam(defaultValue = "desc") String expensesSortDir,
      @RequestParam(defaultValue = "0") int ordersPage,
      @RequestParam(defaultValue = "0") int expensesPage,
      @RequestParam(defaultValue = "10") int pageSize,
      Model model) {
    Long currentUserId = CurrentUser.id();
    List<com.kitchen.kitchendiary.entities.Business> businesses = businessService.list(currentUserId);

    Long effectiveBusinessId = businessId;
    if (effectiveBusinessId == null && !businesses.isEmpty()) {
      effectiveBusinessId = businesses.get(0).getId();
    }

    List<com.kitchen.kitchendiary.entities.Platform> platforms = List.of();
    if (effectiveBusinessId != null) {
      try {
        platforms = platformService.list(currentUserId, effectiveBusinessId);
      } catch (RuntimeException ex) {
        model.addAttribute("error", messageOrDefault(ex));
      }
    }

    LocalDate effectiveStart = startDate == null ? YearMonth.now().atDay(1) : startDate;
    LocalDate effectiveEnd = endDate == null ? LocalDate.now() : endDate;
    int effectivePageSize = Math.min(Math.max(pageSize, MIN_PAGE_SIZE), MAX_PAGE_SIZE);

    model.addAttribute("lang", lang);
    model.addAttribute("isAdmin", CurrentUser.isAdmin());
    model.addAttribute("loggedInEmail", CurrentUser.email());
    model.addAttribute("loggedInRole", CurrentUser.role());
    model.addAttribute("selectedBusinessId", effectiveBusinessId);
    model.addAttribute("selectedPlatformId", platformId);
    model.addAttribute("startDate", effectiveStart);
    model.addAttribute("endDate", effectiveEnd);
    model.addAttribute("category", category == null ? "" : category);
    model.addAttribute("ordersSortBy", ordersSortBy);
    model.addAttribute("ordersSortDir", ordersSortDir);
    model.addAttribute("expensesSortBy", expensesSortBy);
    model.addAttribute("expensesSortDir", expensesSortDir);
    model.addAttribute("ordersPage", Math.max(0, ordersPage));
    model.addAttribute("expensesPage", Math.max(0, expensesPage));
    model.addAttribute("pageSize", effectivePageSize);
    model.addAttribute("businesses", businesses);
    model.addAttribute("platforms", platforms);
    return "ui/lists";
  }

  private String renderUi(
      String lang,
      Long businessId,
      LocalDate startDate,
      LocalDate endDate,
      Long platformId,
      String category,
      String ordersSortBy,
      String ordersSortDir,
      String expensesSortBy,
      String expensesSortDir,
      int ordersPage,
      int expensesPage,
      int pageSize,
      String message,
      String error,
      Model model) {
    Long currentUserId = CurrentUser.id();
    boolean isAdmin = CurrentUser.isAdmin();
    String loggedInEmail = CurrentUser.email();
    String loggedInRole = CurrentUser.role();
    LocalDate effectiveStart = startDate == null ? YearMonth.now().atDay(1) : startDate;
    LocalDate effectiveEnd = endDate == null ? LocalDate.now() : endDate;
    int effectivePageSize = Math.min(Math.max(pageSize, MIN_PAGE_SIZE), MAX_PAGE_SIZE);
    int safeOrdersPage = Math.max(0, ordersPage);
    int safeExpensesPage = Math.max(0, expensesPage);

    setupBaseModel(
        model,
        lang,
        isAdmin,
        loggedInEmail,
        loggedInRole,
        effectiveStart,
        effectiveEnd,
        platformId,
        category,
        ordersSortBy,
        ordersSortDir,
        expensesSortBy,
        expensesSortDir,
        safeOrdersPage,
        safeExpensesPage,
        effectivePageSize,
        message,
        error);

    List<com.kitchen.kitchendiary.entities.Business> businesses = businessService.list(currentUserId);
    model.addAttribute("businesses", businesses);

    Long effectiveBusinessId = businessId;
    if (effectiveBusinessId == null && !businesses.isEmpty()) {
      effectiveBusinessId = businesses.get(0).getId();
    }
    model.addAttribute("selectedBusinessId", effectiveBusinessId);

    if (effectiveBusinessId == null) {
      applyEmptyState(model);
      return templateForLang(lang);
    }

    try {
      applyBusinessData(
          model,
          currentUserId,
          effectiveBusinessId,
          effectiveStart,
          effectiveEnd,
          platformId,
          category,
          ordersSortBy,
          ordersSortDir,
          expensesSortBy,
          expensesSortDir,
          safeOrdersPage,
          safeExpensesPage,
          effectivePageSize);
    } catch (IllegalArgumentException ex) {
      model.addAttribute("error", messageOrDefault(ex));
      applyEmptyState(model);
    }

    return templateForLang(lang);
  }

  private void applyBusinessData(
      Model model,
      Long currentUserId,
      Long effectiveBusinessId,
      LocalDate effectiveStart,
      LocalDate effectiveEnd,
      Long platformId,
      String category,
      String ordersSortBy,
      String ordersSortDir,
      String expensesSortBy,
      String expensesSortDir,
      int safeOrdersPage,
      int safeExpensesPage,
      int effectivePageSize) {
    var selectedBusiness = businessService.get(currentUserId, effectiveBusinessId);
    var platforms = platformService.list(currentUserId, effectiveBusinessId);
    var ordersPageResult =
          orderQueryService.listPaged(
              currentUserId,
              effectiveBusinessId,
              effectiveStart,
              effectiveEnd,
              platformId,
              ordersSortBy,
              ordersSortDir,
              safeOrdersPage,
              effectivePageSize);
      var expensesPageResult =
          expenseQueryService.listPaged(
              currentUserId,
              effectiveBusinessId,
              effectiveStart,
              effectiveEnd,
              category,
              expensesSortBy,
              expensesSortDir,
              safeExpensesPage,
              effectivePageSize);
    var dashboard = dashboardService.get(currentUserId, effectiveBusinessId, effectiveStart, effectiveEnd);

    model.addAttribute("selectedBusiness", selectedBusiness);
    model.addAttribute("platforms", platforms);
    model.addAttribute("orders", ordersPageResult.getContent());
    model.addAttribute("expenses", expensesPageResult.getContent());
    model.addAttribute("ordersTotalPages", ordersPageResult.getTotalPages());
    model.addAttribute("ordersHasPrev", ordersPageResult.hasPrevious());
    model.addAttribute("ordersHasNext", ordersPageResult.hasNext());
    model.addAttribute("expensesTotalPages", expensesPageResult.getTotalPages());
    model.addAttribute("expensesHasPrev", expensesPageResult.hasPrevious());
    model.addAttribute("expensesHasNext", expensesPageResult.hasNext());
    model.addAttribute("dashboard", dashboard);
  }

  private void setupBaseModel(
      Model model,
      String lang,
      boolean isAdmin,
      String loggedInEmail,
      String loggedInRole,
      LocalDate startDate,
      LocalDate endDate,
      Long platformId,
      String category,
      String ordersSortBy,
      String ordersSortDir,
      String expensesSortBy,
      String expensesSortDir,
      int ordersPage,
      int expensesPage,
      int pageSize,
      String message,
      String error) {
    model.addAttribute("lang", lang);
    model.addAttribute("isAdmin", isAdmin);
    model.addAttribute("loggedInEmail", loggedInEmail);
    model.addAttribute("loggedInRole", loggedInRole);
    model.addAttribute("startDate", startDate);
    model.addAttribute("endDate", endDate);
    model.addAttribute("selectedPlatformId", platformId);
    model.addAttribute("category", category == null ? "" : category);
    model.addAttribute("ordersSortBy", ordersSortBy == null || ordersSortBy.isBlank() ? "orderDate" : ordersSortBy);
    model.addAttribute("ordersSortDir", "asc".equalsIgnoreCase(ordersSortDir) ? "asc" : "desc");
    model.addAttribute("expensesSortBy", expensesSortBy == null || expensesSortBy.isBlank() ? "expenseDate" : expensesSortBy);
    model.addAttribute("expensesSortDir", "asc".equalsIgnoreCase(expensesSortDir) ? "asc" : "desc");
    model.addAttribute("ordersPage", ordersPage);
    model.addAttribute("expensesPage", expensesPage);
    model.addAttribute("pageSize", pageSize);
    model.addAttribute("message", message);
    model.addAttribute("error", error);
  }

  private void applyEmptyState(Model model) {
    model.addAttribute("platforms", List.of());
    model.addAttribute("orders", List.of());
    model.addAttribute("expenses", List.of());
    model.addAttribute("ordersTotalPages", 0);
    model.addAttribute("ordersHasPrev", false);
    model.addAttribute("ordersHasNext", false);
    model.addAttribute("expensesTotalPages", 0);
    model.addAttribute("expensesHasPrev", false);
    model.addAttribute("expensesHasNext", false);
    model.addAttribute("dashboard", null);
  }

  @PostMapping("/ui/businesses")
  public String createBusiness(
      @RequestParam String name,
      @RequestParam(required = false) String gstin,
      @RequestParam(required = false) String address,
      @RequestParam(required = false) String city,
      @RequestParam(required = false) String state,
      @RequestParam(defaultValue = "en") String lang) {
    Long currentUserId = CurrentUser.id();
    try {
      var created =
          businessService.create(
              currentUserId, new CreateBusinessRequest(name, gstin, address, city, state));
      return redirectToUi(
          lang, created.getId(), null, null, null, null, "Business created", null);
    } catch (RuntimeException ex) {
      return redirectToUi(lang, null, null, null, null, null, null, messageOrDefault(ex));
    }
  }

  @PostMapping("/ui/platforms")
  public String createPlatform(
      @RequestParam Long businessId,
      @RequestParam String code,
      @RequestParam String name,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate startDate,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate endDate,
      @RequestParam(defaultValue = "en") String lang) {
    Long currentUserId = CurrentUser.id();
    try {
      platformService.create(currentUserId, businessId, new CreatePlatformRequest(code, name));
      return redirectToUi(
          lang, businessId, startDate, endDate, null, null, "Platform created", null);
    } catch (RuntimeException ex) {
      return redirectToUi(
          lang, businessId, startDate, endDate, null, null, null, messageOrDefault(ex));
    }
  }

  @PostMapping("/ui/orders")
  public String createOrder(
      @RequestParam Long businessId,
      @RequestParam Long platformId,
      @RequestParam("orderDateTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
          LocalDateTime orderDateTime,
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
    Long currentUserId = CurrentUser.id();
    try {
      Instant createdAt = orderDateTime.atZone(ZoneId.systemDefault()).toInstant();
      var created =
          orderService.create(
              currentUserId,
              businessId,
              platformId,
              new CreateOrderRequest(
                  orderDateTime.toLocalDate(),
                  grossAmount,
                  commissionRate,
                  gstRateOnComm,
                  netReceived,
                  notes),
              createdAt);
      return redirectToInvoice(lang, businessId, created.id(), startDate, endDate, true);
    } catch (RuntimeException ex) {
      return redirectToUi(
          lang, businessId, startDate, endDate, null, null, null, messageOrDefault(ex));
    }
  }

  @PostMapping("/ui/expenses")
  public String createExpense(
      @RequestParam Long businessId,
      @RequestParam("expenseDateTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
          LocalDateTime expenseDateTime,
      @RequestParam String category,
      @RequestParam BigDecimal amount,
      @RequestParam(required = false) String notes,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate startDate,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate endDate,
      @RequestParam(defaultValue = "en") String lang) {
    Long currentUserId = CurrentUser.id();
    try {
      Instant createdAt = expenseDateTime.atZone(ZoneId.systemDefault()).toInstant();
      expenseService.create(
          currentUserId,
          businessId,
          new CreateExpenseRequest(expenseDateTime.toLocalDate(), category, amount, notes),
          createdAt);
      return redirectToUi(
          lang, businessId, startDate, endDate, null, null, "Expense created", null);
    } catch (RuntimeException ex) {
      return redirectToUi(
          lang, businessId, startDate, endDate, null, null, null, messageOrDefault(ex));
    }
  }

  @GetMapping("/ui/orders/{orderId}/invoice")
  public String invoice(
      @PathVariable Long orderId,
      @RequestParam Long businessId,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate startDate,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate endDate,
      @RequestParam(defaultValue = "en") String lang,
      @RequestParam(defaultValue = "false") boolean autoPrint,
      Model model) {
    Long currentUserId = CurrentUser.id();
    try {
      var invoice = orderQueryService.getInvoice(currentUserId, businessId, orderId);
      model.addAttribute("invoice", invoice);
      model.addAttribute("autoPrint", autoPrint);
      model.addAttribute("lang", lang);
      model.addAttribute("isAdmin", CurrentUser.isAdmin());
      model.addAttribute("loggedInEmail", CurrentUser.email());
      model.addAttribute("loggedInRole", CurrentUser.role());
      model.addAttribute("businessId", businessId);
      model.addAttribute("startDate", startDate);
      model.addAttribute("endDate", endDate);
      return "ui/invoice";
    } catch (RuntimeException ex) {
      return redirectToUi(
          lang, businessId, startDate, endDate, null, null, null, messageOrDefault(ex));
    }
  }

  @GetMapping("/ui/export/orders")
  public ResponseEntity<byte[]> exportOrdersExcel(
      @RequestParam Long businessId,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate startDate,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate endDate,
      @RequestParam(required = false) Long platformId) {
    Long currentUserId = CurrentUser.id();
    try {
      LocalDate effectiveStart = startDate == null ? YearMonth.now().atDay(1) : startDate;
      LocalDate effectiveEnd = endDate == null ? LocalDate.now() : endDate;
      var orders =
          orderQueryService.list(currentUserId, businessId, effectiveStart, effectiveEnd, platformId);
      byte[] content = ordersWorkbook(orders);
      String filename = "orders_" + businessId + "_" + effectiveStart + "_to_" + effectiveEnd + ".xlsx";
      return excelResponse(content, filename);
    } catch (RuntimeException ex) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, messageOrDefault(ex), ex);
    }
  }

  @GetMapping("/ui/export/expenses")
  public ResponseEntity<byte[]> exportExpensesExcel(
      @RequestParam Long businessId,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate startDate,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate endDate,
      @RequestParam(required = false) String category) {
    Long currentUserId = CurrentUser.id();
    try {
      LocalDate effectiveStart = startDate == null ? YearMonth.now().atDay(1) : startDate;
      LocalDate effectiveEnd = endDate == null ? LocalDate.now() : endDate;
      var expenses =
          expenseQueryService.list(currentUserId, businessId, effectiveStart, effectiveEnd, category);
      byte[] content = expensesWorkbook(expenses);
      String filename =
          "expenses_" + businessId + "_" + effectiveStart + "_to_" + effectiveEnd + ".xlsx";
      return excelResponse(content, filename);
    } catch (RuntimeException ex) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, messageOrDefault(ex), ex);
    }
  }

  private String redirectToUi(
      String lang,
      Long businessId,
      LocalDate startDate,
      LocalDate endDate,
      Long platformId,
      String category,
      String message,
      String error) {
    UriComponentsBuilder b = UriComponentsBuilder.fromPath(pathForLang(lang));
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
      Long businessId,
      Long orderId,
      LocalDate startDate,
      LocalDate endDate,
      boolean autoPrint) {
    UriComponentsBuilder b =
        UriComponentsBuilder.fromPath("/ui/orders/{orderId}/invoice")
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

  private String templateForLang(String lang) {
    if ("mr".equals(lang)) {
      return TEMPLATE_MR;
    }
    if ("hi".equals(lang)) {
      return TEMPLATE_HI;
    }
    return TEMPLATE_EN;
  }

  private String pathForLang(String lang) {
    if ("mr".equals(lang)) {
      return PATH_MR;
    }
    if ("hi".equals(lang)) {
      return PATH_HI;
    }
    return PATH_EN;
  }

  private ResponseEntity<byte[]> excelResponse(byte[] content, String filename) {
    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
        .contentType(MediaType.parseMediaType(XLSX_CONTENT_TYPE))
        .body(content);
  }

  private byte[] ordersWorkbook(List<OrderResponse> orders) {
    try (XSSFWorkbook workbook = new XSSFWorkbook();
        ByteArrayOutputStream out = new ByteArrayOutputStream()) {
      Sheet sheet = workbook.createSheet("Orders");
      String[] headers = {
        "Order ID",
        "Business ID",
        "Platform ID",
        "Order Date",
        "Gross Amount",
        "Commission Rate",
        "GST Rate On Comm",
        "Commission Amount",
        "GST On Commission",
        "Net Expected",
        "Net Received",
        "Mismatch Amount",
        "Notes"
      };
      Row header = sheet.createRow(0);
      for (int i = 0; i < headers.length; i++) {
        header.createCell(i).setCellValue(headers[i]);
      }

      int rowNum = 1;
      for (OrderResponse o : orders) {
        Row row = sheet.createRow(rowNum++);
        row.createCell(0).setCellValue(nvlLong(o.id()));
        row.createCell(1).setCellValue(nvlLong(o.businessId()));
        row.createCell(2).setCellValue(nvlLong(o.platformId()));
        row.createCell(3).setCellValue(o.orderDate() == null ? "" : o.orderDate().toString());
        row.createCell(4).setCellValue(nvlNum(o.grossAmount()));
        row.createCell(5).setCellValue(nvlNum(o.commissionRate()));
        row.createCell(6).setCellValue(nvlNum(o.gstRateOnComm()));
        row.createCell(7).setCellValue(nvlNum(o.commissionAmount()));
        row.createCell(8).setCellValue(nvlNum(o.gstOnCommission()));
        row.createCell(9).setCellValue(nvlNum(o.netExpected()));
        row.createCell(10).setCellValue(nvlNum(o.netReceived()));
        row.createCell(11).setCellValue(nvlNum(o.mismatchAmount()));
        row.createCell(12).setCellValue(o.notes() == null ? "" : o.notes());
      }

      for (int i = 0; i < headers.length; i++) {
        sheet.autoSizeColumn(i);
      }

      workbook.write(out);
      return out.toByteArray();
    } catch (IOException ex) {
      throw new ResponseStatusException(
          HttpStatus.INTERNAL_SERVER_ERROR, STATUS_INTERNAL_ERROR, ex);
    }
  }

  private byte[] expensesWorkbook(List<ExpenseResponse> expenses) {
    try (XSSFWorkbook workbook = new XSSFWorkbook();
        ByteArrayOutputStream out = new ByteArrayOutputStream()) {
      Sheet sheet = workbook.createSheet("Expenses");
      String[] headers = {"Expense ID", "Business ID", "Expense Date", "Category", "Amount", "Notes"};
      Row header = sheet.createRow(0);
      for (int i = 0; i < headers.length; i++) {
        header.createCell(i).setCellValue(headers[i]);
      }

      int rowNum = 1;
      for (ExpenseResponse e : expenses) {
        Row row = sheet.createRow(rowNum++);
        row.createCell(0).setCellValue(nvlLong(e.id()));
        row.createCell(1).setCellValue(nvlLong(e.businessId()));
        row.createCell(2).setCellValue(e.expenseDate() == null ? "" : e.expenseDate().toString());
        row.createCell(3).setCellValue(e.category() == null ? "" : e.category());
        row.createCell(4).setCellValue(nvlNum(e.amount()));
        row.createCell(5).setCellValue(e.notes() == null ? "" : e.notes());
      }

      for (int i = 0; i < headers.length; i++) {
        sheet.autoSizeColumn(i);
      }

      workbook.write(out);
      return out.toByteArray();
    } catch (IOException ex) {
      throw new ResponseStatusException(
          HttpStatus.INTERNAL_SERVER_ERROR, STATUS_INTERNAL_ERROR, ex);
    }
  }

  private long nvlLong(Long value) {
    return value == null ? 0L : value;
  }

  private double nvlNum(BigDecimal value) {
    return value == null ? 0.0 : value.doubleValue();
  }

  private String messageOrDefault(RuntimeException ex) {
    if (ex.getMessage() == null || ex.getMessage().isBlank()) {
      return DEFAULT_MESSAGE;
    }
    return ex.getMessage();
  }
}
