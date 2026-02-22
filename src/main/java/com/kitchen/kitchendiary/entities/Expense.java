package com.kitchen.kitchendiary.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
    name = "expenses",
    indexes = {
      @Index(name = "idx_expense_business_date", columnList = "business_id, expense_date")
    })
public class Expense {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "business_id")
  private Business business;

  @Column(name = "expense_date", nullable = false)
  private LocalDate expenseDate;

  @Column(nullable = false, length = 80)
  private String category;

  @Column(nullable = false, precision = 12, scale = 2)
  private BigDecimal amount;

  private String notes;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @PrePersist
  public void prePersist() {
    if (this.createdAt == null) {
      this.createdAt = Instant.now();
    }
  }
}
