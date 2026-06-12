package model.repository;

import com.google.gson.reflect.TypeToken;
import model.entity.Order;
import model.entity.OrderStatus;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class JsonOrderRepository implements OrderRepository {

    private static final Type LIST_TYPE = new TypeToken<List<Order>>() {}.getType();

    private final Path filePath;

    public JsonOrderRepository(String filePath) {
        this.filePath = Paths.get(filePath);
        ensureFileExists();
    }

    private void ensureFileExists() {
        try {
            if (filePath.getParent() != null) Files.createDirectories(filePath.getParent());
            if (!Files.exists(filePath)) Files.writeString(filePath, "[]", StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("저장소 파일 초기화 실패: " + filePath, e);
        }
    }

    private List<Order> loadAll() {
        try {
            String json = Files.readString(filePath, StandardCharsets.UTF_8).trim();
            if (json.isEmpty()) return new ArrayList<>();
            List<Order> list = GsonConfig.INSTANCE.fromJson(json, LIST_TYPE);
            return list != null ? new ArrayList<>(list) : new ArrayList<>();
        } catch (IOException e) {
            throw new UncheckedIOException("파일 읽기 실패: " + filePath, e);
        } catch (Exception e) {
            System.err.println("[경고] orders.json 파싱 오류 — 빈 목록으로 초기화. 원인: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private void persistAll(List<Order> orders) {
        try {
            Files.writeString(filePath, GsonConfig.INSTANCE.toJson(orders), StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new UncheckedIOException("파일 쓰기 실패: " + filePath, e);
        }
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
        return loadAll().stream().filter(o -> o.getOrderId().equals(orderId)).findFirst();
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
        return loadAll().stream()
                .filter(o -> o.getStatus() == status)
                .collect(Collectors.toList());
    }
}
