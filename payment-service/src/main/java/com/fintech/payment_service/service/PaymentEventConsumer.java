package com.fintech.payment_service.service;

import java.util.Map;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
public class PaymentEventConsumer {

    private final PaymentService paymentService;
    private final PaymentEventProducer paymentEventProducer;

    public PaymentEventConsumer(PaymentService paymentService,
                                PaymentEventProducer paymentEventProducer) {
        this.paymentService = paymentService;
        this.paymentEventProducer = paymentEventProducer;
    }

    @KafkaListener(topics = "webhook.received", groupId = "payment-group")
    public void handleWebhook(Map<String, Object> payload, Acknowledgment acknowledgment) {
        try {
            paymentService.handleWebhookUpdate(payload);
        } catch (Exception ex) {
            String paymentRef = String.valueOf(payload.getOrDefault("paymentRef", "unknown"));
            paymentEventProducer.send("webhook.received.retry", paymentRef, payload);
        } finally {
            acknowledgment.acknowledge();
        }
    }

    @KafkaListener(topics = "webhook.received.retry", groupId = "payment-group")
    public void handleWebhookRetry(Map<String, Object> payload, Acknowledgment acknowledgment) {
        try {
            paymentService.handleWebhookUpdate(payload);
        } catch (Exception ex) {
            String paymentRef = String.valueOf(payload.getOrDefault("paymentRef", "unknown"));
            paymentEventProducer.send("webhook.received.dlt", paymentRef, payload);
        } finally {
            acknowledgment.acknowledge();
        }
    }
}
