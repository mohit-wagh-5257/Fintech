package com.fintech.payment_service.service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import com.fintech.payment_service.entity.ProcessedEvent;
import com.fintech.payment_service.repository.ProcessedEventRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentEventConsumer {

    private final PaymentService paymentService;
    private final PaymentEventProducer paymentEventProducer;
    private final ProcessedEventRepository processedEventRepository;

    @KafkaListener(topics = "webhook.received", groupId = "payment-group")
    public void handleWebhook(Map<String, Object> payload, Acknowledgment acknowledgment) {
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
            paymentService.handleWebhookUpdate(payload);
            acknowledgment.acknowledge();
            
        } catch (Exception ex) {
            log.error("Failed to handle webhook", ex);
            String paymentRef = String.valueOf(payload.getOrDefault("paymentRef", "unknown"));
            paymentEventProducer.send("webhook.received.retry", paymentRef, payload);
            acknowledgment.acknowledge();
        }
    }

    @KafkaListener(topics = "webhook.received.retry", groupId = "payment-group")
    public void handleWebhookRetry(Map<String, Object> payload, Acknowledgment acknowledgment) {
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
            paymentService.handleWebhookUpdate(payload);
            acknowledgment.acknowledge();
            
        } catch (Exception ex) {
            log.error("Failed to handle webhook retry", ex);
            String paymentRef = String.valueOf(payload.getOrDefault("paymentRef", "unknown"));
            paymentEventProducer.send("webhook.received.dlt", paymentRef, payload);
            acknowledgment.acknowledge();
        }
    }
}
