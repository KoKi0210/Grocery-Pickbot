package com.example.grocerypickbot.product.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.example.grocerypickbot.product.mappers.ProductMapper;
import com.example.grocerypickbot.product.models.Location;
import com.example.grocerypickbot.product.models.Product;
import com.example.grocerypickbot.product.models.ProductDto;
import com.example.grocerypickbot.product.repositories.ProductRepository;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

  @Mock
  private ProductRepository productRepository;
  @Mock
  private ProductMapper productMapper;
  @InjectMocks
  private ProductServiceImpl productService;

  @Test
  void findAllProducts_shouldReturnListOfDtos_whenProductsExist() {

    Location locationZeroZero = new Location();
    locationZeroZero.setX(0);
    locationZeroZero.setY(0);
    Location locationOneOne = new Location();
    locationOneOne.setX(1);
    locationOneOne.setY(1);
    Product product1 = new Product(15L, "string", BigDecimal.valueOf(1200), 10, locationZeroZero);
    Product product2 = new Product(2L, "string", BigDecimal.valueOf(50), 100, locationOneOne);
    List<Product> productsFromDb = List.of(product1, product2);

    ProductDto dto1 = new ProductDto(15L, "string", BigDecimal.valueOf(1200), 10, locationZeroZero);
    ProductDto dto2 = new ProductDto(2L, "string", BigDecimal.valueOf(50), 100,
        locationOneOne);
    List<ProductDto> expectedDtos = List.of(dto1, dto2);

    when(productRepository.findAll()).thenReturn(productsFromDb);
    when(productMapper.toDto(product1)).thenReturn(dto1);
    when(productMapper.toDto(product2)).thenReturn(dto2);

    List<ProductDto> result = productService.findAllProducts();

    assertEquals(expectedDtos, result);
  }

  @Test
  void createProduct_shouldThrowException_whenProductNameAlreadyExists() {
    Location location = new Location();
    location.setX(0);
    location.setY(1);
    ProductDto productDto = new ProductDto(1L,"ExistingProduct", BigDecimal.valueOf(1),1,
        location);
    when(productRepository.existsByName(productDto.name())).thenReturn(true);

    assertThrows(IllegalArgumentException.class, () -> productService.createProduct(productDto));
  }

  @Test
  void createProduct_shouldThrowException_whenLocationIsOccupied() {
    Location location = new Location();
    location.setX(1);
    location.setY(0);
    ProductDto productDto = new ProductDto(1L,"NewProduct",BigDecimal.valueOf(1),1,
        location);
    when(productRepository.existsByName(productDto.name())).thenReturn(false);
    when(productRepository.findByLocation(productDto.location())).thenReturn(Optional.of(new Product()));

    assertThrows(IllegalArgumentException.class, () -> productService.createProduct(productDto));
  }

  @Test
  void createProduct_shouldThrowException_whenLocationIsZeroZero() {
    Location locationZeroZero = new Location();
    locationZeroZero.setX(0);
    locationZeroZero.setY(0);
    ProductDto productDto = new ProductDto(1L,"NewProduct",BigDecimal.valueOf(1),
        1, locationZeroZero);
    when(productRepository.existsByName(productDto.name())).thenReturn(false);
    when(productRepository.findByLocation(productDto.location())).thenReturn(Optional.empty());

    assertThrows(IllegalArgumentException.class, () -> productService.createProduct(productDto));
  }

  @Test
  void findAllProducts_shouldThrowException_whenNoProductsExist() {
    when(productRepository.findAll()).thenReturn(Collections.emptyList());

    assertThrows(IllegalArgumentException.class, () -> productService.findAllProducts());
  }

  @Test
  void findProductById_shouldThrowException_whenProductDoesNotExist() {
    Long productId = 1L;
    when(productRepository.findById(productId)).thenReturn(Optional.empty());

    assertThrows(IllegalArgumentException.class, () -> productService.findProductById(productId));
  }

  @Test
  void updateProduct_shouldThrowException_whenProductDoesNotExist() {
    Long productId = 1L;
    Location location = new Location();
    location.setX(0);
    location.setY(1);

    ProductDto productDto = new ProductDto(1L,"UpdatedProduct", BigDecimal.valueOf(1),1,location);
    when(productRepository.findById(productId)).thenReturn(Optional.empty());

    assertThrows(IllegalArgumentException.class, () -> productService.updateProduct(productId, productDto));
  }

  @Test
  void updateProduct_shouldThrowException_whenLocationIsOccupiedByAnotherProduct() {
    Location locationOneOne = new Location();
    locationOneOne.setX(0);
    locationOneOne.setY(1);
    Location locationTwoTwo = new Location();
    locationTwoTwo.setX(2);
    locationTwoTwo.setY(2);
    Product existingProduct = new Product(1L, "ExistingProduct", BigDecimal.valueOf(1),1,
        locationOneOne);
    ProductDto productDto = new ProductDto(1L,"UpdatedProduct",BigDecimal.valueOf(1) ,1,
        locationTwoTwo);
    Product anotherProduct = new Product(2L, "AnotherProduct", BigDecimal.valueOf(1),1,
        locationTwoTwo);

    when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
    when(productRepository.findByLocation(productDto.location())).thenReturn(Optional.of(anotherProduct));

    assertThrows(IllegalArgumentException.class, () -> productService.updateProduct(1L, productDto));
  }

  @Test
  void deleteProduct_shouldThrowException_whenProductDoesNotExist() {
    Long productId = 1L;
    when(productRepository.existsById(productId)).thenReturn(false);

    assertThrows(IllegalArgumentException.class, () -> productService.deleteProduct(productId));
  }
}