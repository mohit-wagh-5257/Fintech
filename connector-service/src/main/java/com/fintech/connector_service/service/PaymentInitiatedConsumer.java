package com.fintech.connector_service.service;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentInitiatedConsumer {

    private static final long DEDUPE_WINDOW_SECONDS = 3600;

    private final ConnectorEventProducer connectorEventProducer;
    private final ConcurrentHashMap<String, LocalDateTime> processedEvents = new ConcurrentHashMap<>();

    private boolean markIfNew(String eventId) {
        cleanupOldEntries();
        return processedEvents.putIfAbsent(eventId, LocalDateTime.now()) == null;
    }

    private void cleanupOldEntries() {
        LocalDateTime cutoff = LocalDateTime.now().minusSeconds(DEDUPE_WINDOW_SECONDS);
        Iterator<Map.Entry<String, LocalDateTime>> iterator = processedEvents.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, LocalDateTime> entry = iterator.next();
            if (entry.getValue().isBefore(cutoff)) {
                iterator.remove();
            }
        }
    }

    @KafkaListener(topics = "payment.initiated", groupId = "connector-group")
    public void handlePayment(Map<String, Object> payment, Acknowledgment acknowledgment) {
        try {
            String eventId = (String) payment.getOrDefault("eventId", UUID.randomUUID().toString());

            // In-memory idempotency for stateless connector
            if (!markIfNew(eventId)) {
                log.info("Event already processed in dedupe window: {}", eventId);
                acknowledgment.acknowledge();
                return;
            }

            String paymentRef = String.valueOf(payment.get("paymentRef"));
            Number amount = (Number) payment.get("amount");
            String status = (amount.longValue() % 2 == 0) ? "SUCCESS" : "FAILED";
            String correlationId = (String) payment.getOrDefault("correlationId", UUID.randomUUID().toString());
            
            connectorEventProducer.sendConnectorResult(paymentRef, status, eventId, correlationId);
            acknowledgment.acknowledge();
            
        } catch (Exception ex) {
            log.error("Failed to handle payment", ex);
            String paymentRef = String.valueOf(payment.getOrDefault("paymentRef", "unknown"));
            connectorEventProducer.send("payment.initiated.retry", paymentRef, payment);
            acknowledgment.acknowledge();
        }
    }

    @KafkaListener(topics = "payment.initiated.retry", groupId = "connector-group")
    public void handlePaymentRetry(Map<String, Object> payment, Acknowledgment acknowledgment) {
        try {
            String eventId = (String) payment.getOrDefault("eventId", UUID.randomUUID().toString());

            // In-memory idempotency for stateless connector
            if (!markIfNew(eventId)) {
                log.info("Event already processed in dedupe window: {}", eventId);
                acknowledgment.acknowledge();
                return;
            }

            String paymentRef = String.valueOf(payment.get("paymentRef"));
            Number amount = (Number) payment.get("amount");
            String status = (amount.longValue() % 2 == 0) ? "SUCCESS" : "FAILED";
            String correlationId = (String) payment.getOrDefault("correlationId", UUID.randomUUID().toString());
            
            connectorEventProducer.sendConnectorResult(paymentRef, status, eventId, correlationId);
            acknowledgment.acknowledge();
            
        } catch (Exception ex) {
            log.error("Failed to handle payment retry", ex);
            String paymentRef = String.valueOf(payment.getOrDefault("paymentRef", "unknown"));
            connectorEventProducer.send("payment.initiated.dlt", paymentRef, payment);
            acknowledgment.acknowledge();
        }
    }
}
