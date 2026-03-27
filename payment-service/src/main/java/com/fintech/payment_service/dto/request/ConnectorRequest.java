package com.fintech.payment_service.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ConnectorRequest {

    private String paymentRef;
    private Long amount;
    private String currency;
    private String paymentMethod;
}