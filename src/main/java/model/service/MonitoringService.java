package model.service;

import model.entity.Order;
import model.entity.OrderStatus;
import model.entity.Sample;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MonitoringService {

    public Map<OrderStatus, Long> getOrderCountByStatus(List<Order> orders) {
        return orders.stream()
                .filter(o -> o.getStatus() != OrderStatus.REJECTED)
                .collect(Collectors.groupingBy(Order::getStatus, Collectors.counting()));
    }

    public String getStockStatus(Sample sample, List<Order> ordersForSample) {
        if (sample.getStock() == 0) return "고갈";

        int demand = ordersForSample.stream()
                .filter(o -> o.getStatus() == OrderStatus.RESERVED
                          || o.getStatus() == OrderStatus.PRODUCING
                          || o.getStatus() == OrderStatus.CONFIRMED)
                .mapToInt(Order::getQuantity)
                .sum();

        return sample.getStock() >= demand ? "여유" : "부족";
    }
}
