package com.company.fintech.merchant.service;

import com.company.fintech.common.exception.BusinessException;
import com.company.fintech.merchant.entity.Merchant;
import com.company.fintech.merchant.entity.MerchantApiCredential;
import com.company.fintech.merchant.repository.MerchantApiCredentialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MerchantAuthService {

    private final MerchantApiCredentialRepository credentialRepository;

    public Merchant authenticate(String apiKey) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new BusinessException("Missing API key");
        }

        String[] parts = apiKey.split("\\.", 2);
        if (parts.length != 2) {
            throw new BusinessException("Invalid API key format");
        }

        String keyPrefix = parts[0];
        String rawSecret = parts[1];

        MerchantApiCredential credential = credentialRepository
                .findByKeyPrefixAndStatus(keyPrefix, "ACTIVE")
                .orElseThrow(() -> new BusinessException("Invalid API key"));

        if (!credential.getSecretHash().equals(rawSecret)) {
            throw new BusinessException("Invalid API key");
        }

        return credential.getMerchant();
    }
}