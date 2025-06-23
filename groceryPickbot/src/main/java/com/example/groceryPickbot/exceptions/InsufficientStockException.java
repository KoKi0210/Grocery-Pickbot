package com.example.groceryPickbot.exceptions;

public class InsufficientStockException extends RuntimeException{
    public InsufficientStockException(String productName, int requested, int available) {
        super(String.format(
                "Not enough availability for %s: requested %d, available %d",
                productName, requested, available
        ));
    }
}
