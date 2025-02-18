package org.esales.products.service.impl;

import lombok.RequiredArgsConstructor;
import org.esales.products.dto.ProductDTO;
import org.esales.products.exception.NotFoundException;
import org.esales.products.mapper.ProductMapper;
import org.esales.products.model.Category;
import org.esales.products.model.Product;
import org.esales.products.repository.CategoryRepository;
import org.esales.products.repository.ProductRepository;
import org.esales.products.service.ProductService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    @Override
    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ProductDTO> getProductById(String id) {
        return productRepository.findById(id)
                .map(productMapper::toDto);
    }

    @Override
    public ProductDTO addProduct(ProductDTO productDTO) {
        Category category = categoryRepository.findById(productDTO.getCategoryId())
                .orElseThrow(() -> new NotFoundException("Category not found with ID: " + productDTO.getCategoryId()));

        Product product = productMapper.toEntity(productDTO);
        product.setCategory(category); // Set category after mapping

        Product savedProduct = productRepository.save(product);
        return productMapper.toDto(savedProduct);
    }

    @Override
    public ProductDTO updateProduct(String id, ProductDTO productDTO) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found with ID: " + id));

        productMapper.updateProductFromDto(productDTO, existingProduct); // Use MapStruct for updates

        if (productDTO.getCategoryId() != null) {
            Category category = categoryRepository.findById(productDTO.getCategoryId())
                    .orElseThrow(() -> new NotFoundException("Category not found with ID: " + productDTO.getCategoryId()));
            existingProduct.setCategory(category);
        }

        Product updatedProduct = productRepository.save(existingProduct);
        return productMapper.toDto(updatedProduct);
    }

    @Override
    public void deleteProduct(String id) {
        if (!productRepository.existsById(id)) {
            throw new NotFoundException("Product not found with ID: " + id);
        }
        productRepository.deleteById(id);
    }
}
