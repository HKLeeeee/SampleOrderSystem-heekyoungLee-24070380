package model.service;

import model.entity.Order;
import model.entity.OrderStatus;
import model.entity.Sample;
import model.repository.JsonOrderRepository;
import model.repository.JsonSampleRepository;
import model.repository.OrderRepository;
import model.repository.SampleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class ReleaseServiceTest {

    @TempDir
    Path tempDir;

    private ReleaseService releaseService;
    private SampleRepository sampleRepo;
    private OrderRepository orderRepo;

    @BeforeEach
    void setUp() {
        sampleRepo = new JsonSampleRepository(tempDir.resolve("samples.json").toString());
        orderRepo = new JsonOrderRepository(tempDir.resolve("orders.json").toString());
        releaseService = new ReleaseService(sampleRepo, orderRepo);
    }

    private Sample sample(int stock) {
        Sample s = new Sample("S-001", "SiC 웨이퍼", 0.8, 0.92, stock);
        sampleRepo.save(s);
        return s;
    }

    private Order confirmedOrder(int qty) {
        Order o = new Order("ORD-001", "S-001", "홍길동", qty);
        o.changeStatus(OrderStatus.CONFIRMED);
        orderRepo.save(o);
        return o;
    }

    @Test
    @DisplayName("출고 성공 — 재고 차감, 상태 RELEASE")
    void 출고_성공_재고_차감() {
        Sample s = sample(100);
        Order o = confirmedOrder(50);
        releaseService.release(o, s);
        assertEquals(50, sampleRepo.findById("S-001").get().getStock());
        assertEquals(OrderStatus.RELEASE, orderRepo.findById("ORD-001").get().getStatus());
    }

    @Test
    @DisplayName("출고 시 재고 부족 — IllegalStateException")
    void 출고_재고_부족_차단() {
        Sample s = sample(100);
        Order o = confirmedOrder(200);
        assertThrows(IllegalStateException.class, () -> releaseService.release(o, s));
    }

    @Test
    @DisplayName("비CONFIRMED 주문 출고 거부 — IllegalStateException")
    void 비CONFIRMED_출고_거부() {
        Sample s = sample(100);
        Order o = new Order("ORD-001", "S-001", "홍길동", 50);
        assertThrows(IllegalStateException.class, () -> releaseService.release(o, s));
    }
}
