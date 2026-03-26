package com.fintech.platform.order.dto;

import com.fintech.platform.order.entity.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class OrderResponse {
    private UUID id;
    private UUID merchantId;
    private UUID tenantId;
    private String merchantOrderRef;
    private String currency;
    private Long amount;
    private Long amountPaid;
    private Long amountDue;
    private OrderStatus status;
    private String description;
}