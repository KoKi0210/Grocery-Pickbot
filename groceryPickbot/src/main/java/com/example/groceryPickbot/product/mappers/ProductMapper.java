package com.example.groceryPickbot.product.mappers;

import com.example.groceryPickbot.product.models.Product;
import com.example.groceryPickbot.product.models.ProductDTO;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    Product toEntity(ProductDTO productDTO);

    ProductDTO toDTO(Product product);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateProductFromDto(ProductDTO productDTO, @MappingTarget Product product);
}
