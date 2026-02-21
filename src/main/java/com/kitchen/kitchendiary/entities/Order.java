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
    name = "orders",
    indexes = {
      @Index(name = "idx_order_business_date", columnList = "business_id, order_date"),
      @Index(name = "idx_order_platform", columnList = "platform_id")
    })
public class Order {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "business_id")
  private Business business;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "platform_id")
  private Platform platform;

  @Column(name = "order_date", nullable = false)
  private LocalDate orderDate;

  @Column(name = "gross_amount", nullable = false, precision = 12, scale = 2)
  private BigDecimal grossAmount;

  @Column(name = "commission_rate", nullable = false, precision = 5, scale = 2)
  private BigDecimal commissionRate;

  @Column(name = "gst_rate_on_comm", nullable = false, precision = 5, scale = 2)
  private BigDecimal gstRateOnComm;

  @Column(name = "commission_amount", nullable = false, precision = 12, scale = 2)
  private BigDecimal commissionAmount;

  @Column(name = "gst_on_commission", nullable = false, precision = 12, scale = 2)
  private BigDecimal gstOnCommission;

  @Column(name = "net_expected", nullable = false, precision = 12, scale = 2)
  private BigDecimal netExpected;

  @Column(name = "net_received", nullable = false, precision = 12, scale = 2)
  private BigDecimal netReceived;

  @Column(name = "mismatch_amount", nullable = false, precision = 12, scale = 2)
  private BigDecimal mismatchAmount;

  private String notes;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  @PrePersist
  public void prePersist() {
    this.createdAt = Instant.now();
    this.updatedAt = Instant.now();
  }

  @PreUpdate
  public void preUpdate() {
    this.updatedAt = Instant.now();
  }
}
