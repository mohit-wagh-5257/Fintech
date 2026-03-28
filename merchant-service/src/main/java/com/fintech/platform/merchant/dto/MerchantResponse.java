package com.fintech.platform.merchant.dto;

import com.fintech.platform.merchant.entity.MerchantStatus;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class MerchantResponse {
    private UUID id;
    private UUID tenantId;
    private String merchantCode;
    private String legalName;
    private String displayName;
    private MerchantStatus status;
    private String country;
    private String baseCurrency;
}