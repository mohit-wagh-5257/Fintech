package com.fintech.platform.tenant.controller;

import com.fintech.platform.tenant.dto.CreateTenantRequest;
import com.fintech.platform.tenant.dto.TenantResponse;
import com.fintech.platform.tenant.service.TenantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tenants")
@RequiredArgsConstructor
public class TenantController {

    private final TenantService tenantService;

    @PostMapping
    public TenantResponse createTenant(@Valid @RequestBody CreateTenantRequest request) {
        return tenantService.createTenant(request);
    }

    @GetMapping("/{id}")
    public TenantResponse getTenantById(@PathVariable UUID id) {
        return tenantService.getTenantById(id);
    }

    @GetMapping
    public List<TenantResponse> getAllTenants() {
        return tenantService.getAllTenants();
    }
}