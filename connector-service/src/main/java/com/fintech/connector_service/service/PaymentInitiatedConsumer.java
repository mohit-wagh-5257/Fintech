package com.fintech.connector_service.service;

import java.util.Map;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
public class PaymentInitiatedConsumer {

    private final ConnectorEventProducer connectorEventProducer;

    public PaymentInitiatedConsumer(ConnectorEventProducer connectorEventProducer) {
        this.connectorEventProducer = connectorEventProducer;
    }

    @KafkaListener(topics = "payment.initiated", groupId = "connector-group")
    public void handlePayment(Map<String, Object> payment, Acknowledgment acknowledgment) {
        try {
            String paymentRef = String.valueOf(payment.get("paymentRef"));
            Number amount = (Number) payment.get("amount");

            String status = (amount.longValue() % 2 == 0) ? "SUCCESS" : "FAILED";
            connectorEventProducer.sendConnectorResult(paymentRef, status);
        } catch (Exception ex) {
            String paymentRef = String.valueOf(payment.getOrDefault("paymentRef", "unknown"));
            connectorEventProducer.send("payment.initiated.retry", paymentRef, payment);
        } finally {
            acknowledgment.acknowledge();
        }
    }

    @KafkaListener(topics = "payment.initiated.retry", groupId = "connector-group")
    public void handlePaymentRetry(Map<String, Object> payment, Acknowledgment acknowledgment) {
        try {
            String paymentRef = String.valueOf(payment.get("paymentRef"));
            Number amount = (Number) payment.get("amount");

            String status = (amount.longValue() % 2 == 0) ? "SUCCESS" : "FAILED";
            connectorEventProducer.sendConnectorResult(paymentRef, status);
        } catch (Exception ex) {
            String paymentRef = String.valueOf(payment.getOrDefault("paymentRef", "unknown"));
            connectorEventProducer.send("payment.initiated.dlt", paymentRef, payment);
        } finally {
            acknowledgment.acknowledge();
        }
    }
}
