package org.esales.products.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.esales.products.model.Category;
import java.util.Optional;

public interface CategoryRepository extends MongoRepository<Category, String> {
}
