package com.example.groceryPickbot.product.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ProductDTO(
        Long id,

        @NotBlank(message = "Product name is required")
        @Size(min = 3, max = 100, message = "The name must be between 2 and 100 characters")
        String name,

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.01", message = "Price must be at least 0.01")
        BigDecimal price,

        @NotNull(message = "Quantity is required")
        @Min(value = 1, message = "Quantity can't be negative")
        Integer quantity,

        @Valid
        @NotNull(message = "Location is required")
        Location location
) {}

