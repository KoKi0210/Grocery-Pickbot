package com.example.groceryPickbot.product.model;

import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "location.x", source = "location.x")
    @Mapping(target = "location.y", source = "location.y")
    Product toEntity(ProductDTO productDTO);

    @Mapping(target = "location.x", source = "location.x")
    @Mapping(target = "location.y", source = "location.y")
    ProductDTO toDTO(Product product);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateProductFromDto(ProductDTO productDTO, @MappingTarget Product product);
}
