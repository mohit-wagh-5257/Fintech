package com.fintech.connector_service.dto.response;

import lombok.Data;

@Data
public class ConnectorResponse {

    private String status;
    private String connectorPaymentRef;
    private String errorCode;
    private String errorMessage;

}
