package com.fintech.platform.merchant.dto;

import lombok.Data;

@Data
public class CreateMerchantBankAccountRequest {
    private String accountHolderName;
    private String bankName;
    private String accountNumber;
    private String ifscSwift;
    private String currency;
    private Boolean isPrimary;
}