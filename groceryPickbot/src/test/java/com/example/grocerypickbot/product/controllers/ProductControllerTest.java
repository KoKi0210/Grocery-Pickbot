package com.example.grocerypickbot.product.controllers;

import com.example.grocerypickbot.product.models.Location;
import com.example.grocerypickbot.product.models.ProductDto;
import com.example.grocerypickbot.product.services.ProductServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
public class ProductControllerTest {

  @Mock
  private ProductServiceImpl productService;

  @InjectMocks
  private ProductController productController;

  private MockMvc mockMvc;
  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
      mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
      objectMapper = new ObjectMapper();
  }

  private Location createLocation(int x, int y) {
    Location location = new Location();
    location.setX(x);
    location.setY(y);
    return location;
  }

  @Test
  void createProduct_whenValidRequest_shouldReturnCreatedStatus() throws Exception {
    Location location = createLocation(1, 2);
    ProductDto productDto = new ProductDto(null, "Apple", new BigDecimal("1.99"), 100, location);
    ProductDto createdProduct = new ProductDto(1L, "Apple", new BigDecimal("1.99"), 100, location);

    when(productService.createProduct(any(ProductDto.class))).thenReturn(createdProduct);

    mockMvc.perform(post("/products")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(productDto)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(createdProduct.id()))
        .andExpect(jsonPath("$.name").value(createdProduct.name()))
        .andExpect(jsonPath("$.price").value(createdProduct.price()))
        .andExpect(jsonPath("$.quantity").value(createdProduct.quantity()));

    verify(productService).createProduct(any(ProductDto.class));
  }

  @Test
  void getAllProducts_whenProductsExist_shouldReturnOkWithProductList() throws Exception {
    Location location1 = createLocation(1, 2);
    Location location2 = createLocation(3, 4);
    ProductDto product1 = new ProductDto(1L, "Apple", new BigDecimal("1.99"), 100, location1);
    ProductDto product2 = new ProductDto(2L, "Banana", new BigDecimal("0.99"), 150, location2);
    List<ProductDto> products = Arrays.asList(product1, product2);

    when(productService.findAllProducts()).thenReturn(products);

    mockMvc.perform(get("/products"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$[0].id").value(products.get(0).id()))
        .andExpect(jsonPath("$[0].name").value(products.get(0).name()))
        .andExpect(jsonPath("$[1].id").value(products.get(1).id()))
        .andExpect(jsonPath("$[1].name").value(products.get(1).name()));

    verify(productService).findAllProducts();
  }

  @Test
  void getAllProducts_whenNoProducts_shouldReturnEmptyList() throws Exception {
    when(productService.findAllProducts()).thenReturn(Collections.emptyList());

    mockMvc.perform(get("/products"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$").isEmpty());

    verify(productService).findAllProducts();
  }

  @Test
  void getProductById_whenProductExists_shouldReturnOkWithProduct() throws Exception {
    Long productId = 1L;
    Location location = createLocation(1, 2);
    ProductDto product = new ProductDto(productId, "Apple", new BigDecimal("1.99"), 100, location);

    when(productService.findProductById(productId)).thenReturn(product);

    mockMvc.perform(get("/products/{id}", productId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(product.id()))
        .andExpect(jsonPath("$.name").value(product.name()))
        .andExpect(jsonPath("$.price").value(product.price()))
        .andExpect(jsonPath("$.quantity").value(product.quantity()));

    verify(productService).findProductById(productId);
  }

  @Test
  void updateProduct_whenValidRequest_shouldReturnOkWithUpdatedProduct() throws Exception {
    Long productId = 1L;
    Location location = createLocation(1, 2);
    ProductDto productDto = new ProductDto(null, "Updated Apple", new BigDecimal("2.49"), 120, location);
    ProductDto updatedProduct = new ProductDto(productId, "Updated Apple", new BigDecimal("2.49"), 120, location);

    when(productService.updateProduct(eq(productId), any(ProductDto.class))).thenReturn(updatedProduct);

    mockMvc.perform(put("/products/{id}", productId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(productDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(updatedProduct.id()))
        .andExpect(jsonPath("$.name").value(updatedProduct.name()))
        .andExpect(jsonPath("$.price").value(updatedProduct.price()))
        .andExpect(jsonPath("$.quantity").value(updatedProduct.quantity()));

    verify(productService).updateProduct(eq(productId), any(ProductDto.class));
  }

  @Test
  void deleteProduct_whenProductExists_shouldReturnNoContent() throws Exception {
    Long productId = 1L;

    doNothing().when(productService).deleteProduct(productId);

    mockMvc.perform(delete("/products/{id}", productId))
        .andExpect(status().isNoContent());

    verify(productService).deleteProduct(productId);
  }

  @Test
  void createProduct_whenNameIsBlank_shouldReturnBadRequest() throws Exception {
    Location location = createLocation(1, 2);
    ProductDto productDto = new ProductDto(null, "", new BigDecimal("1.99"), 100, location);

    mockMvc.perform(post("/products")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(productDto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void createProduct_whenPriceIsNegative_shouldReturnBadRequest() throws Exception {
    Location location = createLocation(1, 2);
    ProductDto productDto = new ProductDto(null, "Apple", new BigDecimal("-1.00"), 100, location);

    mockMvc.perform(post("/products")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(productDto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void createProduct_whenQuantityIsZero_shouldReturnBadRequest() throws Exception {
    Location location = createLocation(1, 2);
    ProductDto productDto = new ProductDto(null, "Apple", new BigDecimal("1.99"), 0, location);

    mockMvc.perform(post("/products")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(productDto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void createProduct_whenLocationIsNull_shouldReturnBadRequest() throws Exception {
    ProductDto productDto = new ProductDto(null, "Apple", new BigDecimal("1.99"), 100, null);

    mockMvc.perform(post("/products")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(productDto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void updateProduct_whenNameIsTooShort_shouldReturnBadRequest() throws Exception {
    Long productId = 1L;
    Location location = createLocation(1, 2);
    ProductDto productDto = new ProductDto(null, "Ab", new BigDecimal("1.99"), 100, location);

    mockMvc.perform(put("/products/{id}", productId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(productDto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void getProductById_whenDifferentId_shouldReturnOkWithCorrectProduct() throws Exception {
    Long productId = 99L;
    Location location = createLocation(5, 6);
    ProductDto product = new ProductDto(productId, "Orange", new BigDecimal("3.50"), 75, location);

    when(productService.findProductById(productId)).thenReturn(product);

    mockMvc.perform(get("/products/{id}", productId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(product.id()))
        .andExpect(jsonPath("$.name").value(product.name()));

    verify(productService).findProductById(productId);
  }

  @Test
  void deleteProduct_whenDifferentId_shouldReturnNoContent() throws Exception {
    Long productId = 50L;

    doNothing().when(productService).deleteProduct(productId);

    mockMvc.perform(delete("/products/{id}", productId))
        .andExpect(status().isNoContent());

    verify(productService).deleteProduct(productId);
  }

  @Test
  void createProduct_whenMinimumValidValues_shouldReturnCreatedStatus() throws Exception {
    Location location = createLocation(0, 0);
    ProductDto productDto = new ProductDto(null, "Abc", new BigDecimal("0.01"), 1, location);
    ProductDto createdProduct = new ProductDto(1L, "Abc", new BigDecimal("0.01"), 1, location);

    when(productService.createProduct(any(ProductDto.class))).thenReturn(createdProduct);

    mockMvc.perform(post("/products")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(productDto)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value(createdProduct.name()))
        .andExpect(jsonPath("$.price").value(createdProduct.price()))
        .andExpect(jsonPath("$.quantity").value(createdProduct.quantity()));

    verify(productService).createProduct(any(ProductDto.class));
  }

  @Test
  void getAllProducts_whenMultipleProducts_shouldReturnAllProducts() throws Exception {
    Location location = createLocation(1, 1);
    List<ProductDto> products = Arrays.asList(
        new ProductDto(1L, "Product1", new BigDecimal("10.00"), 10, location),
        new ProductDto(2L, "Product2", new BigDecimal("20.00"), 20, location),
        new ProductDto(3L, "Product3", new BigDecimal("30.00"), 30, location)
    );

    when(productService.findAllProducts()).thenReturn(products);

    mockMvc.perform(get("/products"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(3));

    verify(productService).findAllProducts();
  }

  @Test
  void createProduct_whenPriceIsNull_shouldReturnBadRequest() throws Exception {
    Location location = createLocation(1, 2);
    ProductDto productDto = new ProductDto(null, "Apple", null, 100, location);

    mockMvc.perform(post("/products")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(productDto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void createProduct_whenQuantityIsNegative_shouldReturnBadRequest() throws Exception {
    Location location = createLocation(1, 2);
    ProductDto productDto = new ProductDto(null, "Apple", new BigDecimal("1.99"), -5, location);

    mockMvc.perform(post("/products")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(productDto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void createProduct_whenLargeQuantity_shouldReturnCreatedStatus() throws Exception {
    Location location = createLocation(1, 1);
    ProductDto productDto = new ProductDto(null, "WaterBottle", new BigDecimal("0.99"), 1000000,
        location);
    ProductDto createdProduct = new ProductDto(1L, "WaterBottle", new BigDecimal("0.99"), 1000000, location);

    when(productService.createProduct(any(ProductDto.class))).thenReturn(createdProduct);

    mockMvc.perform(post("/products")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(productDto)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.quantity").value(createdProduct.quantity()));

    verify(productService).createProduct(any(ProductDto.class));
  }

  @Test
  void createProduct_whenHighPrice_shouldReturnCreatedStatus() throws Exception {
    Location location = createLocation(5, 5);
    ProductDto productDto = new ProductDto(null, "ExpensiveItem", new BigDecimal("999.99"), 10, location);
    ProductDto createdProduct = new ProductDto(1L, "ExpensiveItem", new BigDecimal("999.99"), 10, location);

    when(productService.createProduct(any(ProductDto.class))).thenReturn(createdProduct);

    mockMvc.perform(post("/products")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(productDto)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.price").value(createdProduct.price()));

    verify(productService).createProduct(any(ProductDto.class));
  }
}

