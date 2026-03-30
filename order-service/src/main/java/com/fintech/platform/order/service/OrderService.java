package com.fintech.platform.order.service;

import com.fintech.platform.order.dto.CreateOrderRequest;
import com.fintech.platform.order.dto.OrderResponse;
import com.fintech.platform.order.dto.UpdateOrderRequest;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    OrderResponse createOrder(CreateOrderRequest request);
    OrderResponse getOrderById(UUID id);
    List<OrderResponse> getAllOrders();
    OrderResponse updateOrder(UUID id, UpdateOrderRequest request);
    OrderResponse cancelOrder(UUID id);
    OrderResponse expireOrder(UUID id);
}