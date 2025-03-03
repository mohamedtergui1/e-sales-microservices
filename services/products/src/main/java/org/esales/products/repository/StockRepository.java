package org.esales.products.repository;

import org.esales.products.model.Stock;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface StockRepository extends MongoRepository <Stock, String> {
    List<Stock> findByProductIdOrderByIdAsc(String productId);
}
