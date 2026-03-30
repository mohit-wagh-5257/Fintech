package com.fintech.platform.merchant.controller;

import com.fintech.platform.merchant.dto.*;
import com.fintech.platform.merchant.service.MerchantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/merchants")
@RequiredArgsConstructor
public class MerchantController {

    private final MerchantService merchantService;

    @PostMapping
    public MerchantResponse createMerchant(@Valid @RequestBody CreateMerchantRequest request) {
        return merchantService.createMerchant(request);
    }

    @GetMapping("/{id}")
    public MerchantResponse getMerchantById(@PathVariable UUID id) {
        return merchantService.getMerchantById(id);
    }

    @GetMapping
    public List<MerchantResponse> getAllMerchants() {
        return merchantService.getAllMerchants();
    }

    @PostMapping("/{merchantId}/profile")
    public MerchantProfileResponse createOrUpdateMerchantProfile(@PathVariable UUID merchantId,
                                                                 @RequestBody CreateMerchantProfileRequest request) {
        return merchantService.createOrUpdateMerchantProfile(merchantId, request);
    }

    @GetMapping("/{merchantId}/profile")
    public MerchantProfileResponse getMerchantProfile(@PathVariable UUID merchantId) {
        return merchantService.getMerchantProfile(merchantId);
    }

    @PostMapping("/{merchantId}/bank-accounts")
    public MerchantBankAccountResponse addMerchantBankAccount(@PathVariable UUID merchantId,
                                                              @RequestBody CreateMerchantBankAccountRequest request) {
        return merchantService.addMerchantBankAccount(merchantId, request);
    }

    @GetMapping("/{merchantId}/bank-accounts")
    public List<MerchantBankAccountResponse> getMerchantBankAccounts(@PathVariable UUID merchantId) {
        return merchantService.getMerchantBankAccounts(merchantId);
    }

    @PostMapping("/{merchantId}/webhook-endpoints")
    public MerchantWebhookEndpointResponse addMerchantWebhookEndpoint(@PathVariable UUID merchantId,
                                                                      @RequestBody CreateMerchantWebhookEndpointRequest request) {
        return merchantService.addMerchantWebhookEndpoint(merchantId, request);
    }

    @GetMapping("/{merchantId}/webhook-endpoints")
    public List<MerchantWebhookEndpointResponse> getMerchantWebhookEndpoints(@PathVariable UUID merchantId) {
        return merchantService.getMerchantWebhookEndpoints(merchantId);
    }
}