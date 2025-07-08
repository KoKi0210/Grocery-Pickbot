package com.example.groceryPickbot.product.repositories;

import com.example.groceryPickbot.product.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsByName(String name);
}
