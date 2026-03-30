package com.fintech.platform.merchant.dto;

import lombok.Data;

@Data
public class CreateMerchantProfileRequest {
    private String email;
    private String phone;
    private String website;
    private String address;
}