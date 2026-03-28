package com.fintech.platform.checkout.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateCheckoutSessionRequest {

    @NotNull(message = "Order ID is required")
    private UUID orderId;

    @NotNull(message = "Merchant ID is required")
    private UUID merchantId;

    @NotBlank(message = "Currency is required")
    private String currency;

    @NotNull(message = "Amount is required")
    private Long amount;
}