package org.esales.products.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ProductDTO {

    private String id;

    @NotBlank(message = "Product name is required")
    private String name;

    @NotBlank(message = "Description cannot be empty")
    private String description;

    @NotBlank(message = "Category ID is required")
    private String categoryId; // Store category ID instead of full Category object
}