package com.Romito.ecommerce_api.repository;

import com.Romito.ecommerce_api.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}