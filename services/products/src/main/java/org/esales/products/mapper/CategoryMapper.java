package org.esales.products.mapper;

import org.esales.products.model.Category;
import org.esales.products.dto.CategoryDTO;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryDTO toDto(Category category);
    Category toEntity(CategoryDTO categoryDTO);
}