package org.esales.products.service;

import org.esales.products.dto.ProductDTO;

import java.util.List;
import java.util.Optional;

public interface ProductService {
    List<ProductDTO> getAllProducts();
    Optional<ProductDTO> getProductById(String id);
    ProductDTO addProduct(ProductDTO productDTO);
    ProductDTO updateProduct(String id, ProductDTO productDTO);
    void deleteProduct(String id);
}
