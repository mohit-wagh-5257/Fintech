package com.fintech.platform.order.service.impl;

import com.fintech.platform.order.dto.CreateOrderRequest;
import com.fintech.platform.order.dto.OrderResponse;
import com.fintech.platform.order.entity.Order;
import com.fintech.platform.order.entity.OrderStatus;
import com.fintech.platform.order.exception.ResourceNotFoundException;
import com.fintech.platform.order.repository.OrderRepository;
import com.fintech.platform.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    @Override
    public OrderResponse createOrder(CreateOrderRequest request) {
        Order order = Order.builder()
                .merchantId(request.getMerchantId())
                .tenantId(request.getTenantId())
                .merchantOrderRef(request.getMerchantOrderRef())
                .currency(request.getCurrency())
                .amount(request.getAmount())
                .amountPaid(0L)
                .amountDue(request.getAmount())
                .status(OrderStatus.CREATED)
                .description(request.getDescription())
                .expiresAt(LocalDateTime.now().plusMinutes(30))
                .build();

        order = orderRepository.save(order);
        return mapToResponse(order);
    }

    @Override
    public OrderResponse getOrderById(UUID id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        return mapToResponse(order);
    }

    @Override
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    private OrderResponse mapToResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .merchantId(order.getMerchantId())
                .tenantId(order.getTenantId())
                .merchantOrderRef(order.getMerchantOrderRef())
                .currency(order.getCurrency())
                .amount(order.getAmount())
                .amountPaid(order.getAmountPaid())
                .amountDue(order.getAmountDue())
                .status(order.getStatus())
                .description(order.getDescription())
                .build();
    }
}