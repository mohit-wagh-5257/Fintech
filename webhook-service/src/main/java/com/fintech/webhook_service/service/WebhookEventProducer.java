package com.fintech.webhook_service.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebhookEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public WebhookEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(String topic, String key, Object payload) {
        kafkaTemplate.send(topic, key, payload);
    }
}
