package com.fintech.platform.tenant.service;

import com.fintech.platform.tenant.dto.CreateTenantRequest;
import com.fintech.platform.tenant.dto.TenantResponse;

import java.util.List;
import java.util.UUID;

public interface TenantService {
    TenantResponse createTenant(CreateTenantRequest request);
    TenantResponse getTenantById(UUID id);
    List<TenantResponse> getAllTenants();
}