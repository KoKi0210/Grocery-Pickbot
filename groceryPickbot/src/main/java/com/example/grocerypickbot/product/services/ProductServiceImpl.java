package com.example.grocerypickbot.product.services;

import com.example.grocerypickbot.product.mappers.ProductMapper;
import com.example.grocerypickbot.product.models.Product;
import com.example.grocerypickbot.product.models.ProductDto;
import com.example.grocerypickbot.product.repositories.ProductRepository;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
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
   * @param productMapper the mapper for converting between Product and ProductDto
   */
  public ProductServiceImpl(ProductRepository productRepository, ProductMapper productMapper) {
    this.productRepository = productRepository;
    this.productMapper = productMapper;
  }

  @Override
  public ProductDto createProduct(@Valid ProductDto productDto) {
    if (productRepository.existsByName(productDto.name())) {
      throw new IllegalArgumentException("Product with this name already exists");
    }

    if (productRepository.findByLocation(productDto.location()).isPresent()) {
      throw new IllegalArgumentException("Location is already occupied by another product");
    }

    if (productDto.location().getX() == 0 && productDto.location().getY() == 0) {
      throw new IllegalArgumentException("Location can't be {0:0}");
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
    Product existingProduct = productRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Product not found"));

    productRepository.findByLocation(productDto.location())
        .filter(productAtLocation -> !productAtLocation.getId().equals(existingProduct.getId()))
        .ifPresent(productAtLocation -> {
          throw new IllegalArgumentException("Location is already occupied by another product");
        });

    if (productDto.location().getX() == 0 && productDto.location().getY() == 0) {
      throw new IllegalArgumentException("Location can't be {0:0}");
    }

    productMapper.updateProductFromDto(productDto, existingProduct);
    Product updatedProduct = productRepository.save(existingProduct);
    return productMapper.toDto(updatedProduct);
  }

  @Override
  public void deleteProduct(Long id) {
    if (!productRepository.existsById(id)) {
      throw new IllegalArgumentException("Product not found");
    }
    productRepository.deleteById(id);
  }

}
