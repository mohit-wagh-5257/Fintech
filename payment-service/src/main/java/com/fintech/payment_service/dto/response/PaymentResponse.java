package com.fintech.payment_service.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;
import com.fintech.payment_service.enums.PaymentStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentResponse {

    private UUID paymentId;
    private String paymentRef;

    private PaymentStatus status;

    private Long amount;
    private String currency;

    private LocalDateTime createdAt;
}