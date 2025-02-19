package org.esales.products.controller;

import lombok.RequiredArgsConstructor;
import org.esales.products.dto.StatisticsDTO;
import org.esales.products.service.CategoryService;
import org.esales.products.service.ProductService;
import org.esales.products.service.StockService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final StockService stockService;

    @GetMapping("/count")
    public ResponseEntity<StatisticsDTO> getStatistics() {
        long totalCategories = categoryService.countCategories();
        long totalProducts = productService.countProducts();
        long totalStocks = stockService.CountStocks();

        StatisticsDTO statistics = new StatisticsDTO(totalCategories, totalProducts, totalStocks);
        return ResponseEntity.ok(statistics);
    }
}
