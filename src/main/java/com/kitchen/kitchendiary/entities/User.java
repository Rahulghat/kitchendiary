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
    name = "users",
    indexes = {@Index(name = "idx_users_email", columnList = "email", unique = true)})
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 120)
  private String name;

  @Column(nullable = false, unique = true, length = 180)
  private String email;

  @Column(name = "password_hash", nullable = false)
  private String passwordHash;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @PrePersist
  public void prePersist() {
    this.createdAt = Instant.now();
  }
}
