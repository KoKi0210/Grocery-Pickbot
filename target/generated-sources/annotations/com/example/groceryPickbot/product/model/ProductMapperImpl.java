package com.example.groceryPickbot.product.model;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-06-21T18:08:01+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 22.0.2 (Oracle Corporation)"
)
@Component
public class ProductMapperImpl implements ProductMapper {

    @Override
    public Product toEntity(ProductDTO productDTO) {
        if ( productDTO == null ) {
            return null;
        }

        Product product = new Product();

        product.setLocation( locationToLocation( productDTO.getLocation() ) );
        product.setName( productDTO.getName() );
        if ( productDTO.getPrice() != null ) {
            product.setPrice( productDTO.getPrice() );
        }
        if ( productDTO.getQuantity() != null ) {
            product.setQuantity( productDTO.getQuantity() );
        }

        return product;
    }

    @Override
    public ProductDTO toDTO(Product product) {
        if ( product == null ) {
            return null;
        }

        ProductDTO productDTO = new ProductDTO();

        productDTO.setLocation( locationToLocation1( product.getLocation() ) );
        productDTO.setName( product.getName() );
        productDTO.setPrice( product.getPrice() );
        productDTO.setQuantity( product.getQuantity() );

        return productDTO;
    }

    @Override
    public void updateProductFromDto(ProductDTO productDTO, Product product) {
        if ( productDTO == null ) {
            return;
        }

        if ( productDTO.getName() != null ) {
            product.setName( productDTO.getName() );
        }
        if ( productDTO.getPrice() != null ) {
            product.setPrice( productDTO.getPrice() );
        }
        if ( productDTO.getQuantity() != null ) {
            product.setQuantity( productDTO.getQuantity() );
        }
        if ( productDTO.getLocation() != null ) {
            product.setLocation( productDTO.getLocation() );
        }
    }

    protected Location locationToLocation(Location location) {
        if ( location == null ) {
            return null;
        }

        Location location1 = new Location();

        location1.setX( location.getX() );
        location1.setY( location.getY() );

        return location1;
    }

    protected Location locationToLocation1(Location location) {
        if ( location == null ) {
            return null;
        }

        Location location1 = new Location();

        location1.setX( location.getX() );
        location1.setY( location.getY() );

        return location1;
    }
}
