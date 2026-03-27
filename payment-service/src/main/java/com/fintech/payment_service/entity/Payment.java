package com.fintech.payment_service.entity;

import com.fintech.payment_service.enums.CaptureMode;
import com.fintech.payment_service.enums.PaymentMethodType;
import com.fintech.payment_service.enums.PaymentStatus;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "payments")
@Data
public class Payment {

    @Id
    @GeneratedValue
    private UUID id;

    private String paymentRef; // public reference

    private UUID orderId;
    private UUID merchantId;
    private UUID tenantId;

    private Long amount;
    private Long authorizedAmount;
    private Long capturedAmount;
    private Long refundedAmount;

    private String currency;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @Enumerated(EnumType.STRING)
    private PaymentMethodType paymentMethodType;

    @Enumerated(EnumType.STRING)
    private CaptureMode captureMode;

    private UUID checkoutSessionId;
    private UUID customerId;

    @Column(columnDefinition = "jsonb")
    private String metadataJson;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}