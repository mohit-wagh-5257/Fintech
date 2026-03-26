package com.fintech.platform.checkout.service;

import com.fintech.platform.checkout.dto.CheckoutSessionResponse;
import com.fintech.platform.checkout.dto.CreateCheckoutSessionRequest;

import java.util.List;
import java.util.UUID;

public interface CheckoutService {
    CheckoutSessionResponse createCheckoutSession(CreateCheckoutSessionRequest request);
    CheckoutSessionResponse getCheckoutSessionById(UUID id);
    List<CheckoutSessionResponse> getAllCheckoutSessions();
}