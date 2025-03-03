package org.esales.products.controller;

import lombok.RequiredArgsConstructor;
import org.esales.products.dto.StockDTO;
import org.esales.products.service.StockService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/products/stocks")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    @GetMapping
    public ResponseEntity<List<StockDTO>> getAllStocks() {
        return ResponseEntity.ok(stockService.getAllStocks());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StockDTO> getStockById(@PathVariable String id) {
        Optional<StockDTO> stockDTO = stockService.getStockById(id);
        return stockDTO.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<StockDTO> addStock(@RequestBody StockDTO stockDTO) {
        return ResponseEntity.ok(stockService.addStock(stockDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StockDTO> updateStock(@PathVariable String id, @RequestBody StockDTO stockDTO) {
        return ResponseEntity.ok(stockService.updateStock(id, stockDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteStock(@PathVariable String id) {
        stockService.deleteStock(id);
        return ResponseEntity.ok("stock deleted successfully");
    }

    @PostMapping("/{id}/{quantity}")
    public BigDecimal processOrder(@PathVariable String id, @PathVariable BigDecimal quantity) {
        return stockService.processOrder(id, quantity);
    }
}
