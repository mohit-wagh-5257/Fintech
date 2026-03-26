package com.fintech.platform.tenant.service.impl;

import com.fintech.platform.tenant.dto.CreateTenantRequest;
import com.fintech.platform.tenant.dto.TenantResponse;
import com.fintech.platform.tenant.entity.Tenant;
import com.fintech.platform.tenant.entity.TenantStatus;
import com.fintech.platform.tenant.exception.ResourceNotFoundException;
import com.fintech.platform.tenant.repository.TenantRepository;
import com.fintech.platform.tenant.service.TenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TenantServiceImpl implements TenantService {

    private final TenantRepository tenantRepository;

    @Override
    public TenantResponse createTenant(CreateTenantRequest request) {
        Tenant tenant = Tenant.builder()
                .code(request.getCode())
                .name(request.getName())
                .status(TenantStatus.ACTIVE)
                .build();

        tenant = tenantRepository.save(tenant);
        return mapToResponse(tenant);
    }

    @Override
    public TenantResponse getTenantById(UUID id) {
        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found with id: " + id));
        return mapToResponse(tenant);
    }

    @Override
    public List<TenantResponse> getAllTenants() {
        return tenantRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    private TenantResponse mapToResponse(Tenant tenant) {
        return TenantResponse.builder()
                .id(tenant.getId())
                .code(tenant.getCode())
                .name(tenant.getName())
                .status(tenant.getStatus())
                .build();
    }
}