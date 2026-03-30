package com.fintech.platform.order.service.impl;

import com.fintech.platform.order.client.MerchantServiceClient;
import com.fintech.platform.order.dto.CreateOrderRequest;
import com.fintech.platform.order.dto.OrderResponse;
import com.fintech.platform.order.dto.UpdateOrderRequest;
import com.fintech.platform.order.entity.Order;
import com.fintech.platform.order.entity.OrderStatus;
import com.fintech.platform.order.entity.OrderStatusHistory;
import com.fintech.platform.order.exception.ResourceNotFoundException;
import com.fintech.platform.order.repository.OrderRepository;
import com.fintech.platform.order.repository.OrderStatusHistoryRepository;
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
    private final OrderStatusHistoryRepository orderStatusHistoryRepository;
    private final MerchantServiceClient merchantServiceClient;

    @Override
    public OrderResponse createOrder(CreateOrderRequest request) {
        MerchantServiceClient.MerchantDto merchant = merchantServiceClient.getMerchant(request.getMerchantId());

        if (merchant == null) {
            throw new ResourceNotFoundException("Merchant not found with id: " + request.getMerchantId());
        }

        if (!merchant.tenantId().equals(request.getTenantId())) {
            throw new IllegalStateException("Merchant does not belong to provided tenant");
        }

        if (!"ACTIVE".equalsIgnoreCase(merchant.status())) {
            throw new IllegalStateException("Merchant is not active");
        }

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
        Order order = getOrderEntity(id);
        return mapToResponse(order);
    }

    @Override
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public OrderResponse updateOrder(UUID id, UpdateOrderRequest request) {
        Order order = getOrderEntity(id);

        if (order.getStatus() == OrderStatus.PAID ||
                order.getStatus() == OrderStatus.CANCELLED ||
                order.getStatus() == OrderStatus.EXPIRED) {
            throw new IllegalStateException("Order cannot be updated in current status: " + order.getStatus());
        }

        order.setDescription(request.getDescription());
        order = orderRepository.save(order);

        return mapToResponse(order);
    }

    @Override
    public OrderResponse cancelOrder(UUID id) {
        Order order = getOrderEntity(id);

        if (order.getStatus() == OrderStatus.PAID) {
            throw new IllegalStateException("Paid order cannot be cancelled");
        }

        if (order.getStatus() == OrderStatus.CANCELLED) {
            return mapToResponse(order);
        }

        OrderStatus oldStatus = order.getStatus();
        order.setStatus(OrderStatus.CANCELLED);
        order = orderRepository.save(order);

        saveStatusHistory(order.getId(), oldStatus, OrderStatus.CANCELLED, "Order cancelled");
        return mapToResponse(order);
    }

    @Override
    public OrderResponse expireOrder(UUID id) {
        Order order = getOrderEntity(id);

        if (order.getStatus() == OrderStatus.PAID) {
            throw new IllegalStateException("Paid order cannot be expired");
        }

        if (order.getStatus() == OrderStatus.EXPIRED) {
            return mapToResponse(order);
        }

        OrderStatus oldStatus = order.getStatus();
        order.setStatus(OrderStatus.EXPIRED);
        order = orderRepository.save(order);

        saveStatusHistory(order.getId(), oldStatus, OrderStatus.EXPIRED, "Order expired");
        return mapToResponse(order);
    }

    private Order getOrderEntity(UUID id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
    }

    private void saveStatusHistory(UUID orderId, OrderStatus oldStatus, OrderStatus newStatus, String reason) {
        OrderStatusHistory history = OrderStatusHistory.builder()
                .orderId(orderId)
                .oldStatus(oldStatus)
                .newStatus(newStatus)
                .reason(reason)
                .build();

        orderStatusHistoryRepository.save(history);
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