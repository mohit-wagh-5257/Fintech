package com.fintech.platform.merchant.repository;

import com.fintech.platform.merchant.entity.MerchantBankAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MerchantBankAccountRepository extends JpaRepository<MerchantBankAccount, UUID> {
    List<MerchantBankAccount> findByMerchantId(UUID merchantId);
}