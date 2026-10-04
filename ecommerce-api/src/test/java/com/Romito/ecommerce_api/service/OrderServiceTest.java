package com.Romito.ecommerce_api.service;

import com.Romito.ecommerce_api.dto.*;
import com.Romito.ecommerce_api.model.*;
import com.Romito.ecommerce_api.repository.AppUserRepository;
import com.Romito.ecommerce_api.repository.OrderRepository;
import com.Romito.ecommerce_api.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock private OrderRepository orderRepository;
    @Mock private ProductRepository productRepository;
    @Mock private AppUserRepository appUserRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    void creaUnaOrdenYDescuentaStockCalculandoElTotal() {
        AppUser user = new AppUser();
        user.setUsername("comprador1");

        Product product = new Product();
        product.setId(1L);
        product.setName("Mouse");
        product.setPrice(new BigDecimal("10000.00"));
        product.setStock(5);

        OrderRequest request = new OrderRequest(
                List.of(new OrderItemRequest(1L, 2)));

        when(appUserRepository.findByUsername("comprador1")).thenReturn(Optional.of(user));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        OrderResponse response = orderService.create("comprador1", request);

        assertEquals(0, new BigDecimal("20000.00").compareTo(response.total()));
        assertEquals(3, product.getStock());

        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(productCaptor.capture());
        assertEquals(3, productCaptor.getValue().getStock());
    }

    @Test
    void rechazaLaOrdenSiNoHayStockSuficiente() {
        AppUser user = new AppUser();
        user.setUsername("comprador1");

        Product product = new Product();
        product.setId(1L);
        product.setStock(1);

        OrderRequest request = new OrderRequest(
                List.of(new OrderItemRequest(1L, 5)));

        when(appUserRepository.findByUsername("comprador1")).thenReturn(Optional.of(user));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        assertThrows(IllegalStateException.class, () -> orderService.create("comprador1", request));
        verify(orderRepository, never()).save(any());
    }

    @Test
    void calculaElTotalSumandoVariosItems() {
        AppUser user = new AppUser();
        user.setUsername("comprador1");

        Product mouse = new Product();
        mouse.setId(1L);
        mouse.setPrice(new BigDecimal("10000.00"));
        mouse.setStock(10);

        Product teclado = new Product();
        teclado.setId(2L);
        teclado.setPrice(new BigDecimal("45000.00"));
        teclado.setStock(10);

        OrderRequest request = new OrderRequest(List.of(
                new OrderItemRequest(1L, 2),
                new OrderItemRequest(2L, 1)));

        when(appUserRepository.findByUsername("comprador1")).thenReturn(Optional.of(user));
        when(productRepository.findById(1L)).thenReturn(Optional.of(mouse));
        when(productRepository.findById(2L)).thenReturn(Optional.of(teclado));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        OrderResponse response = orderService.create("comprador1", request);

        // 2 * 10000 + 1 * 45000 = 65000
        assertEquals(0, new BigDecimal("65000.00").compareTo(response.total()));
        assertEquals(2, response.items().size());
    }

    @Test
    void findMyOrdersDevuelveSoloLasOrdenesDelUsuario() {
        AppUser user = new AppUser();
        user.setId(1L);
        user.setUsername("compradora2");

        Order order = new Order();
        order.setId(2L);
        order.setUser(user);
        order.setTotal(new BigDecimal("12000.00"));
        order.setStatus(OrderStatus.PENDING);

        when(appUserRepository.findByUsername("compradora2")).thenReturn(Optional.of(user));
        when(orderRepository.findByUserId(1L)).thenReturn(List.of(order));

        List<OrderResponse> result = orderService.findMyOrders("compradora2");

        assertEquals(1, result.size());
        assertEquals(2L, result.get(0).id());
    }

    @Test
    void findMyOrderByIdDevuelveLaOrdenSiEsDelPropioUsuario() {
        AppUser user = new AppUser();
        user.setId(1L);
        user.setUsername("compradora2");

        Order order = new Order();
        order.setId(2L);
        order.setUser(user);
        order.setTotal(new BigDecimal("12000.00"));
        order.setStatus(OrderStatus.PENDING);

        when(appUserRepository.findByUsername("compradora2")).thenReturn(Optional.of(user));
        when(orderRepository.findById(2L)).thenReturn(Optional.of(order));

        OrderResponse response = orderService.findMyOrderById("compradora2", 2L);

        assertEquals(2L, response.id());
    }

    @Test
    void findMyOrderByIdRechazaVerLaOrdenDeOtroUsuario() {
        AppUser dueño = new AppUser();
        dueño.setId(1L);
        dueño.setUsername("compradora2");

        AppUser otroUsuario = new AppUser();
        otroUsuario.setId(2L);
        otroUsuario.setUsername("compradora3");

        Order order = new Order();
        order.setId(2L);
        order.setUser(dueño);

        when(appUserRepository.findByUsername("compradora3")).thenReturn(Optional.of(otroUsuario));
        when(orderRepository.findById(2L)).thenReturn(Optional.of(order));

        assertThrows(SecurityException.class,
                () -> orderService.findMyOrderById("compradora3", 2L));
    }
}