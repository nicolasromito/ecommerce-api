package com.Romito.ecommerce_api.service;

import com.Romito.ecommerce_api.dto.OrderItemRequest;
import com.Romito.ecommerce_api.dto.OrderRequest;
import com.Romito.ecommerce_api.model.AppUser;
import com.Romito.ecommerce_api.model.Category;
import com.Romito.ecommerce_api.model.Product;
import com.Romito.ecommerce_api.model.Role;
import com.Romito.ecommerce_api.repository.AppUserRepository;
import com.Romito.ecommerce_api.repository.CategoryRepository;
import com.Romito.ecommerce_api.repository.ProductRepository;
import com.Romito.ecommerce_api.repository.RoleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
class OrderConcurrencyIntegrationTest {

    @Autowired private OrderService orderService;
    @Autowired private ProductRepository productRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private AppUserRepository appUserRepository;
    @Autowired private RoleRepository roleRepository;

    @Test
    void dosComprasSimultaneasPorElUltimoStockSoloUnaTieneExito() throws Exception {
        Role userRole = roleRepository.findByName("USER").orElseThrow();

        AppUser compradorA = crearUsuario("comprador_a", userRole);
        AppUser compradorB = crearUsuario("comprador_b", userRole);

        Category category = new Category();
        category.setName("Concurrencia");
        categoryRepository.save(category);

        Product product = new Product();
        product.setName("Último disponible");
        product.setPrice(new BigDecimal("50000.00"));
        product.setStock(1); // <- solo queda 1 unidad
        product.setCategory(category);
        Product saved = productRepository.save(product);

        OrderRequest request = new OrderRequest(List.of(new OrderItemRequest(saved.getId(), 1)));

        CountDownLatch startLatch = new CountDownLatch(1);
        ExecutorService executor = Executors.newFixedThreadPool(2);


        Future<Boolean> resultadoA = executor.submit(() -> {
            Thread.currentThread().setName("hilo-A");
            startLatch.await();
            try {
                orderService.create("comprador_a", request);
                return true;
            } catch (Exception e) {
                return false;
            }
        });

        Future<Boolean> resultadoB = executor.submit(() -> {
            Thread.currentThread().setName("hilo-B");
            startLatch.await();
            try {
                orderService.create("comprador_b", request);
                return true;
            } catch (Exception e) {
                return false;
            }
        });

        startLatch.countDown(); // libera a los dos hilos exactamente al mismo tiempo

        boolean exitoA = resultadoA.get(10, TimeUnit.SECONDS);
        boolean exitoB = resultadoB.get(10, TimeUnit.SECONDS);
        executor.shutdown();

        int exitosos = (exitoA ? 1 : 0) + (exitoB ? 1 : 0);
        assertEquals(1, exitosos, "Exactamente una de las dos compras debe tener éxito");

        Product productoFinal = productRepository.findById(saved.getId()).orElseThrow();
        assertEquals(0, productoFinal.getStock(), "El stock final no debe quedar negativo ni duplicado");
    }

    private AppUser crearUsuario(String username, Role role) {
        AppUser user = new AppUser();
        user.setUsername(username);
        user.setEmail(username + "@example.com");
        user.setPassword("hash-ficticio");
        user.setRole(role);
        return appUserRepository.save(user);
    }
}