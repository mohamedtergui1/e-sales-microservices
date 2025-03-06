package org.esales.products.mapper;

import org.esales.products.dto.OrderDTO;
import org.esales.products.dto.OrderItemDTO;
import org.esales.products.model.Order;
import org.esales.products.model.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {OrderItemMapper.class})
public interface OrderMapper {

    OrderDTO toDto(Order order);

    Order toEntity(OrderDTO orderDTO);

    void updateOrderFromDto(OrderDTO orderDTO, @MappingTarget Order order);
}