package com.fintech.webhook_service.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fintech.webhook_service.service.WebhookService;

@RestController
@RequestMapping("/webhook")
public class WebhookController {

    private final WebhookService webhookService;

    public WebhookController(WebhookService webhookService) {
        this.webhookService = webhookService;
    }

    @PostMapping("/payment")
    public ResponseEntity<Void> handlePaymentWebhook(
            @RequestBody Map<String, Object> payload) {

        webhookService.processWebhook(payload);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/webhook/update")
    public ResponseEntity<Void> handleWebhook(
            @RequestBody Map<String, Object> payload) {

        webhookService.processWebhook(payload);
        return ResponseEntity.ok().build();
    }
}