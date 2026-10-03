package com.Romito.ecommerce_api.controller;

import com.Romito.ecommerce_api.dto.OrderRequest;
import com.Romito.ecommerce_api.dto.OrderResponse;
import com.Romito.ecommerce_api.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> create(@Valid @RequestBody OrderRequest request,
                                                 Authentication authentication) {
        String username = authentication.getName();
        OrderResponse response = orderService.create(username, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}