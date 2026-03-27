package com.fintech.payment_service.entity;

import com.fintech.payment_service.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "payment_status_history")
@Data
public class PaymentStatusHistory {

    @Id
    @GeneratedValue
    private UUID id;

    private UUID paymentId;

    @Enumerated(EnumType.STRING)
    private PaymentStatus oldStatus;

    @Enumerated(EnumType.STRING)
    private PaymentStatus newStatus;

    private String reason;
    private String source; // API / WEBHOOK / SYSTEM

    private LocalDateTime changedAt;
}