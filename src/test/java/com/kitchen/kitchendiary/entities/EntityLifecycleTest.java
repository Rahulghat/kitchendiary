package com.kitchen.kitchendiary.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class EntityLifecycleTest {

  @Test
  void userPrePersist_shouldSetDefaults() {
    User user = new User();
    user.setRole(" ");
    user.prePersist();
    assertEquals("USER", user.getRole());
    assertNotNull(user.getCreatedAt());
  }

  @Test
  void businessAndPlatformPrePersist_shouldSetCreatedAt() {
    Business business = new Business();
    business.prePersist();
    assertNotNull(business.getCreatedAt());

    Platform platform = new Platform();
    platform.prePersist();
    assertNotNull(platform.getCreatedAt());
  }

  @Test
  void orderLifecycle_shouldSetTimestamps() {
    Order order = new Order();
    order.prePersist();
    assertNotNull(order.getCreatedAt());
    assertNotNull(order.getUpdatedAt());
    Instant oldUpdated = order.getUpdatedAt();
    order.preUpdate();
    assertNotNull(order.getUpdatedAt());
    assertEquals(true, order.getUpdatedAt().compareTo(oldUpdated) >= 0);
  }

  @Test
  void expensePrePersist_shouldSetCreatedAtWhenMissing() {
    Expense expense = new Expense();
    expense.prePersist();
    assertNotNull(expense.getCreatedAt());

    Instant fixed = Instant.parse("2026-02-22T00:00:00Z");
    expense.setCreatedAt(fixed);
    expense.prePersist();
    assertEquals(fixed, expense.getCreatedAt());
  }

  @Test
  void allArgsConstructors_shouldPopulateFields() {
    User owner = new User();
    owner.setId(1L);
    Business business =
        new Business(2L, owner, "Kitchen", "GST", "Addr", "Pune", "MH", Instant.now());
    Platform platform = new Platform(3L, business, "SWG", "Swiggy", Instant.now());
    Order order =
        new Order(
            4L,
            business,
            platform,
            LocalDate.of(2026, 2, 22),
            BigDecimal.ONE,
            BigDecimal.ONE,
            BigDecimal.ONE,
            BigDecimal.ONE,
            BigDecimal.ONE,
            BigDecimal.ONE,
            BigDecimal.ONE,
            BigDecimal.ZERO,
            "n",
            Instant.now(),
            Instant.now());
    Expense expense =
        new Expense(
            5L,
            business,
            LocalDate.of(2026, 2, 22),
            "packaging",
            BigDecimal.TEN,
            "n",
            Instant.now());
    assertEquals(2L, business.getId());
    assertEquals("SWG", platform.getCode());
    assertEquals(4L, order.getId());
    assertEquals(5L, expense.getId());
  }
}
