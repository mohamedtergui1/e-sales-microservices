package org.esales.products.service.impl;

import lombok.RequiredArgsConstructor;
import org.esales.products.dto.OrderDTO;
import org.esales.products.dto.OrderItemDTO;
import org.esales.products.exception.NotFoundException;
import org.esales.products.mapper.OrderMapper;
import org.esales.products.model.Order;
import org.esales.products.model.OrderItem;
import org.esales.products.model.Product;
import org.esales.products.repository.OrderRepository;
import org.esales.products.repository.ProductRepository;
import org.esales.products.service.OrderService;
import org.esales.products.service.StockService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final StockService stockService;
    private final OrderMapper orderMapper;

    @Override
    @Transactional
    public OrderDTO createOrder(OrderDTO orderDTO) {
        // Set creation timestamp
        LocalDateTime now = LocalDateTime.now();
        orderDTO.setCreatedAt(now);
        orderDTO.setUpdatedAt(now);

        // Default status to PENDING if not provided
        if (orderDTO.getStatus() == null) {
            orderDTO.setStatus("PENDING");
        }

        // Calculate total order amount
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItemDTO> processedItems = new ArrayList<>();

        for (OrderItemDTO itemDTO : orderDTO.getItems()) {
            // Get product details
            Product product = productRepository.findById(itemDTO.getProductId())
                    .orElseThrow(() -> new NotFoundException("Product not found with ID: " + itemDTO.getProductId()));

            // Process stock and get the cost
            BigDecimal itemCost = stockService.processOrder(itemDTO.getProductId(), itemDTO.getQuantity());

            // Update item details
            itemDTO.setProductName(product.getName());
            itemDTO.setUnitPrice(itemCost.divide(itemDTO.getQuantity(), BigDecimal.ROUND_HALF_UP));
            itemDTO.setSubtotal(itemCost);

            // Add to processed items and total
            processedItems.add(itemDTO);
            totalAmount = totalAmount.add(itemCost);
        }

        // Update DTO with processed items and total
        orderDTO.setItems(processedItems);
        orderDTO.setTotalAmount(totalAmount);

        // Convert to entity and save
        Order order = orderMapper.toEntity(orderDTO);
        Order savedOrder = orderRepository.save(order);

        // Return saved order as DTO
        return orderMapper.toDto(savedOrder);
    }

    @Override
    public Optional<OrderDTO> getOrderById(String id) {
        return orderRepository.findById(id)
                .map(orderMapper::toDto);
    }

    @Override
    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(orderMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderDTO> getOrdersByCustomerId(String customerId) {
        return orderRepository.findByCustomerId(customerId).stream()
                .map(orderMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrderDTO updateOrder(String id, OrderDTO orderDTO) {
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Order not found with ID: " + id));

        // Update timestamp
        orderDTO.setUpdatedAt(LocalDateTime.now());

        // Preserve creation date
        orderDTO.setCreatedAt(existingOrder.getCreatedAt());

        // Update entity from DTO
        orderMapper.updateOrderFromDto(orderDTO, existingOrder);

        // Save updated entity
        Order updatedOrder = orderRepository.save(existingOrder);

        // Return updated entity as DTO
        return orderMapper.toDto(updatedOrder);
    }

    @Override
    public void deleteOrder(String id) {
        if (!orderRepository.existsById(id)) {
            throw new NotFoundException("Order not found with ID: " + id);
        }
        orderRepository.deleteById(id);
    }
}