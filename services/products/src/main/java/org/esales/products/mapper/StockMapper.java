package org.esales.products.mapper;

import org.esales.products.dto.StockDTO;
import org.esales.products.model.Stock;
import org.mapstruct.*;

@Mapper (componentModel = "spring")
public interface StockMapper {

    @Mapping(target = "productId", source = "product.id")
    StockDTO toDto(Stock stock);


    Stock toEntity(StockDTO stockDTO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateStockFromDto(StockDTO stockDTO, @MappingTarget Stock stock);
}