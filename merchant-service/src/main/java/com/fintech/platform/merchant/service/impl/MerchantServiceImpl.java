package com.fintech.platform.merchant.service.impl;

import com.fintech.platform.merchant.dto.CreateMerchantRequest;
import com.fintech.platform.merchant.dto.MerchantResponse;
import com.fintech.platform.merchant.entity.Merchant;
import com.fintech.platform.merchant.entity.MerchantStatus;
import com.fintech.platform.merchant.exception.ResourceNotFoundException;
import com.fintech.platform.merchant.repository.MerchantRepository;
import com.fintech.platform.merchant.service.MerchantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MerchantServiceImpl implements MerchantService {

    private final MerchantRepository merchantRepository;

    @Override
    public MerchantResponse createMerchant(CreateMerchantRequest request) {
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
        return mapToResponse(merchant);
    }

    @Override
    public MerchantResponse getMerchantById(UUID id) {
        Merchant merchant = merchantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Merchant not found with id: " + id));
        return mapToResponse(merchant);
    }

    @Override
    public List<MerchantResponse> getAllMerchants() {
        return merchantRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    private MerchantResponse mapToResponse(Merchant merchant) {
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
}