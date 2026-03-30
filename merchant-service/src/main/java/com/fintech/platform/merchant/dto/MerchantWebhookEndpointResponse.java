package com.fintech.platform.merchant.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class MerchantWebhookEndpointResponse {
    private UUID id;
    private UUID merchantId;
    private String url;
    private String secret;
    private String status;
}