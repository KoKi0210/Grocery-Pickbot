package com.example.grocerypickbot.product.controllers;

import com.example.grocerypickbot.exceptions.InvalidProductDataException;
import com.example.grocerypickbot.exceptions.UnauthorizedException;
import com.example.grocerypickbot.product.models.ProductDto;
import com.example.grocerypickbot.product.services.ProductService;
import com.example.grocerypickbot.product.services.ProductServiceImpl;
import com.example.grocerypickbot.security.annotation.RoleAccess;
import com.example.grocerypickbot.user.models.Role;
import jakarta.validation.Valid;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing product-related endpoints.
 *
 * <p>Provides endpoints to create, retrieve, update, and delete products.
 * Delegates business logic to the {@link ProductService}.
 * </p>
 */
@RestController
@RequestMapping("/products")
@Validated
@RoleAccess(allowedRoles = {Role.ADMIN})
public class ProductController {

  private final ProductService productService;

  /**
   * Constructor for ProductController.
   *
   * @param productService the product service implementation
   */
  public ProductController(ProductServiceImpl productService) {
    this.productService = productService;
  }

  /**
   * Creates a new product.
   *
   * @param productDto the product data transfer object
   * @return ResponseEntity containing the created product and HTTP status
   */
  @PostMapping
  public ResponseEntity<?> createProduct(@Valid @RequestBody ProductDto productDto) {
    ProductDto productDtoCreated;
    try {
      productDtoCreated = productService.createProduct(productDto);
    } catch (InvalidProductDataException e) {
      return ResponseEntity.badRequest().body(e.getErrors());
    } catch (UnauthorizedException e) {
      return ResponseEntity.badRequest().body(e.getErrors());
    }
    return new ResponseEntity<>(productDtoCreated, HttpStatus.CREATED);
  }

  /**
   * Retrieves all products.
   *
   * @return ResponseEntity containing the list of products and HTTP status
   */
  @RoleAccess(allowedRoles = {Role.ADMIN, Role.USER})
  @GetMapping
  public ResponseEntity<List<ProductDto>> getAllProducts() {
    return ResponseEntity.ok(productService.findAllProducts());
  }

  /**
   * Retrieves a product by its ID.
   *
   * @param id the ID of the product
   * @return ResponseEntity containing the product and HTTP status
   */
  @GetMapping("/{id}")
  public ResponseEntity<ProductDto> getProductById(@PathVariable Long id) {
    return ResponseEntity.ok(productService.findProductById(id));
  }

  /**
   * Updates an existing product.
   *
   * @param id         the ID of the product to update
   * @param productDto the updated product data transfer object
   * @return ResponseEntity containing the updated product and HTTP status
   */
  @PutMapping("/{id}")
  public ResponseEntity<?> updateProduct(@PathVariable Long id,
                                         @Valid @RequestBody ProductDto productDto) {
    try {
      ProductDto updatedProduct = productService.updateProduct(id, productDto);
      return ResponseEntity.ok(updatedProduct);
    } catch (InvalidProductDataException e) {
      return ResponseEntity.badRequest().body(e.getErrors());
    }
  }

  /**
   * Deletes a product by its ID.
   *
   * @param id the ID of the product to delete
   * @return ResponseEntity with HTTP status
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
    try{
      productService.deleteProduct(id);
      return ResponseEntity.noContent().build();
    }catch (InvalidProductDataException e) {
      return ResponseEntity.badRequest().body(e.getErrors());
    }
  }
}
