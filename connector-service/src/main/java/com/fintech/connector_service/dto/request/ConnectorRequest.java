package com.fintech.connector_service.dto.request;

import lombok.Data;

@Data
public class ConnectorRequest {

    private String paymentRef;
    private Long amount;
    private String currency;
    private String paymentMethod;

}
