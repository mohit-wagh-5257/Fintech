package com.fintech.webhook_service.service;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.fintech.webhook_service.entity.WebhookEvent;
import com.fintech.webhook_service.repository.WebhookEventRepository;

@Service
public class WebhookService {

    private final WebhookEventRepository repository;
    private final WebhookEventProducer webhookEventProducer;

    public WebhookService(WebhookEventRepository repository,
                          WebhookEventProducer webhookEventProducer) {
        this.repository = repository;
        this.webhookEventProducer = webhookEventProducer;
    }

    public void processConnectorResult(Map<String, Object> payload) {

        String eventKey = buildEventKey(payload);
        if (repository.existsByEventKey(eventKey)) {
            return;
        }

        WebhookEvent event = new WebhookEvent();
        event.setSource("CONNECTOR");
        event.setEventType("PAYMENT_UPDATE");
        event.setEventKey(eventKey);
        event.setPayload(payload.toString());
        event.setProcessed(false);
        event.setReceivedAt(LocalDateTime.now());

        event = repository.save(event);

        String paymentRef = String.valueOf(payload.getOrDefault("paymentRef", "unknown"));
        webhookEventProducer.send("webhook.received", paymentRef, payload);

        event.setProcessed(true);
        repository.save(event);
    }

    public void processWebhook(Map<String, Object> payload) {
        processConnectorResult(payload);
    }

    private String buildEventKey(Map<String, Object> payload) {
        return String.valueOf(payload.get("paymentRef")) + "|" + String.valueOf(payload.get("status"));
    }
}
