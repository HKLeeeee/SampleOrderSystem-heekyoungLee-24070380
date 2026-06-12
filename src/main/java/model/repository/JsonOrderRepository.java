package model.repository;

import com.google.gson.reflect.TypeToken;
import model.entity.Order;
import model.entity.OrderStatus;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class JsonOrderRepository extends AbstractJsonRepository implements OrderRepository {

    public JsonOrderRepository(String filePath) {
        super(filePath, new TypeToken<List<Order>>() {}.getType(), "orders.json");
    }

    @Override
    public void save(Order order) {
        Objects.requireNonNull(order);
        List<Order> list = loadAll();
        list.removeIf(o -> o.getOrderId().equals(order.getOrderId()));
        list.add(order);
        persistAll(list);
    }

    @Override
    public List<Order> findAll() { return loadAll(); }

    @Override
    public Optional<Order> findById(String orderId) {
        Objects.requireNonNull(orderId);
        return this.<Order>loadAll().stream().filter(o -> o.getOrderId().equals(orderId)).findFirst();
    }

    @Override
    public boolean delete(String orderId) {
        Objects.requireNonNull(orderId);
        List<Order> list = loadAll();
        boolean removed = list.removeIf(o -> o.getOrderId().equals(orderId));
        if (removed) persistAll(list);
        return removed;
    }

    @Override
    public List<Order> findByStatus(OrderStatus status) {
        Objects.requireNonNull(status);
        return this.<Order>loadAll().stream()
                .filter(o -> o.getStatus() == status)
                .collect(Collectors.toList());
    }
}
