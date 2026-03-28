package com.fintech.platform.order.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private UUID merchantId;

    @Column(nullable = false)
    private UUID tenantId;

    @Column(nullable = false, length = 100)
    private String merchantOrderRef;

    @Column(nullable = false, length = 10)
    private String currency;

    @Column(nullable = false)
    private Long amount;

    @Column(nullable = false)
    private Long amountPaid;

    @Column(nullable = false)
    private Long amountDue;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private OrderStatus status;

    private String description;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}