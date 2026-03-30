package com.fintech.platform.checkout.service.impl;

import com.fintech.platform.checkout.client.OrderServiceClient;
import com.fintech.platform.checkout.dto.CheckoutSessionResponse;
import com.fintech.platform.checkout.dto.CreateCheckoutSessionRequest;
import com.fintech.platform.checkout.entity.CheckoutSession;
import com.fintech.platform.checkout.entity.CheckoutSessionEvent;
import com.fintech.platform.checkout.entity.CheckoutStatus;
import com.fintech.platform.checkout.exception.ResourceNotFoundException;
import com.fintech.platform.checkout.repository.CheckoutSessionEventRepository;
import com.fintech.platform.checkout.repository.CheckoutSessionRepository;
import com.fintech.platform.checkout.service.CheckoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CheckoutServiceImpl implements CheckoutService {

    private final CheckoutSessionRepository checkoutSessionRepository;
    private final CheckoutSessionEventRepository checkoutSessionEventRepository;
    private final OrderServiceClient orderServiceClient;

    @Override
    public CheckoutSessionResponse createCheckoutSession(CreateCheckoutSessionRequest request) {
        OrderServiceClient.OrderDto order = orderServiceClient.getOrder(request.getOrderId());

        if (order == null) {
            throw new ResourceNotFoundException("Order not found with id: " + request.getOrderId());
        }

        if (!order.merchantId().equals(request.getMerchantId())) {
            throw new IllegalStateException("Order does not belong to provided merchant");
        }

        if ("CANCELLED".equalsIgnoreCase(order.status()) ||
                "EXPIRED".equalsIgnoreCase(order.status()) ||
                "PAID".equalsIgnoreCase(order.status())) {
            throw new IllegalStateException("Checkout cannot be created for order status: " + order.status());
        }

        if (!order.currency().equalsIgnoreCase(request.getCurrency())) {
            throw new IllegalStateException("Checkout currency does not match order currency");
        }

        if (!order.amount().equals(request.getAmount())) {
            throw new IllegalStateException("Checkout amount does not match order amount");
        }

        CheckoutSession session = CheckoutSession.builder()
                .orderId(request.getOrderId())
                .merchantId(request.getMerchantId())
                .currency(request.getCurrency())
                .amount(request.getAmount())
                .sessionToken(UUID.randomUUID().toString())
                .status(CheckoutStatus.ACTIVE)
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .build();

        session = checkoutSessionRepository.save(session);
        saveEvent(session.getId(), "CREATED", "Checkout session created");

        return mapToResponse(session);
    }

    @Override
    public CheckoutSessionResponse getCheckoutSessionById(UUID id) {
        CheckoutSession session = getCheckoutEntity(id);
        return mapToResponse(session);
    }

    @Override
    public List<CheckoutSessionResponse> getAllCheckoutSessions() {
        return checkoutSessionRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public CheckoutSessionResponse expireCheckoutSession(UUID id) {
        CheckoutSession session = getCheckoutEntity(id);

        if (session.getStatus() == CheckoutStatus.COMPLETED) {
            throw new IllegalStateException("Completed checkout session cannot be expired");
        }

        if (session.getStatus() == CheckoutStatus.EXPIRED) {
            return mapToResponse(session);
        }

        session.setStatus(CheckoutStatus.EXPIRED);
        session = checkoutSessionRepository.save(session);

        saveEvent(session.getId(), "EXPIRED", "Checkout session expired");
        return mapToResponse(session);
    }

    private CheckoutSession getCheckoutEntity(UUID id) {
        return checkoutSessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Checkout session not found with id: " + id));
    }

    private void saveEvent(UUID checkoutSessionId, String eventType, String remarks) {
        CheckoutSessionEvent event = CheckoutSessionEvent.builder()
                .checkoutSessionId(checkoutSessionId)
                .eventType(eventType)
                .remarks(remarks)
                .build();

        checkoutSessionEventRepository.save(event);
    }

    private CheckoutSessionResponse mapToResponse(CheckoutSession session) {
        return CheckoutSessionResponse.builder()
                .id(session.getId())
                .orderId(session.getOrderId())
                .merchantId(session.getMerchantId())
                .sessionToken(session.getSessionToken())
                .status(session.getStatus())
                .currency(session.getCurrency())
                .amount(session.getAmount())
                .build();
    }
}