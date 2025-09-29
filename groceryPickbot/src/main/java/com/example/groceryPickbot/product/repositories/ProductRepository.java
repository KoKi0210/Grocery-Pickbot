package com.example.groceryPickbot.product.repositories;

import com.example.groceryPickbot.product.models.Location;
import com.example.groceryPickbot.product.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsByName(String name);

    Optional<Object> findByLocation(Location location);
}
