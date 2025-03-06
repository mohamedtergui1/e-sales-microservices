package org.esales.products.mapper;

import org.esales.products.dto.OrderItemDTO;
import org.esales.products.model.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface OrderItemMapper {

    OrderItemDTO toDto(OrderItem orderItem);

    OrderItem toEntity(OrderItemDTO orderItemDTO);

    void updateOrderItemFromDto(OrderItemDTO orderItemDTO, @MappingTarget OrderItem orderItem);
}