package com.Romito.ecommerce_api.repository;

import com.Romito.ecommerce_api.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class OrderRepositoryTest {

    @Autowired private OrderRepository orderRepository;
    @Autowired private AppUserRepository appUserRepository;
    @Autowired private RoleRepository roleRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private TestEntityManager em;

    @Test
    void guardaUnaOrdenConVariosItemsPorCascada() {
        Role userRole = roleRepository.findByName("USER").orElseThrow();

        AppUser user = new AppUser();
        user.setUsername("comprador1");
        user.setEmail("comprador1@example.com");
        user.setPassword("hash-ficticio");
        user.setRole(userRole);
        appUserRepository.save(user);

        Category category = new Category();
        category.setName("Accesorios");
        categoryRepository.save(category);

        Product product = new Product();
        product.setName("Mouse");
        product.setPrice(new BigDecimal("10000.00"));
        product.setStock(20);
        product.setCategory(category);
        productRepository.save(product);

        Order order = new Order();
        order.setUser(user);
        order.setTotal(new BigDecimal("20000.00"));
        order.setStatus(OrderStatus.PENDING);

        OrderItem item = new OrderItem();
        item.setOrder(order);
        item.setProduct(product);
        item.setQuantity(2);
        item.setUnitPrice(new BigDecimal("10000.00"));
        order.getItems().add(item);

        Order saved = orderRepository.save(order);

        em.flush();
        em.clear();

        Order found = orderRepository.findById(saved.getId()).orElseThrow();

        assertEquals(1, found.getItems().size());
        assertEquals(2, found.getItems().get(0).getQuantity());
        assertEquals("comprador1", found.getUser().getUsername());
        assertEquals(OrderStatus.PENDING, found.getStatus());
    }
}