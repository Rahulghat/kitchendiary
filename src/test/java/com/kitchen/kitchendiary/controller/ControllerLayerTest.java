package com.kitchen.kitchendiary.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import com.kitchen.kitchendiary.config.CurrentUser;
import com.kitchen.kitchendiary.dto.CreateBusinessRequest;
import com.kitchen.kitchendiary.dto.CreateExpenseRequest;
import com.kitchen.kitchendiary.dto.CreateOrderRequest;
import com.kitchen.kitchendiary.dto.CreatePlatformRequest;
import com.kitchen.kitchendiary.dto.DashboardResponse;
import com.kitchen.kitchendiary.dto.ExpenseResponse;
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
import jakarta.validation.ConstraintViolationException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

@ExtendWith(MockitoExtension.class)
class ControllerLayerTest {

  @Test
  void apiControllers_shouldDelegateToServices() {
    BusinessService businessService = mock(BusinessService.class);
    OrderService orderService = mock(OrderService.class);
    OrderQueryService orderQueryService = mock(OrderQueryService.class);
    ExpenseService expenseService = mock(ExpenseService.class);
    ExpenseQueryService expenseQueryService = mock(ExpenseQueryService.class);
    PlatformService platformService = mock(PlatformService.class);
    DashboardService dashboardService = mock(DashboardService.class);

    BusinessController businessController = new BusinessController(businessService);
    OrderController orderController = new OrderController(orderService, orderQueryService);
    ExpenseController expenseController = new ExpenseController(expenseService, expenseQueryService);
    PlatformController platformController = new PlatformController(platformService);
    DashboardController dashboardController = new DashboardController(dashboardService);
    HealthController healthController = new HealthController();

    Business business = new Business();
    business.setId(22L);
    Platform platform = new Platform();
    platform.setId(33L);
    OrderResponse order =
        new OrderResponse(
            1L,
            22L,
            33L,
            LocalDate.of(2026, 2, 22),
            BigDecimal.ONE,
            BigDecimal.ONE,
            BigDecimal.ONE,
            BigDecimal.ONE,
            BigDecimal.ONE,
            BigDecimal.ONE,
            BigDecimal.ONE,
            BigDecimal.ZERO,
            "",
            null);
    ExpenseResponse expense =
        new ExpenseResponse(1L, 22L, LocalDate.of(2026, 2, 22), "cat", BigDecimal.ONE, "", null);
    DashboardResponse dashboard =
        new DashboardResponse(
            22L,
            LocalDate.of(2026, 2, 1),
            LocalDate.of(2026, 2, 22),
            BigDecimal.ONE,
            BigDecimal.ONE,
            BigDecimal.ONE,
            BigDecimal.ONE,
            BigDecimal.ONE,
            BigDecimal.ZERO,
            BigDecimal.ONE,
            BigDecimal.ONE);

    when(businessService.create(any(), any())).thenReturn(business);
    when(businessService.list(5L)).thenReturn(List.of(business));
    when(businessService.get(5L, 22L)).thenReturn(business);
    when(orderService.create(any(), any(), any(), any())).thenReturn(order);
    when(orderQueryService.list(any(), any(), any(), any(), any())).thenReturn(List.of(order));
    when(expenseService.create(any(), any(), any())).thenReturn(expense);
    when(expenseQueryService.list(any(), any(), any(), any(), any())).thenReturn(List.of(expense));
    when(platformService.create(any(), any(), any())).thenReturn(platform);
    when(platformService.list(5L, 22L)).thenReturn(List.of(platform));
    when(dashboardService.get(any(), any(), any(), any())).thenReturn(dashboard);

    try (MockedStatic<CurrentUser> currentUser = mockStatic(CurrentUser.class)) {
      currentUser.when(CurrentUser::id).thenReturn(5L);

      assertEquals(business, businessController.createBusiness(new CreateBusinessRequest("b", null, null, null, null)));
      assertEquals(1, businessController.listMyBusinesses().size());
      assertEquals(business, businessController.getBusiness(22L));
      assertEquals(order, orderController.createOrder(22L, 33L, new CreateOrderRequest(LocalDate.now(), BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE, null)));
      assertEquals(1, orderController.listOrders(22L, LocalDate.now().minusDays(1), LocalDate.now(), null).size());
      assertEquals(expense, expenseController.createExpense(22L, new CreateExpenseRequest(LocalDate.now(), "cat", BigDecimal.ONE, null)));
      assertEquals(1, expenseController.listExpenses(22L, LocalDate.now().minusDays(1), LocalDate.now(), null).size());
      assertEquals(platform, platformController.createPlatform(22L, new CreatePlatformRequest("SWG", "Swiggy")));
      assertEquals(1, platformController.listPlatforms(22L).size());
      assertEquals(dashboard, dashboardController.getDashboard(22L, LocalDate.now().minusDays(1), LocalDate.now()));
      assertEquals("OK", healthController.health());
    }
  }

  @Test
  void authController_shouldRedirectAuthenticatedUsers() {
    AuthController controller = new AuthController();
    var auth = new TestingAuthenticationToken("u", "p", "ROLE_USER");
    auth.setAuthenticated(true);
    assertEquals("redirect:/ui", controller.login(auth));

    AnonymousAuthenticationToken anonymous =
        new AnonymousAuthenticationToken("k", "anonymousUser", Set.of(() -> "ROLE_ANONYMOUS"));
    assertEquals("login", controller.login(anonymous));
    assertEquals("login", controller.login(null));
  }

  @Test
  void apiExceptionHandler_shouldBuildErrorBodies() throws NoSuchMethodException {
    ApiExceptionHandler handler = new ApiExceptionHandler();

    Map<String, Object> bad = handler.badRequest(new IllegalArgumentException("bad"));
    assertEquals(400, bad.get("status"));
    assertEquals("bad", bad.get("message"));

    BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "obj");
    bindingResult.addError(new FieldError("obj", "email", "must not be blank"));
    MethodParameter parameter =
        new MethodParameter(
            ControllerLayerTest.class.getDeclaredMethod("dummyMethod", String.class),
            0);
    MethodArgumentNotValidException ex = new MethodArgumentNotValidException(parameter, bindingResult);
    Map<String, Object> validation = handler.validation(ex);
    assertTrue(validation.get("message").toString().contains("email"));

    ConstraintViolationException cve =
        new ConstraintViolationException("constraint failed", Set.of());
    Map<String, Object> constraint = handler.constraint(cve);
    assertEquals("constraint failed", constraint.get("message"));

    var response =
        handler.status(new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "oops"));
    assertEquals(400, response.getStatusCode().value());
    assertEquals("oops", response.getBody().get("message"));
  }

  @Test
  void adminUserController_shouldValidateAndCreateUsers() {
    var userRepository = mock(com.kitchen.kitchendiary.repositories.UserRepository.class);
    var encoder = mock(org.springframework.security.crypto.password.PasswordEncoder.class);
    AdminUserController controller = new AdminUserController(userRepository, encoder);

    when(userRepository.findAll()).thenReturn(List.of());
    when(userRepository.existsByEmailIgnoreCase("new@demo.com")).thenReturn(false);
    when(encoder.encode("secret1")).thenReturn("ENC");

    var model = new org.springframework.ui.ExtendedModelMap();
    assertEquals("admin/users", controller.usersPage(model));

    RedirectAttributesModelMap attrs = new RedirectAttributesModelMap();
    String ok = controller.createUser(" New User ", " NEW@demo.com ", "secret1", "admin", attrs);
    assertEquals("redirect:/admin/users", ok);
    assertTrue(attrs.asMap().get("message").toString().contains("new@demo.com"));

    RedirectAttributesModelMap badAttrs = new RedirectAttributesModelMap();
    String bad = controller.createUser("", "", "123", "bad-role", badAttrs);
    assertEquals("redirect:/admin/users", bad);
    assertEquals("Name is required", badAttrs.asMap().get("error"));
  }

  private static void dummyMethod(String value) {}
}
