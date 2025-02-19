package org.esales.products.service.impl;

import lombok.RequiredArgsConstructor;
import org.esales.products.dto.StockDTO;
import org.esales.products.exception.NotFoundException;
import org.esales.products.mapper.StockMapper;
import org.esales.products.model.Product;
import org.esales.products.model.Stock;
import org.esales.products.repository.ProductRepository;
import org.esales.products.repository.StockRepository;
import org.esales.products.service.StockService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {

    private final StockRepository stockRepository;
    private final ProductRepository productRepository;
    private final StockMapper stockMapper;

    @Override
    public List<StockDTO> getAllStocks() {
        return stockRepository.findAll()
                .stream()
                .map(stockMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<StockDTO> getStockById(String id) {
        return stockRepository.findById(id)
                .map(stockMapper::toDto);
    }

    @Override
    public StockDTO addStock(StockDTO stockDTO) {
        Product product = productRepository.findById(stockDTO.getProductId())
                .orElseThrow(() -> new NotFoundException("Product not found with ID: " + stockDTO.getProductId()));

        Stock stock = stockMapper.toEntity(stockDTO);
        stock.setProduct(product); // Set product reference

        Stock savedStock = stockRepository.save(stock);
        StockDTO responseDto = stockMapper.toDto(savedStock);
        responseDto.setProductId(product.getId()); // Ensure productId is set in response
        return responseDto;
    }

    @Override
    public StockDTO updateStock(String id, StockDTO stockDTO) {
        Stock existingStock = stockRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Stock not found with ID: " + id));

        stockMapper.updateStockFromDto(stockDTO, existingStock);

        if (stockDTO.getProductId() != null) {
            Product product = productRepository.findById(stockDTO.getProductId())
                    .orElseThrow(() -> new NotFoundException("Product not found with ID: " + stockDTO.getProductId()));
            existingStock.setProduct(product);
        }

        Stock updatedStock = stockRepository.save(existingStock);
        StockDTO responseDto = stockMapper.toDto(updatedStock);
        responseDto.setProductId(updatedStock.getProduct().getId()); // Ensure productId is set in response
        return responseDto;
    }

    @Override
    public void deleteStock(String id) {
        if (!stockRepository.existsById(id)) {
            throw new NotFoundException("Stock not found with ID: " + id);
        }
        stockRepository.deleteById(id);
    }

    @Transactional
    @Override
    public BigDecimal processOrder(String productId, BigDecimal orderQuantity) {
        List<Stock> stocks = stockRepository.findByProductIdOrderByIdAsc(productId);

        if (stocks.isEmpty()) {
            throw new NotFoundException("No stock available for product ID: " + productId);
        }

        BigDecimal remainingQuantity = orderQuantity;
        BigDecimal totalCost = BigDecimal.ZERO;

        for (Stock stock : stocks) {
            if (remainingQuantity.compareTo(BigDecimal.ZERO) <= 0) break;

            BigDecimal availableQuantity = stock.getQuantity();
            BigDecimal usedQuantity = remainingQuantity.min(availableQuantity); // Take the min to avoid over-consuming stock

            // Calculate cost for the used quantity
            BigDecimal cost = usedQuantity.multiply(stock.getPrice());
            totalCost = totalCost.add(cost);

            // Update stock quantity
            stock.setQuantity(availableQuantity.subtract(usedQuantity));
            stockRepository.save(stock);

            // Reduce remaining quantity
            remainingQuantity = remainingQuantity.subtract(usedQuantity);
        }

        if (remainingQuantity.compareTo(BigDecimal.ZERO) > 0) {
            throw new NotFoundException("Not enough stock available for product ID: " + productId);
        }

        return totalCost;
    }

    @Override
    public Long CountStocks() {
        stockRepository.count();
        return stockRepository.count();
    }
}
