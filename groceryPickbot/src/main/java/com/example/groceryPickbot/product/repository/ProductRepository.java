package com.example.groceryPickbot.product.repository;

import com.example.groceryPickbot.product.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsByName(String name);
}
