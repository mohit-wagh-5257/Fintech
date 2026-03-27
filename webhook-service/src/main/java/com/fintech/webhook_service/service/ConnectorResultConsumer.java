package com.fintech.webhook_service.service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import com.fintech.webhook_service.entity.ProcessedEvent;
import com.fintech.webhook_service.repository.ProcessedEventRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConnectorResultConsumer {

    private final WebhookService webhookService;
    private final WebhookEventProducer webhookEventProducer;
    private final ProcessedEventRepository processedEventRepository;

    @KafkaListener(topics = "connector.result", groupId = "webhook-group")
    public void handleConnectorResult(Map<String, Object> payload, Acknowledgment acknowledgment) {
        try {
            String eventId = (String) payload.getOrDefault("eventId", UUID.randomUUID().toString());
            
            // DB-level idempotency: try to save first
            ProcessedEvent event = new ProcessedEvent();
            event.setEventId(eventId);
            event.setProcessedAt(LocalDateTime.now());
            
            try {
                processedEventRepository.save(event);
            } catch (DataIntegrityViolationException e) {
                // Already processed, skip
                log.info("Event already processed (duplicate): {}", eventId);
                acknowledgment.acknowledge();
                return;
            }
            
            // Only process if we successfully saved the event ID
            webhookService.processConnectorResult(payload);
            acknowledgment.acknowledge();
            
        } catch (Exception ex) {
            log.error("Failed to handle connector result", ex);
            String paymentRef = String.valueOf(payload.getOrDefault("paymentRef", "unknown"));
            webhookEventProducer.send("connector.result.retry", paymentRef, payload);
            acknowledgment.acknowledge();
        }
    }

    @KafkaListener(topics = "connector.result.retry", groupId = "webhook-group")
    public void handleConnectorResultRetry(Map<String, Object> payload, Acknowledgment acknowledgment) {
        try {
            String eventId = (String) payload.getOrDefault("eventId", UUID.randomUUID().toString());
            
            // DB-level idempotency: try to save first
            ProcessedEvent event = new ProcessedEvent();
            event.setEventId(eventId);
            event.setProcessedAt(LocalDateTime.now());
            
            try {
                processedEventRepository.save(event);
            } catch (DataIntegrityViolationException e) {
                // Already processed, skip
                log.info("Event already processed (duplicate): {}", eventId);
                acknowledgment.acknowledge();
                return;
            }
            
            // Only process if we successfully saved the event ID
            webhookService.processConnectorResult(payload);
            acknowledgment.acknowledge();
            
        } catch (Exception ex) {
            log.error("Failed to handle connector result retry", ex);
            String paymentRef = String.valueOf(payload.getOrDefault("paymentRef", "unknown"));
            webhookEventProducer.send("connector.result.dlt", paymentRef, payload);
            acknowledgment.acknowledge();
        }
    }
}
