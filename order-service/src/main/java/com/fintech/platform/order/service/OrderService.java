package com.fintech.platform.order.service;

import com.fintech.platform.order.dto.CreateOrderRequest;
import com.fintech.platform.order.dto.OrderResponse;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    OrderResponse createOrder(CreateOrderRequest request);
    OrderResponse getOrderById(UUID id);
    List<OrderResponse> getAllOrders();
}