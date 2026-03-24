package com.company.fintech.merchant.repository;

import com.company.fintech.merchant.entity.MerchantApiCredential;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MerchantApiCredentialRepository extends JpaRepository<MerchantApiCredential, UUID> {
    Optional<MerchantApiCredential> findByKeyPrefixAndStatus(String keyPrefix, String status);
}