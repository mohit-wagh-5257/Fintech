package com.fintech.connector_service.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ConnectorEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendConnectorResult(String paymentRef, String status) {
        ConnectorResultEvent event = new ConnectorResultEvent(paymentRef, status);
        kafkaTemplate.send("connector.result", paymentRef, event);
    }

    public void sendConnectorResult(String paymentRef, String status, String eventId, String correlationId) {
        Map<String, Object> event = new HashMap<>();
        event.put("paymentRef", paymentRef);
        event.put("status", status);
        event.put("eventId", eventId);
        event.put("correlationId", correlationId);
        kafkaTemplate.send("connector.result", paymentRef, event);
    }

    public void send(String topic, String key, Object payload) {
        kafkaTemplate.send(topic, key, payload);
    }

    public record ConnectorResultEvent(String paymentRef, String status) {
    }
}
