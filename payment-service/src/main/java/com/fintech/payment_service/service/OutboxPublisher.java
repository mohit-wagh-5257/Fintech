package com.fintech.payment_service.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fintech.payment_service.entity.OutboxEvent;
import com.fintech.payment_service.repository.OutboxEventRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutboxPublisher {

    private final OutboxEventRepository outboxRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedDelay = 5000)
    public void publishEvents() {
        List<OutboxEvent> events = outboxRepository.findByPublishedFalse();

        for (OutboxEvent event : events) {
            try {
                String topicName = getTopicName(event.getEventType());
                
                // Deserialize payload to Map (CRITICAL: fixes serialization issue)
                Map<String, Object> payloadMap = objectMapper.readValue(
                    event.getPayload(),
                    new TypeReference<Map<String, Object>>() {}
                );
                
                // Add eventId and correlationId if not present
                if (!payloadMap.containsKey("eventId")) {
                    payloadMap.put("eventId", event.getId().toString());
                }
                if (!payloadMap.containsKey("correlationId")) {
                    payloadMap.put("correlationId", UUID.randomUUID().toString());
                }
                
                // Extract paymentRef as Kafka key (CRITICAL: ensures ordering per payment)
                String paymentRef = (String) payloadMap.get("paymentRef");
                if (paymentRef == null) {
                    log.warn("No paymentRef in payload, using aggregateId: {}", event.getAggregateId());
                    paymentRef = event.getAggregateId().toString();
                }

                // Send Map object (Jackson will serialize it properly without double-encoding)
                kafkaTemplate.send(topicName, paymentRef, payloadMap);

                event.setPublished(true);
                outboxRepository.save(event);

                log.info("Published event: {} with key: {}", event.getEventType(), paymentRef);

            } catch (Exception e) {
                log.error("Failed to publish event: {}", event.getId(), e);
                // do nothing → retry later
            }
        }
    }

    private String getTopicName(String eventType) {
        return switch (eventType) {
            case "PAYMENT_INITIATED" -> "payment.initiated";
            case "PAYMENT_UPDATED" -> "payment.updated";
            default -> "payment.events";
        };
    }
}
