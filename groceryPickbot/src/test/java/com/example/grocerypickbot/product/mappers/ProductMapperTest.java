package com.example.grocerypickbot.product.mappers;

import com.example.grocerypickbot.product.models.Location;
import com.example.grocerypickbot.product.models.Product;
import com.example.grocerypickbot.product.models.ProductDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class ProductMapperTest {
  private final ProductMapper productMapper = new ProductMapperImpl();

  @Test
  void toDto_shouldCorrectlyMapProductToProductDto() {

    Location location = new Location();
    location.setX(10);
    location.setY(3);

    Product product = new Product(1L, "Test Product", BigDecimal.valueOf(99.99), 50, location);

    ProductDto resultDto = productMapper.toDto(product);

    assertNotNull(resultDto);
    assertEquals(product.getId(), resultDto.id());
    assertEquals(product.getName(), resultDto.name());
    assertEquals(product.getPrice(), resultDto.price());
    assertEquals(product.getQuantity(), resultDto.quantity());
    assertNotNull(resultDto.location());
    assertEquals(location.getX(), resultDto.location().getX());
    assertEquals(location.getY(), resultDto.location().getY());
  }

  @Test
  void toEntity_shouldCorrectlyMapProductDtoToProduct() {
    Location location = new Location();
    location.setX(5);
    location.setY(7);

    ProductDto productDto = new ProductDto(2L, "Another Product", BigDecimal.valueOf(49.99), 20, location);

    Product resultProduct = productMapper.toEntity(productDto);

    assertNotNull(resultProduct);
    assertEquals(productDto.id(), resultProduct.getId());
    assertEquals(productDto.name(), resultProduct.getName());
    assertEquals(productDto.price(), resultProduct.getPrice());
    assertEquals(productDto.quantity(), resultProduct.getQuantity());
    assertNotNull(resultProduct.getLocation());
    assertEquals(location.getX(), resultProduct.getLocation().getX());
    assertEquals(location.getY(), resultProduct.getLocation().getY());
  }

  @Test
  void toProductInfo_shouldCorrectlyMapProductToProductInfo() {
    Location location = new Location();
    location.setX(8);
    location.setY(6);

    Product product = new Product(3L, "Info Product", BigDecimal.valueOf(19.99), 15, location);

    var resultInfo = productMapper.toProductInfo(product);

    assertNotNull(resultInfo);
    assertEquals(product.getName(), resultInfo.name());
    assertNotNull(resultInfo.location());
    assertEquals(location.getX(), resultInfo.location().x());
    assertEquals(location.getY(), resultInfo.location().y());
  }
}
