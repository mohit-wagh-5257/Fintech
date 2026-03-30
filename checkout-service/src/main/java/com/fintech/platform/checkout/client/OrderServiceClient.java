package com.fintech.platform.checkout.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OrderServiceClient {

    private final RestTemplate restTemplate;

    @Value("${external.order-service.base-url}")
    private String orderServiceBaseUrl;

    public OrderDto getOrder(UUID orderId) {
        try {
            String url = orderServiceBaseUrl + "/orders/" + orderId;
            return restTemplate.getForObject(url, OrderDto.class);
        } catch (Exception ex) {
            return null;
        }
    }

    public record OrderDto(
            UUID id,
            UUID merchantId,
            UUID tenantId,
            String merchantOrderRef,
            String currency,
            Long amount,
            Long amountPaid,
            Long amountDue,
            String status,
            String description
    ) {}
}