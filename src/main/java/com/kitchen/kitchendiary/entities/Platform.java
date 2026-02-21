package com.kitchen.kitchendiary.entities;

import jakarta.persistence.*;
import java.time.Instant;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
    name = "platforms",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"business_id", "code"})},
    indexes = {@Index(name = "idx_platform_business", columnList = "business_id")})
public class Platform {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "business_id")
  private Business business;

  @Column(nullable = false, length = 20)
  private String code; // ZOMATO, SWIGGY, DIRECT

  @Column(nullable = false, length = 80)
  private String name;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @PrePersist
  public void prePersist() {
    this.createdAt = Instant.now();
  }
}
