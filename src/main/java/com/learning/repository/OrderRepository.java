package com.learning.repository;

import com.learning.model.Order;
import java.util.List;
import java.util.Optional;

/**
 * OrderRepository - Database layer for orders
 */
public interface OrderRepository {

    Order save(Order order);

    Optional<Order> findById(Long id);

    List<Order> findByUserId(Long userId);

    List<Order> findByStatus(Order.OrderStatus status);

    void deleteById(Long id);

    long count();
}
