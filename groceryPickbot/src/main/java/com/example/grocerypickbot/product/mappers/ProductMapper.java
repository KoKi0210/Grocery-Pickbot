package com.example.grocerypickbot.product.mappers;

import com.example.grocerypickbot.product.models.Product;
import com.example.grocerypickbot.product.models.ProductDto;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * Mapper interface for converting between Product and ProductDto objects.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProductMapper {

  /**
   * Converts a ProductDto to a Product entity.
   *
   * @param productDto the ProductDto to convert
   * @return the corresponding Product entity
   */
  Product toEntity(ProductDto productDto);

  /**
   * Converts a Product entity to a ProductDto.
   *
   * @param product the Product entity to convert
   * @return the corresponding ProductDto
   */
  ProductDto toDto(Product product);

  /**
   * Updates an existing Product entity with values from a ProductDto.
   * Only non-null properties from the ProductDto will be copied to the Product entity.
   *
   * @param productDto the ProductDto containing updated values
   * @param product    the existing Product entity to be updated
   */
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateProductFromDto(ProductDto productDto, @MappingTarget Product product);
}
