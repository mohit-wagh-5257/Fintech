package com.fintech.connector_service.controller;

import java.util.UUID;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fintech.connector_service.dto.request.ConnectorRequest;
import com.fintech.connector_service.dto.response.ConnectorResponse;

@RestController
@RequestMapping("/connector")
public class ConnectorController {

    @PostMapping("/pay")
    public ConnectorResponse processPayment(@RequestBody ConnectorRequest request) {

        ConnectorResponse response = new ConnectorResponse();
        response.setStatus(request.getAmount() % 2 == 0 ? "SUCCESS" : "FAILED");
        response.setConnectorPaymentRef("conn_" + UUID.randomUUID());

        return response;
    }
}