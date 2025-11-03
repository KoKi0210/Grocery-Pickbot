package com.example.grocerypickbot.product.models;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * Data Transfer Object for Product entity.
 */
public record ProductDto(
    Long id,

    @NotEmpty(message = "Product name is required")
    @Size(min = 3, max = 100, message = "The name must be between 2 and 100 characters")
    String name,

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be at least 0.01")
    BigDecimal price,

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity can't be negative or zero")
    Integer quantity,

    @Valid
    @NotNull(message = "Location is required")
    Location location
) {
}

