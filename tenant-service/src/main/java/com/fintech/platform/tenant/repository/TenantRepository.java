package com.fintech.platform.tenant.repository;

import com.fintech.platform.tenant.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TenantRepository extends JpaRepository<Tenant, UUID> {
}