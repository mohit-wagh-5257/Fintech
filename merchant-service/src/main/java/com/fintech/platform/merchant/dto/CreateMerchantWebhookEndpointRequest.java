package com.fintech.platform.merchant.dto;

import lombok.Data;

@Data
public class CreateMerchantWebhookEndpointRequest {
    private String url;
    private String secret;
}