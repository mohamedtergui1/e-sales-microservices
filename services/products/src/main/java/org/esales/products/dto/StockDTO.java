package org.esales.products.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class StockDTO {

    private String id;

    @NotBlank(message = "Description cannot be empty")
    private String description;

    @NotNull(message = "Price is required")
    private BigDecimal price;

    @NotNull(message ="Quantity is required")
    private BigDecimal quantity;

    @NotBlank(message = "Product ID is required")
    private String ProductId;
}