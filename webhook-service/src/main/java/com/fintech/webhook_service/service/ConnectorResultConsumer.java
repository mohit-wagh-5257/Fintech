package com.fintech.webhook_service.service;

import java.util.Map;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
public class ConnectorResultConsumer {

    private final WebhookService webhookService;
    private final WebhookEventProducer webhookEventProducer;

    public ConnectorResultConsumer(WebhookService webhookService,
                                   WebhookEventProducer webhookEventProducer) {
        this.webhookService = webhookService;
        this.webhookEventProducer = webhookEventProducer;
    }

    @KafkaListener(topics = "connector.result", groupId = "webhook-group")
    public void handleConnectorResult(Map<String, Object> payload, Acknowledgment acknowledgment) {
        try {
            webhookService.processConnectorResult(payload);
        } catch (Exception ex) {
            String paymentRef = String.valueOf(payload.getOrDefault("paymentRef", "unknown"));
            webhookEventProducer.send("connector.result.retry", paymentRef, payload);
        } finally {
            acknowledgment.acknowledge();
        }
    }

    @KafkaListener(topics = "connector.result.retry", groupId = "webhook-group")
    public void handleConnectorResultRetry(Map<String, Object> payload, Acknowledgment acknowledgment) {
        try {
            webhookService.processConnectorResult(payload);
        } catch (Exception ex) {
            String paymentRef = String.valueOf(payload.getOrDefault("paymentRef", "unknown"));
            webhookEventProducer.send("connector.result.dlt", paymentRef, payload);
        } finally {
            acknowledgment.acknowledge();
        }
    }
}
