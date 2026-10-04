package com.Romito.ecommerce_api.service;

import com.Romito.ecommerce_api.dto.ProductRequest;
import com.Romito.ecommerce_api.dto.ProductResponse;
import com.Romito.ecommerce_api.model.Category;
import com.Romito.ecommerce_api.model.Product;
import com.Romito.ecommerce_api.repository.CategoryRepository;
import com.Romito.ecommerce_api.repository.ProductRepository;
import com.Romito.ecommerce_api.specification.ProductSpecifications;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository,
                           CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    public Page<ProductResponse> findAll(Long categoryId, BigDecimal minPrice,
                                      BigDecimal maxPrice, Pageable pageable) {
        Specification<Product> spec = Specification
                .where(ProductSpecifications.hasCategoryId(categoryId))
                .and(ProductSpecifications.hasPriceGreaterThanOrEqual(minPrice))
                .and(ProductSpecifications.hasPriceLessThanOrEqual(maxPrice));

        return productRepository.findAll(spec, pageable)
                .map(this::toResponse);
        }

    public ProductResponse findById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Producto no encontrado: " + id));
        return toResponse(product);
    }

    public ProductResponse create(ProductRequest request) {
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new NoSuchElementException(
                        "Categoría no encontrada: " + request.categoryId()));

        Product product = new Product();
        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setStock(request.stock());
        product.setCategory(category);

        Product saved = productRepository.save(product);
        return toResponse(saved);
    }

    private ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock(),
                product.getCategory().getId(),
                product.getCategory().getName()
        );
    }
}