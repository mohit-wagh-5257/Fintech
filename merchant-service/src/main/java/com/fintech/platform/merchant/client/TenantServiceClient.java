package com.fintech.platform.merchant.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TenantServiceClient {

    private final RestTemplate restTemplate;

    @Value("${external.tenant-service.base-url}")
    private String tenantServiceBaseUrl;

    public boolean tenantExists(UUID tenantId) {
        try {
            String url = tenantServiceBaseUrl + "/tenants/" + tenantId;
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception ex) {
            return false;
        }
    }
}