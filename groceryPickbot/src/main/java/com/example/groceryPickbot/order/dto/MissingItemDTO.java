package com.example.groceryPickbot.order.dto;

public class MissingItemDTO {
    private String productName;
    private int requested;
    private int available;

    public MissingItemDTO(String productName, int requested, int available) {
        this.productName = productName;
        this.requested = requested;
        this.available = available;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getRequested() {
        return requested;
    }

    public void setRequested(int requested) {
        this.requested = requested;
    }

    public int getAvailable() {
        return available;
    }

    public void setAvailable(int available) {
        this.available = available;
    }
}
