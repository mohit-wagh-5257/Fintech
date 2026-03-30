package com.fintech.platform.merchant.repository;

import com.fintech.platform.merchant.entity.MerchantProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MerchantProfileRepository extends JpaRepository<MerchantProfile, UUID> {
    Optional<MerchantProfile> findByMerchantId(UUID merchantId);
}