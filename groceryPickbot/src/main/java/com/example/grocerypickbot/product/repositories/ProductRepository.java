package com.example.grocerypickbot.product.repositories;

import com.example.grocerypickbot.product.models.Location;
import com.example.grocerypickbot.product.models.Product;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing Product entities.
 */
public interface ProductRepository extends JpaRepository<Product, Long> {
  /**
   * Checks if a product with the specified name exists.
   *
   * @param name the name of the product
   * @return true if a product with the given name exists, false otherwise
   */
  boolean existsByName(String name);

  /**
   * Finds a product by its location.
   *
   * @param location the location of the product
   * @return an Optional containing the product if found, or empty if not found
   */
  Optional<Product> findByLocation(Location location);
}
