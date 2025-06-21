package com.example.groceryPickbot.product.services;

import com.example.groceryPickbot.product.model.Product;
import com.example.groceryPickbot.product.model.ProductDTO;
import com.example.groceryPickbot.product.model.ProductMapper;
import com.example.groceryPickbot.product.repository.ProductRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService{

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductServiceImpl(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    @Override
    public ProductDTO createProduct(@Valid ProductDTO productDTO){
        if (productRepository.existsByName(productDTO.getName())) {
            throw new IllegalArgumentException("Product with this name already exists");
        }

        Product product = productMapper.toEntity(productDTO);
        Product savedProduct = productRepository.save(product);
        return productMapper.toDTO(savedProduct);
    }

    @Override
    public List<ProductDTO> findAllProducts(){
        return productRepository.findAll().stream()
                .map(productMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ProductDTO findProductById(Long id) {
        return productRepository.findById(id)
                .map(productMapper::toDTO)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
    }

    @Override
    public ProductDTO updateProduct(Long id,@Valid ProductDTO product) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        productMapper.updateProductFromDto(product, existingProduct);
        Product updatedProduct = productRepository.save(existingProduct);
        return productMapper.toDTO(updatedProduct);
    }

    @Override
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)){
            throw new IllegalArgumentException("Product not found");
        }
        productRepository.deleteById(id);
    }

}
