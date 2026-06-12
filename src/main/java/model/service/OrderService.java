package model.service;

import model.entity.Order;
import model.entity.OrderStatus;
import model.repository.OrderRepository;
import model.repository.SampleRepository;

import java.util.List;

public class OrderService {

    private final SampleRepository sampleRepository;
    private final OrderRepository orderRepository;
    private final OrderIdGenerator idGenerator = new OrderIdGenerator();

    public OrderService(SampleRepository sampleRepository, OrderRepository orderRepository) {
        this.sampleRepository = sampleRepository;
        this.orderRepository = orderRepository;
    }

    public Order placeOrder(String sampleId, String customerName, int quantity, String date) {
        if (sampleRepository.findById(sampleId).isEmpty()) {
            throw new IllegalArgumentException("등록되지 않은 시료 ID입니다: " + sampleId);
        }
        String orderId = idGenerator.generate(date);
        Order order = new Order(orderId, sampleId, customerName, quantity);
        orderRepository.save(order);
        return order;
    }

    public List<Order> findByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status);
    }
}
