package com.example.grocerypickbot.product.models;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Represents the location of a product in the store.
 */
@Embeddable
public class Location {
  @NotNull(message = "Location X is required")
  @Min(value = 0, message = "Location X can't be negative")
  @Column(name = "loc_x", nullable = false)
  private Integer cordX;

  @NotNull(message = "Location Y is required")
  @Min(value = 0, message = "Location Y can't be negative")
  @Column(name = "loc_y", nullable = false)
  private Integer cordY;

  public Integer getX() {
    return cordX;
  }

  public void setX(Integer x) {
    this.cordX = x;
  }

  public Integer getY() {
    return cordY;
  }

  public void setY(Integer y) {
    this.cordY = y;
  }
}


