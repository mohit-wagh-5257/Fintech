package com.fintech.platform.order.controller;

import com.fintech.platform.order.dto.CreateOrderRequest;
import com.fintech.platform.order.dto.OrderResponse;
import com.fintech.platform.order.dto.UpdateOrderRequest;
import com.fintech.platform.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public OrderResponse createOrder(@Valid @RequestBody CreateOrderRequest request) {
        return orderService.createOrder(request);
    }

    @GetMapping("/{id}")
    public OrderResponse getOrderById(@PathVariable UUID id) {
        return orderService.getOrderById(id);
    }

    @GetMapping
    public List<OrderResponse> getAllOrders() {
        return orderService.getAllOrders();
    }

    @PutMapping("/{id}")
    public OrderResponse updateOrder(@PathVariable UUID id,
                                     @RequestBody UpdateOrderRequest request) {
        return orderService.updateOrder(id, request);
    }

    @PutMapping("/{id}/cancel")
    public OrderResponse cancelOrder(@PathVariable UUID id) {
        return orderService.cancelOrder(id);
    }

    @PutMapping("/{id}/expire")
    public OrderResponse expireOrder(@PathVariable UUID id) {
        return orderService.expireOrder(id);
    }
}