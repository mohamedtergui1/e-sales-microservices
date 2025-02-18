package org.esales.products.mapper;

import org.esales.products.dto.ProductDTO;
import org.esales.products.model.Product;
import org.mapstruct.*;

@Mapper (componentModel = "spring")
public interface ProductMapper {
    ProductDTO toDto(Product product);
    Product toEntity(ProductDTO productDTO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateProductFromDto(ProductDTO productDTO, @MappingTarget Product product);
}
