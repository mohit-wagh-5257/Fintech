package com.fintech.webhook_service.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "processed_events")
@Data
public class ProcessedEvent {

    @Id
    private String eventId;

    private LocalDateTime processedAt;
}
