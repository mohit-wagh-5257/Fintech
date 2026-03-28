package com.fintech.platform.merchant.service;

import com.fintech.platform.merchant.dto.CreateMerchantRequest;
import com.fintech.platform.merchant.dto.MerchantResponse;

import java.util.List;
import java.util.UUID;

public interface MerchantService {
    MerchantResponse createMerchant(CreateMerchantRequest request);
    MerchantResponse getMerchantById(UUID id);
    List<MerchantResponse> getAllMerchants();
}