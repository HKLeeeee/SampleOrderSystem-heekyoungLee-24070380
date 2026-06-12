package model.repository;

import model.entity.Order;
import model.entity.OrderStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JsonOrderRepositoryTest {

    @TempDir
    Path tempDir;

    private JsonOrderRepository repo() {
        return new JsonOrderRepository(tempDir.resolve("orders.json").toString());
    }

    @Test
    @DisplayName("주문 저장 후 조회")
    void save_thenFindById() {
        var repo = repo();
        Order o = new Order("ORD-20260612-0001", "S-001", "홍길동", 100);
        repo.save(o);
        var found = repo.findById("ORD-20260612-0001");
        assertTrue(found.isPresent());
        assertEquals("홍길동", found.get().getCustomerName());
        assertEquals(OrderStatus.RESERVED, found.get().getStatus());
    }

    @Test
    @DisplayName("상태별 조회 — RESERVED만 반환")
    void findByStatus_returnsMatchingOrders() {
        var repo = repo();
        Order reserved = new Order("ORD-001", "S-001", "고객A", 10);
        Order confirmed = new Order("ORD-002", "S-001", "고객B", 20);
        confirmed.changeStatus(OrderStatus.CONFIRMED);
        repo.save(reserved);
        repo.save(confirmed);

        List<Order> result = repo.findByStatus(OrderStatus.RESERVED);
        assertEquals(1, result.size());
        assertEquals("ORD-001", result.get(0).getOrderId());
    }

    @Test
    @DisplayName("상태 변경 후 저장 재조회")
    void changeStatus_saveAndReload() {
        var repo = repo();
        Order o = new Order("ORD-001", "S-001", "고객A", 10);
        repo.save(o);

        o.changeStatus(OrderStatus.CONFIRMED);
        repo.save(o);

        assertEquals(OrderStatus.CONFIRMED, repo.findById("ORD-001").get().getStatus());
    }
}
