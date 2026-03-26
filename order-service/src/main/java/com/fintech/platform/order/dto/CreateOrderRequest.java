package com.fintech.platform.order.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateOrderRequest {

    @NotNull(message = "Merchant ID is required")
    private UUID merchantId;

    @NotNull(message = "Tenant ID is required")
    private UUID tenantId;

    @NotBlank(message = "Merchant order reference is required")
    private String merchantOrderRef;

    @NotBlank(message = "Currency is required")
    private String currency;

    @NotNull(message = "Amount is required")
    private Long amount;

    private String description;
}