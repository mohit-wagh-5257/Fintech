package com.fintech.platform.merchant.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class MerchantProfileResponse {
    private UUID id;
    private UUID merchantId;
    private String email;
    private String phone;
    private String website;
    private String address;
}