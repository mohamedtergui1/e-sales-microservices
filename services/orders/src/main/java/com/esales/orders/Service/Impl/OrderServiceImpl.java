package com.esales.orders.Service.Impl;

import com.esales.orders.Entity.Order;
import com.esales.orders.Entity.OrderItem;
import com.esales.orders.Exception.OrderProcessingException;
import com.esales.orders.Feign.OrdersInterface;
import com.esales.orders.Service.OrderService;
import com.esales.orders.Repository.OrderItemRepository;
import com.esales.orders.Repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrdersInterface ordersInterface;

    @Autowired
    public OrderServiceImpl(
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            OrdersInterface ordersInterface) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.ordersInterface = ordersInterface;
    }

    @Override
    @Transactional
    public Order createOrder(Order order) {
        order = orderRepository.save(order);

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        try {
            for (OrderItem item : orderItemRepository.findByOrder(order)) {
                BigDecimal cost = ordersInterface.processOrder(
                        item.getProduct().getId(),
                        item.getQuantity()
                );

                BigDecimal unitPrice = cost.divide(new BigDecimal(String.valueOf(item.getQuantity())), 2, BigDecimal.ROUND_HALF_UP);
                item.setPrice(unitPrice);

                orderItems.add(item);
            }

            orderItemRepository.saveAll(orderItems);

        } catch (Exception e) {
            throw new OrderProcessingException("Failed to process order: " + e.getMessage());
        }

        order.setTotalAmount(totalAmount);
        return orderRepository.save(order);
    }
}