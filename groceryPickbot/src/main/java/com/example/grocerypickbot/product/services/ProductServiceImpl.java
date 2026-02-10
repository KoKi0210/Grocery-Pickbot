package com.example.grocerypickbot.product.services;

import com.example.grocerypickbot.exceptions.InvalidProductDataException;
import com.example.grocerypickbot.product.mappers.ProductMapper;
import com.example.grocerypickbot.product.models.Product;
import com.example.grocerypickbot.product.models.ProductDto;
import com.example.grocerypickbot.product.repositories.ProductRepository;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

/**
 * Service implementation for managing products.
 */
@Service
public class ProductServiceImpl implements ProductService {

  private final ProductRepository productRepository;
  private final ProductMapper productMapper;

  /**
   * Constructs a ProductServiceImpl with the specified ProductRepository and ProductMapper.
   *
   * @param productRepository the repository for managing products
   * @param productMapper     the mapper for converting between Product and ProductDto
   */
  public ProductServiceImpl(ProductRepository productRepository, ProductMapper productMapper) {
    this.productRepository = productRepository;
    this.productMapper = productMapper;
  }

  @Override
  public ProductDto createProduct(@Valid ProductDto productDto) {
    Map<String, String> errors = new HashMap<>();

    if (productRepository.existsByName(productDto.name())) {
      errors.put("name", "Product with this name already exists");
    }

    if (productRepository.findByLocation(productDto.location()).isPresent()) {
      errors.put("locationOccupied", "Location is already occupied by another product");
    }

    if (productDto.location().getX() == 0 && productDto.location().getY() == 0) {
      errors.put("location", "Location can't be {0:0}");
    }

    if (!errors.isEmpty()) {
      throw new InvalidProductDataException(errors);
    }

    Product product = productMapper.toEntity(productDto);
    Product savedProduct = productRepository.save(product);
    return productMapper.toDto(savedProduct);
  }

  @Override
  public List<ProductDto> findAllProducts() {
    List<ProductDto> products = productRepository.findAll().stream()
        .map(productMapper::toDto)
        .collect(Collectors.toList());
    if (products.isEmpty()) {
      throw new IllegalArgumentException("No products found");
    }
    return products;
  }

  @Override
  public ProductDto findProductById(Long id) {
    return productRepository.findById(id)
        .map(productMapper::toDto)
        .orElseThrow(() -> new IllegalArgumentException("Product not found"));
  }

  @Override
  public ProductDto updateProduct(Long id, @Valid ProductDto productDto) {
    Map<String, String> errors = new HashMap<>();

    if (id == (null) || id <= 0) {
      errors.put("invalid", "Invalid product ID");
      throw new InvalidProductDataException(errors);
    }

    Optional<Product> existingProduct = productRepository.findById(id);
    if (existingProduct.isEmpty()) {
      errors.put("notFound", "Product not found");
      throw new InvalidProductDataException(errors);
    }

    Product product = existingProduct.get();

    productRepository.findByLocation(productDto.location())
        .filter(productAtLocation -> !productAtLocation.getId().equals(product.getId()))
        .ifPresent(productAtLocation -> errors.put("locationOccupied",
            "Location is already occupied by another product"));

    if (productDto.location().getX() == 0 && productDto.location().getY() == 0) {
      errors.put("location", "Location can't be {0:0}");
    }

    if (!errors.isEmpty()) {
      throw new InvalidProductDataException(errors);
    }

    productMapper.updateProductFromDto(productDto, product);
    Product updatedProduct = productRepository.save(product);
    return productMapper.toDto(updatedProduct);
  }

  @Override
  public void deleteProduct(Long id) throws InvalidProductDataException {
    Map<String, String> errors = new HashMap<>();

    if (id == (null) || id <= 0) {
      errors.put("invalid", "Invalid product ID");
      throw new InvalidProductDataException(errors);
    }
    if (!productRepository.existsById(id)) {
      errors.put("invalid", "Product not found");
      throw new InvalidProductDataException(errors);
    }
    try {
      productRepository.deleteById(id);
    } catch (DataIntegrityViolationException e) {
      errors.put("invalid", "Cannot delete product. It is part of an existing order.");
      throw new InvalidProductDataException(errors);
    }
  }
}
