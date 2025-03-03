package org.esales.products.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StatisticsDTO {
    private long totalCategories;
    private long totalProducts;
    private long totalStocks;
}
