package com.Romito.ecommerce_api.service;

import com.Romito.ecommerce_api.dto.*;
import com.Romito.ecommerce_api.model.*;
import com.Romito.ecommerce_api.repository.AppUserRepository;
import com.Romito.ecommerce_api.repository.OrderRepository;
import com.Romito.ecommerce_api.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final AppUserRepository appUserRepository;

    public OrderService(OrderRepository orderRepository,
                         ProductRepository productRepository,
                         AppUserRepository appUserRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.appUserRepository = appUserRepository;
    }

    @Transactional
    public OrderResponse create(String username, OrderRequest request) {
        AppUser user = appUserRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado: " + username));

        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);

        BigDecimal total = BigDecimal.ZERO;

        for (OrderItemRequest itemRequest : request.items()) {
            Product product = productRepository.findById(itemRequest.productId())
                    .orElseThrow(() -> new NoSuchElementException(
                            "Producto no encontrado: " + itemRequest.productId()));

            if (product.getStock() < itemRequest.quantity()) {
                throw new IllegalStateException(
                        "Stock insuficiente para " + product.getName()
                                + " (disponible: " + product.getStock()
                                + ", pedido: " + itemRequest.quantity() + ")");
            }

            product.setStock(product.getStock() - itemRequest.quantity());
            productRepository.save(product);

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(product);
            item.setQuantity(itemRequest.quantity());
            item.setUnitPrice(product.getPrice());
            order.getItems().add(item);

            total = total.add(product.getPrice().multiply(BigDecimal.valueOf(itemRequest.quantity())));
        }

        order.setTotal(total);
        Order saved = orderRepository.save(order);

        return toResponse(saved);
    }

    private OrderResponse toResponse(Order order) {
                List<OrderItemResponse> items = order.getItems().stream()
                        .map(item -> new OrderItemResponse(
                                item.getProduct().getId(),
                                item.getProduct().getName(),
                                item.getQuantity(),
                                item.getUnitPrice(),
                                item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
                        ))
                        .toList();

                return new OrderResponse(
                        order.getId(), order.getTotal(), order.getStatus(), order.getCreatedAt(), items);
        }

        public List<OrderResponse> findMyOrders(String username) {
        AppUser user = appUserRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado: " + username));

        return orderRepository.findByUserId(user.getId())
                .stream()
                .map(this::toResponse)
                .toList();
        }

        public OrderResponse findMyOrderById(String username, Long orderId) {
        AppUser user = appUserRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado: " + username));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("Orden no encontrada: " + orderId));

        if (!order.getUser().getId().equals(user.getId())) {
                throw new SecurityException("No tenés permiso para ver esta orden");
        }

        return toResponse(order);
        }
}