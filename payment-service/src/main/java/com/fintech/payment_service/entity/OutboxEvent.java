package com.fintech.payment_service.entity;

import java.time.LocalDateTime;
import java.util.UUID;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "outbox_events")
@Data
public class OutboxEvent {

    @Id
    @GeneratedValue
    private UUID id;

    private String aggregateType; // PAYMENT
    private UUID aggregateId;

    private String eventType;

    @Column(columnDefinition = "jsonb")
    private String payload;

    private Boolean published;

    private LocalDateTime createdAt;
}