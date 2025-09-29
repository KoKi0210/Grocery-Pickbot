package com.example.groceryPickbot.product.services;

import com.example.groceryPickbot.product.models.Product;
import com.example.groceryPickbot.product.models.ProductDTO;
import com.example.groceryPickbot.product.mappers.ProductMapper;
import com.example.groceryPickbot.product.repositories.ProductRepository;
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
        if (productRepository.existsByName(productDTO.name())) {
            throw new IllegalArgumentException("Product with this name already exists");
        }

        if (productRepository.findByLocation(productDTO.location()).isPresent()){
            throw new IllegalArgumentException("Location is already occupied by another product");
        }

        if (productDTO.location().getX()==0 && productDTO.location().getY() == 0){
            throw new IllegalArgumentException("Location can't be {0:0}");
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
