package com.example.groceryPickbot.product.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

public class ProductDTO {
    @NotBlank(message = "Product name is required")
    @Size(min = 3, max = 100, message = "The name must be between 2 and 100 characters")
    private String name;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be at least 0.01")
    private Double price;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity can't be negative")
    private Integer quantity;

    @Valid
    @NotNull(message = "Location is required")
    private Location location;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
