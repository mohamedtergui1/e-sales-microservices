package org.esales.products.repository;

import org.esales.products.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface ProductRepository extends MongoRepository<Product, String> {
    List<Product> findByCategoryId(String categoryId); // Get products by category
}
