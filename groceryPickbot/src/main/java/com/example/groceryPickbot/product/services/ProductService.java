package com.example.groceryPickbot.product.services;

import com.example.groceryPickbot.product.model.ProductDTO;

import java.util.List;

public interface ProductService {

    ProductDTO createProduct(ProductDTO product);

    List<ProductDTO> findAllProducts();

    ProductDTO findProductById(Long id);

    ProductDTO updateProduct(Long id, ProductDTO product);

    void deleteProduct(Long id);

}
