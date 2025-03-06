package org.esales.products.service;

import org.esales.products.dto.OrderDTO;

import java.util.List;
import java.util.Optional;

public interface OrderService {
    /**
     * Create a new order
     * @param orderDTO the order to create
     * @return the created order
     */
    OrderDTO createOrder(OrderDTO orderDTO);

    /**
     * Get an order by its ID
     * @param id the order ID
     * @return the order if found
     */
    Optional<OrderDTO> getOrderById(String id);

    /**
     * Get all orders
     * @return list of all orders
     */
    List<OrderDTO> getAllOrders();

    /**
     * Get orders by customer ID
     * @param customerId the customer ID
     * @return list of orders for the customer
     */
    List<OrderDTO> getOrdersByCustomerId(String customerId);

    /**
     * Update an order
     * @param id the order ID
     * @param orderDTO the updated order data
     * @return the updated order
     */
    OrderDTO updateOrder(String id, OrderDTO orderDTO);

    /**
     * Delete an order
     * @param id the order ID
     */
    void deleteOrder(String id);
}