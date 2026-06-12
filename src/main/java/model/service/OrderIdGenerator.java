package model.service;

import model.entity.Order;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderIdGenerator {

    private final Map<String, Integer> sequenceByDate = new HashMap<>();

    public void initFrom(List<Order> existingOrders) {
        for (Order o : existingOrders) {
            String[] parts = o.getOrderId().split("-");
            if (parts.length == 3 && "ORD".equals(parts[0])) {
                try {
                    int seq = Integer.parseInt(parts[2]);
                    sequenceByDate.merge(parts[1], seq, Math::max);
                } catch (NumberFormatException ignored) {
                }
            }
        }
    }

    public String generate(String date) {
        int seq = sequenceByDate.merge(date, 1, Integer::sum);
        return String.format("ORD-%s-%04d", date, seq);
    }
}
