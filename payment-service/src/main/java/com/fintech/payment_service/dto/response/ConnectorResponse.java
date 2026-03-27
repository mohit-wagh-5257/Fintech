package com.fintech.payment_service.dto.response;

import lombok.Data;

@Data
public class ConnectorResponse {

    private String status; // SUCCESS / FAILED
    private String connectorPaymentRef;
    private String errorCode;
    private String errorMessage;
}