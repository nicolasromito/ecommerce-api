package com.Romito.ecommerce_api.repository;
import com.Romito.ecommerce_api.model.Category;
import com.Romito.ecommerce_api.model.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TestEntityManager em;

    @Test
    void guardaProductoConCategoriaYLoLeeDeVuelta() {
        Category category = new Category();
        category.setName("Periféricos");
        category = categoryRepository.save(category);

        Product product = new Product();
        product.setName("Teclado mecánico");
        product.setPrice(new BigDecimal("45000.00"));
        product.setStock(10);
        product.setCategory(category);
        Product saved = productRepository.save(product);

        em.flush();   // manda los INSERT a la base
        em.clear();   // vacía la caché de Hibernate para forzar un SELECT real

        Product found = productRepository.findById(saved.getId()).orElseThrow();

        assertEquals("Teclado mecánico", found.getName());
        assertEquals(0, new BigDecimal("45000.00").compareTo(found.getPrice()));
        assertEquals(10, found.getStock());
        assertEquals(category.getId(), found.getCategory().getId());
        assertNotNull(found.getVersion());
    }

    @Test
    void noPermiteGuardarProductoSinNombre() {
        Product product = new Product();
        product.setPrice(new BigDecimal("100.00"));
        product.setStock(1);

        assertThrows(DataIntegrityViolationException.class,
                () -> productRepository.saveAndFlush(product));
    }
}