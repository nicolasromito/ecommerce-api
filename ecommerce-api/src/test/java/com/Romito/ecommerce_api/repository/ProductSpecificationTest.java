package com.Romito.ecommerce_api.repository;

import com.Romito.ecommerce_api.model.Category;
import com.Romito.ecommerce_api.model.Product;
import com.Romito.ecommerce_api.specification.ProductSpecifications;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ProductSpecificationTest {

    @Autowired private ProductRepository productRepository;
    @Autowired private CategoryRepository categoryRepository;

    private Long perifericosId;

    @BeforeEach
    void setUp() {
        Category perifericos = new Category();
        perifericos.setName("Periféricos");
        perifericos = categoryRepository.save(perifericos);
        perifericosId = perifericos.getId();

        Category monitores = new Category();
        monitores.setName("Monitores");
        monitores = categoryRepository.save(monitores);

        crearProducto("Mouse", new BigDecimal("12000.00"), perifericos);
        crearProducto("Teclado", new BigDecimal("45000.00"), perifericos);
        crearProducto("Monitor 24\"", new BigDecimal("180000.00"), monitores);
    }

    @Test
    void filtraPorCategoria() {
        Specification<Product> spec = Specification.where(
                ProductSpecifications.hasCategoryId(perifericosId));

        Page<Product> result = productRepository.findAll(spec, PageRequest.of(0, 10));

        assertEquals(2, result.getTotalElements());
    }

    @Test
    void filtraPorRangoDePrecio() {
        Specification<Product> spec = Specification
                .where(ProductSpecifications.hasPriceGreaterThanOrEqual(new BigDecimal("40000.00")))
                .and(ProductSpecifications.hasPriceLessThanOrEqual(new BigDecimal("100000.00")));

        Page<Product> result = productRepository.findAll(spec, PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
        assertEquals("Teclado", result.getContent().get(0).getName());
    }

    @Test
    void sinFiltrosDevuelveTodosPaginado() {
        Specification<Product> spec = Specification
                .where(ProductSpecifications.hasCategoryId(null))
                .and(ProductSpecifications.hasPriceGreaterThanOrEqual(null));

        Page<Product> result = productRepository.findAll(spec, PageRequest.of(0, 2));

        assertEquals(3, result.getTotalElements());
        assertEquals(2, result.getNumberOfElements());
        assertEquals(2, result.getTotalPages());
    }

    private void crearProducto(String name, BigDecimal price, Category category) {
        Product product = new Product();
        product.setName(name);
        product.setPrice(price);
        product.setStock(10);
        product.setCategory(category);
        productRepository.save(product);
    }
}