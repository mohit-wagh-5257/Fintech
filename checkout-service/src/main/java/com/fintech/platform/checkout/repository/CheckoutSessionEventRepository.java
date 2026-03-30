package com.fintech.platform.checkout.repository;

import com.fintech.platform.checkout.entity.CheckoutSessionEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CheckoutSessionEventRepository extends JpaRepository<CheckoutSessionEvent, UUID> {
}