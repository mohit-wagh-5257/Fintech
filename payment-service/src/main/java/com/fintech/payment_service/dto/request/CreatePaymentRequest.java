package com.fintech.payment_service.dto.request;

import java.util.UUID;
import com.fintech.payment_service.enums.CaptureMode;
import com.fintech.payment_service.enums.PaymentMethodType;
import lombok.Data;

@Data
public class CreatePaymentRequest {

    private UUID orderId;
    private UUID merchantId;
    private UUID tenantId;

    private Long amount;
    private String currency;

    private PaymentMethodType paymentMethodType;
    private CaptureMode captureMode;
}