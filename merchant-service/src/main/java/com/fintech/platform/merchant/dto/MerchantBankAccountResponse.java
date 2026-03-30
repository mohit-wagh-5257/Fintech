package com.fintech.platform.merchant.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class MerchantBankAccountResponse {
    private UUID id;
    private UUID merchantId;
    private String accountHolderName;
    private String bankName;
    private String accountNumber;
    private String ifscSwift;
    private String currency;
    private Boolean isPrimary;
    private String status;
}