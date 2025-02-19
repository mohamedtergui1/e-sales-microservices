package org.esales.products.service;

import org.esales.products.dto.StockDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface StockService {

    List<StockDTO> getAllStocks();

    Optional<StockDTO> getStockById(String id);

    StockDTO addStock(StockDTO stockDTO);

    StockDTO updateStock(String id, StockDTO stockDTO);

    void deleteStock(String id);

    BigDecimal processOrder(String productId, BigDecimal orderQuantity);

    Long CountStocks();

}
