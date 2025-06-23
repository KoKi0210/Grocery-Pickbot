package com.example.groceryPickbot.exceptions;

import com.example.groceryPickbot.product.repository.ProductRepository;

public class ProductNotFoundException extends RuntimeException{

    public ProductNotFoundException(Long id){
        super("Product with id: "+ id+ " not found");
    }
}
