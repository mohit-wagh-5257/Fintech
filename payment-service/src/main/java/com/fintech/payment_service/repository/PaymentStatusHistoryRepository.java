package com.fintech.payment_service.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import com.fintech.payment_service.entity.PaymentStatusHistory;

public interface PaymentStatusHistoryRepository extends JpaRepository<PaymentStatusHistory, UUID> {
}
