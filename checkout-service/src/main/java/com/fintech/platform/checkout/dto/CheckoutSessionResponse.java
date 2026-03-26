package com.fintech.platform.checkout.dto;

import com.fintech.platform.checkout.entity.CheckoutStatus;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class CheckoutSessionResponse {
    private UUID id;
    private UUID orderId;
    private UUID merchantId;
    private String sessionToken;
    private CheckoutStatus status;
    private String currency;
    private Long amount;
}