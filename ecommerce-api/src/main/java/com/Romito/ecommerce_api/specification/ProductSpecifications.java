package com.Romito.ecommerce_api.specification;

import com.Romito.ecommerce_api.model.Product;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class ProductSpecifications {

    public static Specification<Product> hasCategoryId(Long categoryId) {
        return (root, query, cb) -> categoryId == null
                ? null
                : cb.equal(root.get("category").get("id"), categoryId);
    }

    public static Specification<Product> hasPriceGreaterThanOrEqual(BigDecimal minPrice) {
        return (root, query, cb) -> minPrice == null
                ? null
                : cb.greaterThanOrEqualTo(root.get("price"), minPrice);
    }

    public static Specification<Product> hasPriceLessThanOrEqual(BigDecimal maxPrice) {
        return (root, query, cb) -> maxPrice == null
                ? null
                : cb.lessThanOrEqualTo(root.get("price"), maxPrice);
    }
}