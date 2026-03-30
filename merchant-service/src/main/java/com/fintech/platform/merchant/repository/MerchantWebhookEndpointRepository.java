package com.fintech.platform.merchant.repository;

import com.fintech.platform.merchant.entity.MerchantWebhookEndpoint;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MerchantWebhookEndpointRepository extends JpaRepository<MerchantWebhookEndpoint, UUID> {
    List<MerchantWebhookEndpoint> findByMerchantId(UUID merchantId);
}