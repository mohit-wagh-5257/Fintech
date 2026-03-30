package com.fintech.platform.merchant.service;

import com.fintech.platform.merchant.dto.*;

import java.util.List;
import java.util.UUID;

public interface MerchantService {
    MerchantResponse createMerchant(CreateMerchantRequest request);
    MerchantResponse getMerchantById(UUID id);
    List<MerchantResponse> getAllMerchants();

    MerchantProfileResponse createOrUpdateMerchantProfile(UUID merchantId, CreateMerchantProfileRequest request);
    MerchantProfileResponse getMerchantProfile(UUID merchantId);

    MerchantBankAccountResponse addMerchantBankAccount(UUID merchantId, CreateMerchantBankAccountRequest request);
    List<MerchantBankAccountResponse> getMerchantBankAccounts(UUID merchantId);

    MerchantWebhookEndpointResponse addMerchantWebhookEndpoint(UUID merchantId, CreateMerchantWebhookEndpointRequest request);
    List<MerchantWebhookEndpointResponse> getMerchantWebhookEndpoints(UUID merchantId);
}