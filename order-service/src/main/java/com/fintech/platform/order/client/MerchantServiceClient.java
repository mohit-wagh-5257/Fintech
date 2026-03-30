package com.fintech.platform.order.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MerchantServiceClient {

    private final RestTemplate restTemplate;

    @Value("${external.merchant-service.base-url}")
    private String merchantServiceBaseUrl;

    public MerchantDto getMerchant(UUID merchantId) {
        try {
            String url = merchantServiceBaseUrl + "/merchants/" + merchantId;
            return restTemplate.getForObject(url, MerchantDto.class);
        } catch (Exception ex) {
            return null;
        }
    }

    public record MerchantDto(
            UUID id,
            UUID tenantId,
            String merchantCode,
            String legalName,
            String displayName,
            String status,
            String country,
            String baseCurrency
    ) {}
}