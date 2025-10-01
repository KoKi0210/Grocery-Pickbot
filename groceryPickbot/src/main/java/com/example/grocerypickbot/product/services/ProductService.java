package com.example.grocerypickbot.product.services;

import com.example.grocerypickbot.product.models.ProductDto;
import java.util.List;

/**
 * Service interface for managing products.
 */
public interface ProductService {

  /**
   * Creates a new product.
   *
   * @param product the product to create
   * @return the created product
   */
  ProductDto createProduct(ProductDto product);

  /**
   * Retrieves all products.
   *
   * @return a list of all products
   */
  List<ProductDto> findAllProducts();

  /**
   * Finds a product by its ID.
   *
   * @param id the ID of the product
   * @return the found product
   */
  ProductDto findProductById(Long id);

  /**
   * Updates an existing product.
   *
   * @param id the ID of the product to update
   * @param product the updated product data
   * @return the updated product
   */
  ProductDto updateProduct(Long id, ProductDto product);

  /**
   * Deletes a product by its ID.
   *
   * @param id the ID of the product to delete
   */
  void deleteProduct(Long id);

}
