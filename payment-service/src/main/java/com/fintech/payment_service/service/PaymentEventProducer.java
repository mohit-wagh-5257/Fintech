package com.fintech.payment_service.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.fintech.payment_service.entity.Payment;

@Service
public class PaymentEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public PaymentEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendPaymentInitiated(Payment payment) {
        kafkaTemplate.send("payment.initiated", payment.getPaymentRef(), payment);
    }

    public void sendPaymentUpdated(Payment payment) {
        kafkaTemplate.send("payment.updated", payment.getPaymentRef(), payment);
    }

    public void send(String topic, String key, Object payload) {
        kafkaTemplate.send(topic, key, payload);
    }
}