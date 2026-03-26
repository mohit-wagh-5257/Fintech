package com.fintech.platform.checkout.service.impl;

import com.fintech.platform.checkout.dto.CheckoutSessionResponse;
import com.fintech.platform.checkout.dto.CreateCheckoutSessionRequest;
import com.fintech.platform.checkout.entity.CheckoutSession;
import com.fintech.platform.checkout.entity.CheckoutStatus;
import com.fintech.platform.checkout.exception.ResourceNotFoundException;
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

    @Override
    public CheckoutSessionResponse createCheckoutSession(CreateCheckoutSessionRequest request) {
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
        return mapToResponse(session);
    }

    @Override
    public CheckoutSessionResponse getCheckoutSessionById(UUID id) {
        CheckoutSession session = checkoutSessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Checkout session not found with id: " + id));
        return mapToResponse(session);
    }

    @Override
    public List<CheckoutSessionResponse> getAllCheckoutSessions() {
        return checkoutSessionRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
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