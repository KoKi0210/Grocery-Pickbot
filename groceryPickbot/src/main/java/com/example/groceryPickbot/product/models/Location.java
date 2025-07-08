package com.example.groceryPickbot.product.models;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Embeddable
public class Location {
    @NotNull(message = "Location X is required")
    @Min(value = 0, message = "Location X can't be negative")
    @Column(name = "loc_x",nullable = false)
    private Integer x;

    @NotNull(message = "Location Y is required")
    @Min(value = 0, message = "Location Y can't be negative")
    @Column(name = "loc_y", nullable = false)
    private Integer y;

    public Integer getX() {
        return x;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public Integer getY() {
        return y;
    }

    public void setY(Integer y) {
        this.y = y;
    }
}
