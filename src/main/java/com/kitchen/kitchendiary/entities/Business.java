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
    name = "businesses",
    indexes = {@Index(name = "idx_business_owner", columnList = "owner_user_id")})
public class Business {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "owner_user_id")
  private User owner;

  @Column(nullable = false, length = 160)
  private String name;

  @Column(length = 15)
  private String gstin;

  private String address;
  private String city;
  private String state;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @PrePersist
  public void prePersist() {
    this.createdAt = Instant.now();
  }
}
