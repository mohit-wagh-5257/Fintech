package com.fintech.platform.checkout.repository;

import com.fintech.platform.checkout.entity.CheckoutSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CheckoutSessionRepository extends JpaRepository<CheckoutSession, UUID> {
}