package com.fintech.platform.merchant.service.impl;

import com.fintech.platform.merchant.client.TenantServiceClient;
import com.fintech.platform.merchant.dto.*;
import com.fintech.platform.merchant.entity.*;
import com.fintech.platform.merchant.exception.ResourceNotFoundException;
import com.fintech.platform.merchant.repository.*;
import com.fintech.platform.merchant.service.MerchantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MerchantServiceImpl implements MerchantService {

    private final MerchantRepository merchantRepository;
    private final MerchantProfileRepository merchantProfileRepository;
    private final MerchantBankAccountRepository merchantBankAccountRepository;
    private final MerchantWebhookEndpointRepository merchantWebhookEndpointRepository;
    private final TenantServiceClient tenantServiceClient;

    @Override
    public MerchantResponse createMerchant(CreateMerchantRequest request) {
        if (!tenantServiceClient.tenantExists(request.getTenantId())) {
            throw new ResourceNotFoundException("Tenant not found with id: " + request.getTenantId());
        }

        Merchant merchant = Merchant.builder()
                .tenantId(request.getTenantId())
                .merchantCode(request.getMerchantCode())
                .legalName(request.getLegalName())
                .displayName(request.getDisplayName())
                .country(request.getCountry())
                .baseCurrency(request.getBaseCurrency())
                .status(MerchantStatus.ACTIVE)
                .build();

        merchant = merchantRepository.save(merchant);
        return mapMerchantResponse(merchant);
    }

    @Override
    public MerchantResponse getMerchantById(UUID id) {
        Merchant merchant = getMerchantEntity(id);
        return mapMerchantResponse(merchant);
    }

    @Override
    public List<MerchantResponse> getAllMerchants() {
        return merchantRepository.findAll().stream()
                .map(this::mapMerchantResponse)
                .toList();
    }

    @Override
    public MerchantProfileResponse createOrUpdateMerchantProfile(UUID merchantId, CreateMerchantProfileRequest request) {
        getMerchantEntity(merchantId);

        MerchantProfile profile = merchantProfileRepository.findByMerchantId(merchantId)
                .orElse(MerchantProfile.builder().merchantId(merchantId).build());

        profile.setEmail(request.getEmail());
        profile.setPhone(request.getPhone());
        profile.setWebsite(request.getWebsite());
        profile.setAddress(request.getAddress());

        profile = merchantProfileRepository.save(profile);
        return mapMerchantProfileResponse(profile);
    }

    @Override
    public MerchantProfileResponse getMerchantProfile(UUID merchantId) {
        getMerchantEntity(merchantId);

        MerchantProfile profile = merchantProfileRepository.findByMerchantId(merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("Merchant profile not found for merchantId: " + merchantId));

        return mapMerchantProfileResponse(profile);
    }

    @Override
    public MerchantBankAccountResponse addMerchantBankAccount(UUID merchantId, CreateMerchantBankAccountRequest request) {
        getMerchantEntity(merchantId);

        MerchantBankAccount bankAccount = MerchantBankAccount.builder()
                .merchantId(merchantId)
                .accountHolderName(request.getAccountHolderName())
                .bankName(request.getBankName())
                .accountNumber(request.getAccountNumber())
                .ifscSwift(request.getIfscSwift())
                .currency(request.getCurrency())
                .isPrimary(Boolean.TRUE.equals(request.getIsPrimary()))
                .status("ACTIVE")
                .build();

        bankAccount = merchantBankAccountRepository.save(bankAccount);
        return mapMerchantBankAccountResponse(bankAccount);
    }

    @Override
    public List<MerchantBankAccountResponse> getMerchantBankAccounts(UUID merchantId) {
        getMerchantEntity(merchantId);

        return merchantBankAccountRepository.findByMerchantId(merchantId).stream()
                .map(this::mapMerchantBankAccountResponse)
                .toList();
    }

    @Override
    public MerchantWebhookEndpointResponse addMerchantWebhookEndpoint(UUID merchantId, CreateMerchantWebhookEndpointRequest request) {
        getMerchantEntity(merchantId);

        MerchantWebhookEndpoint endpoint = MerchantWebhookEndpoint.builder()
                .merchantId(merchantId)
                .url(request.getUrl())
                .secret(request.getSecret())
                .status("ACTIVE")
                .build();

        endpoint = merchantWebhookEndpointRepository.save(endpoint);
        return mapMerchantWebhookEndpointResponse(endpoint);
    }

    @Override
    public List<MerchantWebhookEndpointResponse> getMerchantWebhookEndpoints(UUID merchantId) {
        getMerchantEntity(merchantId);

        return merchantWebhookEndpointRepository.findByMerchantId(merchantId).stream()
                .map(this::mapMerchantWebhookEndpointResponse)
                .toList();
    }

    private Merchant getMerchantEntity(UUID id) {
        return merchantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Merchant not found with id: " + id));
    }

    private MerchantResponse mapMerchantResponse(Merchant merchant) {
        return MerchantResponse.builder()
                .id(merchant.getId())
                .tenantId(merchant.getTenantId())
                .merchantCode(merchant.getMerchantCode())
                .legalName(merchant.getLegalName())
                .displayName(merchant.getDisplayName())
                .status(merchant.getStatus())
                .country(merchant.getCountry())
                .baseCurrency(merchant.getBaseCurrency())
                .build();
    }

    private MerchantProfileResponse mapMerchantProfileResponse(MerchantProfile profile) {
        return MerchantProfileResponse.builder()
                .id(profile.getId())
                .merchantId(profile.getMerchantId())
                .email(profile.getEmail())
                .phone(profile.getPhone())
                .website(profile.getWebsite())
                .address(profile.getAddress())
                .build();
    }

    private MerchantBankAccountResponse mapMerchantBankAccountResponse(MerchantBankAccount account) {
        return MerchantBankAccountResponse.builder()
                .id(account.getId())
                .merchantId(account.getMerchantId())
                .accountHolderName(account.getAccountHolderName())
                .bankName(account.getBankName())
                .accountNumber(account.getAccountNumber())
                .ifscSwift(account.getIfscSwift())
                .currency(account.getCurrency())
                .isPrimary(account.getIsPrimary())
                .status(account.getStatus())
                .build();
    }

    private MerchantWebhookEndpointResponse mapMerchantWebhookEndpointResponse(MerchantWebhookEndpoint endpoint) {
        return MerchantWebhookEndpointResponse.builder()
                .id(endpoint.getId())
                .merchantId(endpoint.getMerchantId())
                .url(endpoint.getUrl())
                .secret(endpoint.getSecret())
                .status(endpoint.getStatus())
                .build();
    }
}