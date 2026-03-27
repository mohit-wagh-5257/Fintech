package com.fintech.webhook_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fintech.webhook_service.entity.ProcessedEvent;

public interface ProcessedEventRepository extends JpaRepository<ProcessedEvent, String> {
    
    Optional<ProcessedEvent> findByEventId(String eventId);
}
