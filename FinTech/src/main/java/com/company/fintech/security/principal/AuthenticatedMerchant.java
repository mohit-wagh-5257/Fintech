package com.company.fintech.security.principal;

import com.company.fintech.merchant.entity.Merchant;
import lombok.Getter;

import java.util.UUID;

@Getter
public class AuthenticatedMerchant {

    private final UUID merchantId;
    private final UUID clientId;
    private final String merchantCode;

    public AuthenticatedMerchant(Merchant merchant) {
        this.merchantId = merchant.getId();
        this.clientId = merchant.getClient().getId();
        this.merchantCode = merchant.getMerchantCode();
    }
}