package model.repository;

import model.entity.Order;
import model.entity.OrderStatus;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {
    void save(Order order);
    List<Order> findAll();
    Optional<Order> findById(String orderId);
    boolean delete(String orderId);
    List<Order> findByStatus(OrderStatus status);
}
