package com.example.grocerypickbot.product.mappers;

import com.example.grocerypickbot.product.models.Product;
import com.example.grocerypickbot.product.models.ProductDto;
import com.example.grocerypickbot.route.services.RouteServiceImpl.ProductInfo;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
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

  /**
   * Converts a Product entity to a ProductInfo record.
   *
   * @param product the Product entity to convert
   * @return the corresponding ProductInfo record
   */
  @Mapping(target = "name", source = "name")
  @Mapping(target = "location", expression =
      "java(new RouteServiceImpl.Location(product.getLocation().getX(),"
          + " product.getLocation().getY()))")
  ProductInfo toProductInfo(Product product);
}
