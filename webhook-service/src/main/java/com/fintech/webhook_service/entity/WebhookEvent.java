package com.fintech.webhook_service.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "webhook_events")
@Data
public class WebhookEvent {

    @Id
    @GeneratedValue
    private UUID id;

    private String source; // CONNECTOR

    private String eventType; // PAYMENT_UPDATE

    @Column(unique = true)
    private String eventKey;

    @Column(columnDefinition = "jsonb")
    private String payload;

    private Boolean processed;

    private LocalDateTime receivedAt;
}