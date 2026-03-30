package com.fintech.payment_service.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "idempotency_keys")
@Data
public class IdempotencyKey {

    @Id
    @GeneratedValue
    private UUID id;

    private UUID merchantId;
    
    @Column(unique = true)
    private String idempotencyKey;

    private String requestHash;
    private UUID paymentId;

    private LocalDateTime createdAt;
}