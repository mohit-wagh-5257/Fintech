package com.fintech.platform.merchant.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "merchants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Merchant {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private UUID tenantId;

    @Column(nullable = false, unique = true, length = 50)
    private String merchantCode;

    @Column(nullable = false, length = 255)
    private String legalName;

    @Column(nullable = false, length = 255)
    private String displayName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private MerchantStatus status;

    @Column(nullable = false, length = 10)
    private String country;

    @Column(nullable = false, length = 10)
    private String baseCurrency;

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