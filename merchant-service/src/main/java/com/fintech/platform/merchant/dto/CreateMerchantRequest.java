package com.fintech.platform.merchant.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateMerchantRequest {

    @NotNull(message = "Tenant ID is required")
    private UUID tenantId;

    @NotBlank(message = "Merchant code is required")
    private String merchantCode;

    @NotBlank(message = "Legal name is required")
    private String legalName;

    @NotBlank(message = "Display name is required")
    private String displayName;

    @NotBlank(message = "Country is required")
    private String country;

    @NotBlank(message = "Base currency is required")
    private String baseCurrency;
}