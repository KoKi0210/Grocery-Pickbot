package com.example.groceryPickbot.product.mapper;

import com.example.groceryPickbot.product.model.Product;
import com.example.groceryPickbot.product.model.ProductDTO;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    Product toEntity(ProductDTO productDTO);

    ProductDTO toDTO(Product product);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateProductFromDto(ProductDTO productDTO, @MappingTarget Product product);
}
