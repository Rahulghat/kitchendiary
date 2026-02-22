package com.kitchen.kitchendiary.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import com.kitchen.kitchendiary.config.CurrentUser;
import com.kitchen.kitchendiary.dto.DashboardResponse;
import com.kitchen.kitchendiary.dto.ExpenseResponse;
import com.kitchen.kitchendiary.dto.OrderInvoiceResponse;
import com.kitchen.kitchendiary.dto.OrderResponse;
import com.kitchen.kitchendiary.entities.Business;
import com.kitchen.kitchendiary.entities.Platform;
import com.kitchen.kitchendiary.service.BusinessService;
import com.kitchen.kitchendiary.service.DashboardService;
import com.kitchen.kitchendiary.service.ExpenseQueryService;
import com.kitchen.kitchendiary.service.ExpenseService;
import com.kitchen.kitchendiary.service.OrderQueryService;
import com.kitchen.kitchendiary.service.OrderService;
import com.kitchen.kitchendiary.service.PlatformService;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class UiControllerTest {

  @Mock private BusinessService businessService;
  @Mock private PlatformService platformService;
  @Mock private OrderService orderService;
  @Mock private OrderQueryService orderQueryService;
  @Mock private ExpenseService expenseService;
  @Mock private ExpenseQueryService expenseQueryService;
  @Mock private DashboardService dashboardService;

  private UiController controller() {
    return new UiController(
        businessService,
        platformService,
        orderService,
        orderQueryService,
        expenseService,
        expenseQueryService,
        dashboardService);
  }

  @Test
  void homeRedirect_shouldReturnUiRedirect() {
    assertEquals("redirect:/ui", controller().homeRedirect());
  }

  @Test
  void ui_shouldRenderTemplateWithBusinessData() {
    Business business = new Business();
    business.setId(10L);
    business.setName("Kitchen 1");
    Platform platform = new Platform();
    platform.setId(12L);

    var orders =
        new PageImpl<>(
            List.of(sampleOrderResponse()),
            PageRequest.of(0, 1),
            2);
    var expenses =
        new PageImpl<>(
            List.of(sampleExpenseResponse()),
            PageRequest.of(0, 1),
            2);

    when(businessService.list(1L)).thenReturn(List.of(business));
    when(businessService.get(1L, 10L)).thenReturn(business);
    when(platformService.list(1L, 10L)).thenReturn(List.of(platform));
    when(orderQueryService.listPaged(
            eq(1L),
            eq(10L),
            any(LocalDate.class),
            any(LocalDate.class),
            eq(null),
            eq("orderDate"),
            eq("desc"),
            eq(0),
            eq(10)))
        .thenReturn(orders);
    when(expenseQueryService.listPaged(
            eq(1L),
            eq(10L),
            any(LocalDate.class),
            any(LocalDate.class),
            eq(""),
            eq("expenseDate"),
            eq("desc"),
            eq(0),
            eq(10)))
        .thenReturn(expenses);
    when(dashboardService.get(eq(1L), eq(10L), any(LocalDate.class), any(LocalDate.class)))
        .thenReturn(
            new DashboardResponse(
                10L,
                LocalDate.of(2026, 2, 1),
                LocalDate.of(2026, 2, 22),
                BigDecimal.ONE,
                BigDecimal.ONE,
                BigDecimal.ONE,
                BigDecimal.ONE,
                BigDecimal.ONE,
                BigDecimal.ZERO,
                BigDecimal.ONE,
                BigDecimal.ONE));

    ExtendedModelMap model = new ExtendedModelMap();
    try (MockedStatic<CurrentUser> currentUser = mockCurrentUser()) {
      String view =
          controller()
              .ui(
                  null,
                  null,
                  null,
                  null,
                  "",
                  "orderDate",
                  "desc",
                  "expenseDate",
                  "desc",
                  0,
                  0,
                  10,
                  "ok",
                  null,
                  model);
      assertEquals("ui/index", view);
      assertEquals(10L, model.get("selectedBusinessId"));
      assertNotNull(model.get("dashboard"));
      assertTrue((Boolean) model.get("ordersHasNext"));
      currentUser.verify(CurrentUser::id);
    }
  }

  @Test
  void ui_shouldRenderEmptyStateWhenNoBusiness() {
    when(businessService.list(1L)).thenReturn(List.of());
    ExtendedModelMap model = new ExtendedModelMap();
    try (MockedStatic<CurrentUser> ignored = mockCurrentUser()) {
      String view =
          controller()
              .ui(
                  null, null, null, null, null, "", "", "", "", -1, -1, 1, null, null, model);
      assertEquals("ui/index", view);
      assertEquals(0, ((List<?>) model.get("orders")).size());
      assertEquals(5, model.get("pageSize"));
      assertEquals(0, model.get("ordersPage"));
    }
  }

  @Test
  void listsWindow_shouldSetErrorWhenPlatformListFails() {
    Business business = new Business();
    business.setId(10L);
    when(businessService.list(1L)).thenReturn(List.of(business));
    when(platformService.list(1L, 10L)).thenThrow(new IllegalArgumentException("platform error"));

    ExtendedModelMap model = new ExtendedModelMap();
    try (MockedStatic<CurrentUser> ignored = mockCurrentUser()) {
      String view =
          controller()
              .listsWindow(
                  "en",
                  null,
                  null,
                  null,
                  null,
                  null,
                  "orderDate",
                  "desc",
                  "expenseDate",
                  "desc",
                  0,
                  0,
                  10,
                  model);
      assertEquals("ui/lists", view);
      assertEquals("platform error", model.get("error"));
    }
  }

  @Test
  void createBusiness_shouldRedirectWithMessage() {
    Business business = new Business();
    business.setId(99L);
    when(businessService.create(eq(1L), any())).thenReturn(business);

    try (MockedStatic<CurrentUser> ignored = mockCurrentUser()) {
      String redirect = controller().createBusiness("B1", null, null, null, null, "mr");
      assertTrue(redirect.startsWith("redirect:/ui/mr"));
      assertTrue(redirect.contains("businessId=99"));
      assertTrue(redirect.contains("message="));
    }
  }

  @Test
  void createOrder_shouldRedirectToInvoice() {
    when(orderService.create(eq(1L), eq(10L), eq(12L), any(), any()))
        .thenReturn(sampleOrderResponse());

    try (MockedStatic<CurrentUser> ignored = mockCurrentUser()) {
      String redirect =
          controller()
              .createOrder(
                  10L,
                  12L,
                  LocalDateTime.of(2026, 2, 22, 12, 0),
                  new BigDecimal("100.00"),
                  new BigDecimal("10.00"),
                  new BigDecimal("18.00"),
                  new BigDecimal("85.00"),
                  "note",
                  LocalDate.of(2026, 2, 1),
                  LocalDate.of(2026, 2, 22),
                  "en");
      assertTrue(redirect.startsWith("redirect:/ui/orders/101/invoice"));
      assertTrue(redirect.contains("autoPrint=true"));
    }
  }

  @Test
  void createExpense_shouldRedirectWithSuccessMessage() {
    when(expenseService.create(eq(1L), eq(10L), any(), any())).thenReturn(sampleExpenseResponse());

    try (MockedStatic<CurrentUser> ignored = mockCurrentUser()) {
      String redirect =
          controller()
              .createExpense(
                  10L,
                  LocalDateTime.of(2026, 2, 22, 12, 0),
                  "packaging",
                  new BigDecimal("20.00"),
                  "note",
                  LocalDate.of(2026, 2, 1),
                  LocalDate.of(2026, 2, 22),
                  "en");
      assertTrue(redirect.contains("message="));
    }
  }

  @Test
  void invoice_shouldRenderInvoiceTemplate() {
    when(orderQueryService.getInvoice(1L, 10L, 101L)).thenReturn(sampleInvoice());
    ExtendedModelMap model = new ExtendedModelMap();

    try (MockedStatic<CurrentUser> ignored = mockCurrentUser()) {
      String view =
          controller()
              .invoice(
                  101L,
                  10L,
                  LocalDate.of(2026, 2, 1),
                  LocalDate.of(2026, 2, 22),
                  "en",
                  true,
                  model);
      assertEquals("ui/invoice", view);
      assertNotNull(model.get("invoice"));
      assertEquals(true, model.get("autoPrint"));
    }
  }

  @Test
  void exportOrdersExcel_shouldReturnAttachment() {
    when(orderQueryService.list(eq(1L), eq(10L), any(LocalDate.class), any(LocalDate.class), eq(null)))
        .thenReturn(List.of(sampleOrderResponse()));

    try (MockedStatic<CurrentUser> ignored = mockCurrentUser()) {
      var response = controller().exportOrdersExcel(10L, null, null, null);
      assertEquals(200, response.getStatusCode().value());
      assertTrue(response.getHeaders().getFirst("Content-Disposition").contains("orders_10"));
      assertNotNull(response.getBody());
      assertTrue(response.getBody().length > 0);
    }
  }

  @Test
  void exportExpensesExcel_shouldThrowBadRequestWhenQueryFails() {
    when(expenseQueryService.list(eq(1L), eq(10L), any(LocalDate.class), any(LocalDate.class), eq(null)))
        .thenThrow(new IllegalArgumentException("bad range"));

    try (MockedStatic<CurrentUser> ignored = mockCurrentUser()) {
      assertThrows(
          ResponseStatusException.class,
          () -> controller().exportExpensesExcel(10L, null, null, null));
    }
  }

  private MockedStatic<CurrentUser> mockCurrentUser() {
    MockedStatic<CurrentUser> currentUser = mockStatic(CurrentUser.class);
    currentUser.when(CurrentUser::id).thenReturn(1L);
    currentUser.when(CurrentUser::isAdmin).thenReturn(true);
    currentUser.when(CurrentUser::email).thenReturn("admin@demo.com");
    currentUser.when(CurrentUser::role).thenReturn("ADMIN");
    return currentUser;
  }

  private OrderResponse sampleOrderResponse() {
    return new OrderResponse(
        101L,
        10L,
        12L,
        LocalDate.of(2026, 2, 22),
        new BigDecimal("100.00"),
        new BigDecimal("10.00"),
        new BigDecimal("18.00"),
        new BigDecimal("10.00"),
        new BigDecimal("1.80"),
        new BigDecimal("88.20"),
        new BigDecimal("85.00"),
        new BigDecimal("-3.20"),
        "note",
        Instant.parse("2026-02-22T00:00:00Z"));
  }

  private ExpenseResponse sampleExpenseResponse() {
    return new ExpenseResponse(
        201L,
        10L,
        LocalDate.of(2026, 2, 21),
        "packaging",
        new BigDecimal("20.00"),
        "note",
        Instant.parse("2026-02-22T00:00:00Z"));
  }

  private OrderInvoiceResponse sampleInvoice() {
    return new OrderInvoiceResponse(
        101L,
        10L,
        "Kitchen 1",
        "addr",
        "Pune",
        "MH",
        "22AAAAA0000A1Z5",
        "Swiggy",
        "SWG",
        LocalDate.of(2026, 2, 22),
        new BigDecimal("100.00"),
        new BigDecimal("10.00"),
        new BigDecimal("18.00"),
        new BigDecimal("10.00"),
        new BigDecimal("1.80"),
        new BigDecimal("88.20"),
        new BigDecimal("85.00"),
        new BigDecimal("-3.20"),
        "note",
        Instant.parse("2026-02-22T00:00:00Z"));
  }
}
