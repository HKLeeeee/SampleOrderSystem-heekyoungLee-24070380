package model.service;

import model.entity.Order;
import model.entity.OrderStatus;
import model.entity.ProductionJob;
import model.entity.Sample;
import model.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ProductionServiceTest {

    @TempDir
    Path tempDir;

    private ProductionService productionService;
    private ProductionQueue queue;
    private SampleRepository sampleRepo;
    private OrderRepository orderRepo;

    @BeforeEach
    void setUp() {
        sampleRepo = new JsonSampleRepository(tempDir.resolve("samples.json").toString());
        orderRepo = new JsonOrderRepository(tempDir.resolve("orders.json").toString());
        ProductionQueueRepository queueRepo = new JsonProductionQueueRepository(tempDir.resolve("queue.json").toString());
        queue = new ProductionQueue();
        productionService = new ProductionService(queue, sampleRepo, orderRepo, queueRepo);
    }

    private Sample sample(int stock) {
        Sample s = new Sample("S-001", "SiC 웨이퍼", 0.8, 0.92, stock);
        sampleRepo.save(s);
        return s;
    }

    private Order producingOrder() {
        Order o = new Order("ORD-001", "S-001", "홍길동", 200);
        o.changeStatus(OrderStatus.PRODUCING);
        orderRepo.save(o);
        return o;
    }

    private ProductionJob job(int actualQty) {
        return new ProductionJob("ORD-001", "S-001", 200, 170, actualQty, 0.8);
    }

    @Test
    @DisplayName("생산 완료 — 재고 실생산량만큼 증가")
    void 생산완료_재고_증가() {
        Sample s = sample(30);
        Order o = producingOrder();
        queue.enqueue(job(206));
        productionService.completeCurrentProduction();
        assertEquals(236, sampleRepo.findById("S-001").get().getStock());
    }

    @Test
    @DisplayName("생산 완료 — 주문 CONFIRMED")
    void 생산완료_주문_CONFIRMED() {
        sample(30);
        Order o = producingOrder();
        queue.enqueue(job(206));
        productionService.completeCurrentProduction();
        assertEquals(OrderStatus.CONFIRMED, orderRepo.findById("ORD-001").get().getStatus());
    }

    @Test
    @DisplayName("생산 완료 — 큐에 다음 job 자동 진입")
    void 생산완료_큐_다음진입() {
        sample(30);
        producingOrder();

        Order o2 = new Order("ORD-002", "S-001", "고객B", 50);
        o2.changeStatus(OrderStatus.PRODUCING);
        orderRepo.save(o2);

        queue.enqueue(job(206));
        queue.enqueue(new ProductionJob("ORD-002", "S-001", 50, 20, 25, 0.8));
        productionService.completeCurrentProduction();
        assertEquals("ORD-002", queue.getCurrentJob().getOrderId());
    }

    @Test
    @DisplayName("생산 완료 — 큐 비면 IDLE")
    void 생산완료_큐_비면_IDLE() {
        sample(30);
        producingOrder();
        queue.enqueue(job(206));
        productionService.completeCurrentProduction();
        assertNull(queue.getCurrentJob());
    }

    @Test
    @DisplayName("예상 완료 시각 계산 — startTime + totalTime(분)")
    void 예상완료시각_계산() {
        LocalDateTime start = LocalDateTime.of(2026, 6, 12, 9, 0);
        ProductionJob j = new ProductionJob("ORD-001", "S-001", 200, 170, 206, 0.8);
        LocalDateTime expected = productionService.getExpectedEndTime(j, start);
        double totalMinutes = 0.8 * 206;
        long seconds = (long) (totalMinutes * 60);
        assertEquals(start.plusSeconds(seconds), expected);
    }
}
