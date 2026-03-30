package com.fintech.platform.checkout.controller;

import com.fintech.platform.checkout.dto.CheckoutSessionResponse;
import com.fintech.platform.checkout.dto.CreateCheckoutSessionRequest;
import com.fintech.platform.checkout.service.CheckoutService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/checkout-sessions")
@RequiredArgsConstructor
public class CheckoutController {

    private final CheckoutService checkoutService;

    @PostMapping
    public CheckoutSessionResponse createCheckoutSession(@Valid @RequestBody CreateCheckoutSessionRequest request) {
        return checkoutService.createCheckoutSession(request);
    }

    @GetMapping("/{id}")
    public CheckoutSessionResponse getCheckoutSessionById(@PathVariable UUID id) {
        return checkoutService.getCheckoutSessionById(id);
    }

    @GetMapping
    public List<CheckoutSessionResponse> getAllCheckoutSessions() {
        return checkoutService.getAllCheckoutSessions();
    }

    @PutMapping("/{id}/expire")
    public CheckoutSessionResponse expireCheckoutSession(@PathVariable UUID id) {
        return checkoutService.expireCheckoutSession(id);
    }
}