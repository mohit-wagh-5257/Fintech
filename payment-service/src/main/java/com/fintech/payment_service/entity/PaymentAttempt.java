package com.fintech.payment_service.entity;

import com.fintech.payment_service.enums.PaymentAttemptStatus;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "payment_attempts")
@Data
public class PaymentAttempt {

    @Id
    @GeneratedValue
    private UUID id;

    private UUID paymentId;

    private Integer attemptNo;

    private String connectorName;
    private String connectorPaymentRef;

    @Enumerated(EnumType.STRING)
    private PaymentAttemptStatus status;

    private Long requestedAmount;
    private Long processedAmount;

    private String errorCode;
    private String errorMessage;

    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
}