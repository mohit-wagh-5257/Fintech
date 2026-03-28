package com.fintech.platform.tenant.dto;

import com.fintech.platform.tenant.entity.TenantStatus;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class TenantResponse {
    private UUID id;
    private String code;
    private String name;
    private TenantStatus status;
}