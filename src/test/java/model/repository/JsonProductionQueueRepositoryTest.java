package model.repository;

import model.entity.ProductionJob;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JsonProductionQueueRepositoryTest {

    @TempDir
    Path tempDir;

    private JsonProductionQueueRepository repo() {
        return new JsonProductionQueueRepository(tempDir.resolve("queue.json").toString());
    }

    @Test
    @DisplayName("큐 저장 후 로드 — 순서 유지")
    void 큐_저장_후_로드_순서_유지() {
        var repo = repo();
        List<ProductionJob> jobs = List.of(
                new ProductionJob("ORD-001", "S-001", 200, 170, 206, 0.8),
                new ProductionJob("ORD-002", "S-002", 80, 50, 61, 1.0),
                new ProductionJob("ORD-003", "S-003", 60, 30, 37, 0.5)
        );
        repo.save(jobs);
        List<ProductionJob> loaded = repo.load();
        assertEquals(3, loaded.size());
        assertEquals("ORD-001", loaded.get(0).getOrderId());
        assertEquals("ORD-002", loaded.get(1).getOrderId());
        assertEquals("ORD-003", loaded.get(2).getOrderId());
    }

    @Test
    @DisplayName("빈 큐 저장 후 로드 — 빈 리스트")
    void 빈_큐_저장_로드() {
        var repo = repo();
        repo.save(List.of());
        assertTrue(repo.load().isEmpty());
    }
}
