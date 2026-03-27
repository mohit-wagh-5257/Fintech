package com.fintech.webhook_service.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fintech.webhook_service.entity.WebhookEvent;

public interface WebhookEventRepository extends JpaRepository<WebhookEvent, UUID> {

	boolean existsByEventKey(String eventKey);
}
