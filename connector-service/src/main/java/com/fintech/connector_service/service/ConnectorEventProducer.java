package com.fintech.connector_service.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ConnectorEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public ConnectorEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendConnectorResult(String paymentRef, String status) {
        ConnectorResultEvent event = new ConnectorResultEvent(paymentRef, status);
        kafkaTemplate.send("connector.result", paymentRef, event);
    }

    public void send(String topic, String key, Object payload) {
        kafkaTemplate.send(topic, key, payload);
    }

    public record ConnectorResultEvent(String paymentRef, String status) {
    }
}
