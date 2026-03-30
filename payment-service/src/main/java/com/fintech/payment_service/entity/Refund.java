package com.fintech.payment_service.entity;

import com.fintech.payment_service.enums.RefundStatus;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "refunds")
@Data
public class Refund {

    @Id
    @GeneratedValue
    private UUID id;

    private UUID paymentId;

    private String refundRef;

    private UUID merchantId;

    private Long amount;
    private String currency;

    @Enumerated(EnumType.STRING)
    private RefundStatus status;

    private String reason;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}