package com.fintech.payment_service.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fintech.payment_service.dto.request.CreatePaymentRequest;
import com.fintech.payment_service.dto.response.PaymentResponse;
import com.fintech.payment_service.entity.IdempotencyKey;
import com.fintech.payment_service.entity.OutboxEvent;
import com.fintech.payment_service.entity.Payment;
import com.fintech.payment_service.entity.PaymentAttempt;
import com.fintech.payment_service.entity.PaymentStatusHistory;
import com.fintech.payment_service.enums.PaymentAttemptStatus;
import com.fintech.payment_service.enums.PaymentStatus;
import com.fintech.payment_service.repository.IdempotencyRepository;
import com.fintech.payment_service.repository.OutboxEventRepository;
import com.fintech.payment_service.repository.PaymentAttemptRepository;
import com.fintech.payment_service.repository.PaymentRepository;
import com.fintech.payment_service.repository.PaymentStatusHistoryRepository;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentStatusHistoryRepository historyRepository;
    private final PaymentAttemptRepository attemptRepository;
    private final IdempotencyRepository idempotencyRepository;
    private final OutboxEventRepository outboxRepository;
    private final PaymentEventProducer paymentEventProducer;
    private final ObjectMapper objectMapper;

    public PaymentService(PaymentRepository paymentRepository,
                          PaymentStatusHistoryRepository historyRepository,
                          PaymentAttemptRepository attemptRepository,
                          IdempotencyRepository idempotencyRepository,
                          OutboxEventRepository outboxRepository,
                          PaymentEventProducer paymentEventProducer,
                          ObjectMapper objectMapper) {
        this.paymentRepository = paymentRepository;
        this.historyRepository = historyRepository;
        this.attemptRepository = attemptRepository;
        this.idempotencyRepository = idempotencyRepository;
        this.outboxRepository = outboxRepository;
        this.paymentEventProducer = paymentEventProducer;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public PaymentResponse createPayment(CreatePaymentRequest request, String idempotencyKey) {

        String safeIdempotencyKey = (idempotencyKey == null || idempotencyKey.isBlank())
                ? "auto-" + UUID.randomUUID()
                : idempotencyKey;

        Optional<IdempotencyKey> existing = idempotencyRepository
                .findByIdempotencyKey(safeIdempotencyKey);

        if (existing.isPresent()) {
            Payment payment = paymentRepository
                    .findById(existing.get().getPaymentId())
                    .orElse(null);
            if (payment != null) {
                return mapToResponse(payment);
            }
        }

        Payment payment = createNewPayment(request);

        IdempotencyKey key = new IdempotencyKey();
        key.setMerchantId(request.getMerchantId());
        key.setIdempotencyKey(safeIdempotencyKey);
        key.setRequestHash(buildRequestHash(request));
        key.setPaymentId(payment.getId());
        key.setCreatedAt(LocalDateTime.now());

        idempotencyRepository.save(key);

        return mapToResponse(payment);
    }

    private void saveStatusHistory(UUID paymentId,
                                  PaymentStatus oldStatus,
                                  PaymentStatus newStatus,
                                  String source) {

        PaymentStatusHistory history = new PaymentStatusHistory();

        history.setPaymentId(paymentId);
        history.setOldStatus(oldStatus);
        history.setNewStatus(newStatus);
        history.setSource(source);
        history.setChangedAt(LocalDateTime.now());

        historyRepository.save(history);
    }

    private String buildRequestHash(CreatePaymentRequest request) {
        return String.format("%s|%s|%s|%s|%s",
                request.getOrderId(),
                request.getMerchantId(),
                request.getAmount(),
                request.getCurrency(),
                request.getPaymentMethodType());
    }

    private String generatePaymentRef() {
        return "pay_" + UUID.randomUUID().toString().substring(0, 10);
    }

    private Payment createNewPayment(CreatePaymentRequest request) {
        Payment payment = new Payment();
        payment.setPaymentRef(generatePaymentRef());
        payment.setOrderId(request.getOrderId());
        payment.setMerchantId(request.getMerchantId());
        payment.setTenantId(request.getTenantId());
        payment.setAmount(request.getAmount());
        payment.setAuthorizedAmount(0L);
        payment.setCapturedAmount(0L);
        payment.setRefundedAmount(0L);
        payment.setCurrency(request.getCurrency());
        payment.setStatus(PaymentStatus.CREATED);
        payment.setPaymentMethodType(request.getPaymentMethodType());
        payment.setCaptureMode(request.getCaptureMode());
        payment.setCreatedAt(LocalDateTime.now());
        payment.setUpdatedAt(LocalDateTime.now());

        Payment saved = paymentRepository.save(payment);
        saveStatusHistory(saved.getId(), null, PaymentStatus.CREATED, "API");
        return saved;
    }

    private PaymentResponse mapToResponse(Payment payment) {
        return PaymentResponse.builder()
                .paymentId(payment.getId())
                .paymentRef(payment.getPaymentRef())
                .status(payment.getStatus())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .createdAt(payment.getCreatedAt())
                .build();
    }

    private void saveOutboxEvent(Payment payment, String eventType) {
        try {
            OutboxEvent event = new OutboxEvent();
            event.setAggregateType("PAYMENT");
            event.setAggregateId(payment.getId());
            event.setEventType(eventType);
            event.setPayload(objectMapper.writeValueAsString(payment));
            event.setPublished(false);
            event.setCreatedAt(LocalDateTime.now());

            outboxRepository.save(event);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save outbox event: " + e.getMessage(), e);
        }
    }

    @Transactional
    public PaymentResponse processPayment(UUID paymentId) {

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        if (payment.getStatus() == PaymentStatus.CAPTURED || payment.getStatus() == PaymentStatus.FAILED) {
            return mapToResponse(payment);
        }

        List<PaymentAttempt> attempts = attemptRepository.findByPaymentId(paymentId);

        PaymentAttempt attempt = new PaymentAttempt();
        attempt.setPaymentId(paymentId);
        attempt.setAttemptNo(attempts.size() + 1);
        attempt.setConnectorName("KAFKA_CONNECTOR");
        attempt.setRequestedAmount(payment.getAmount());
        attempt.setStatus(PaymentAttemptStatus.INITIATED);
        attempt.setStartedAt(LocalDateTime.now());

        attemptRepository.save(attempt);
        saveOutboxEvent(payment, "PAYMENT_INITIATED");

        return mapToResponse(payment);
    }

    private void updatePaymentStatus(Payment payment, PaymentStatus newStatus, String source) {
        PaymentStatus oldStatus = payment.getStatus();

        if (oldStatus == newStatus) {
            return;
        }

        payment.setStatus(newStatus);
        payment.setUpdatedAt(LocalDateTime.now());

        Payment updated = paymentRepository.save(payment);

        saveStatusHistory(payment.getId(), oldStatus, newStatus, source);

        if (newStatus == PaymentStatus.CAPTURED || newStatus == PaymentStatus.FAILED) {
            saveOutboxEvent(updated, "PAYMENT_UPDATED");
        }
    }

    public void handleWebhookUpdate(Map<String, Object> payload) {

        String paymentRef = (String) payload.get("paymentRef");
        String status = (String) payload.get("status");

        Payment payment = paymentRepository.findByPaymentRef(paymentRef)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        if (payment.getStatus() == PaymentStatus.CAPTURED && "SUCCESS".equals(status)) {
            return;
        }

        if (payment.getStatus() == PaymentStatus.FAILED && "FAILED".equals(status)) {
            return;
        }

        if ("SUCCESS".equals(status)) {
            updatePaymentStatus(payment, PaymentStatus.CAPTURED, "KAFKA");
        } else {
            updatePaymentStatus(payment, PaymentStatus.FAILED, "KAFKA");
        }
    }
}