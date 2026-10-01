package com.Romito.ecommerce_api.service;

import com.Romito.ecommerce_api.dto.ProductRequest;
import com.Romito.ecommerce_api.dto.ProductResponse;
import com.Romito.ecommerce_api.model.Category;
import com.Romito.ecommerce_api.model.Product;
import com.Romito.ecommerce_api.repository.CategoryRepository;
import com.Romito.ecommerce_api.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void creaProductoConCategoriaExistente() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Periféricos");

        ProductRequest request = new ProductRequest(
                "Teclado mecánico", "Switches rojos",
                new BigDecimal("45000.00"), 10, 1L);

        Product saved = new Product();
        saved.setId(1L);
        saved.setName(request.name());
        saved.setDescription(request.description());
        saved.setPrice(request.price());
        saved.setStock(request.stock());
        saved.setCategory(category);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class))).thenReturn(saved);

        ProductResponse response = productService.create(request);

        assertEquals("Teclado mecánico", response.name());
        assertEquals(1L, response.categoryId());
        assertEquals("Periféricos", response.categoryName());
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void creaProductoLanzaExcepcionSiCategoriaNoExiste() {
        ProductRequest request = new ProductRequest(
                "Mouse", null, new BigDecimal("10000.00"), 5, 99L);

        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> productService.create(request));
        verify(productRepository, never()).save(any());
    }
}