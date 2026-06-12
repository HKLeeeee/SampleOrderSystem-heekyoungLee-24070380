package model.repository;

import model.entity.Order;
import model.entity.OrderStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class JsonOrderRepositoryDeleteTest {

    @TempDir
    Path tempDir;

    private JsonOrderRepository repo() {
        return new JsonOrderRepository(tempDir.resolve("orders.json").toString());
    }

    @Test
    @DisplayName("delete — 존재하는 주문 삭제 시 true 반환 + 목록에서 제거")
    void delete_존재하는_주문_삭제() {
        // Arrange
        var repo = repo();
        Order o = new Order("ORD-001", "S-001", "홍길동", 100);
        repo.save(o);

        // Act
        boolean result = repo.delete("ORD-001");

        // Assert
        assertTrue(result);
        assertTrue(repo.findAll().isEmpty());
    }

    @Test
    @DisplayName("delete — 없는 주문 ID 삭제 시 false 반환")
    void delete_없는ID_false반환() {
        // Arrange
        var repo = repo();

        // Act
        boolean result = repo.delete("ORD-NONE");

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("delete — 여러 주문 중 특정 ID만 삭제")
    void delete_여러주문_중_특정ID_삭제() {
        // Arrange
        var repo = repo();
        repo.save(new Order("ORD-001", "S-001", "고객A", 10));
        repo.save(new Order("ORD-002", "S-001", "고객B", 20));

        // Act
        boolean result = repo.delete("ORD-001");

        // Assert
        assertTrue(result);
        assertEquals(1, repo.findAll().size());
        assertEquals("ORD-002", repo.findAll().get(0).getOrderId());
    }
}
