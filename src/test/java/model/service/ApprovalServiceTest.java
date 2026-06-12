package model.service;

import model.entity.Order;
import model.entity.OrderStatus;
import model.entity.ProductionJob;
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

class ApprovalServiceTest {

    @TempDir
    Path tempDir;

    private ApprovalService approvalService;
    private ProductionQueue queue;
    private OrderRepository orderRepo;

    @BeforeEach
    void setUp() {
        SampleRepository sampleRepo = new JsonSampleRepository(tempDir.resolve("samples.json").toString());
        orderRepo = new JsonOrderRepository(tempDir.resolve("orders.json").toString());
        queue = new ProductionQueue();
        approvalService = new ApprovalService(orderRepo, queue);
    }

    private Sample sampleWith(int stock) {
        return new Sample("S-001", "SiC 웨이퍼", 0.8, 0.92, stock);
    }

    private Order reservedOrder(int qty) {
        return new Order("ORD-001", "S-001", "홍길동", qty);
    }

    @Test
    @DisplayName("승인 — 재고 충분 시 CONFIRMED")
    void 승인_재고충분_CONFIRMED() {
        Order order = reservedOrder(50);
        approvalService.approve(order, sampleWith(100));
        assertEquals(OrderStatus.CONFIRMED, order.getStatus());
        assertNull(queue.getCurrentJob());
    }

    @Test
    @DisplayName("승인 — 재고 부족 시 PRODUCING + 큐 등록")
    void 승인_재고부족_PRODUCING() {
        Order order = reservedOrder(200);
        approvalService.approve(order, sampleWith(30));
        assertEquals(OrderStatus.PRODUCING, order.getStatus());
        assertNotNull(queue.getCurrentJob());
    }

    @Test
    @DisplayName("승인 — 부족 시 큐 job 값 검증 (부족분=170, 실생산량=206)")
    void 승인_재고부족_큐_job_검증() {
        Order order = reservedOrder(200);
        approvalService.approve(order, sampleWith(30));
        ProductionJob job = queue.getCurrentJob();
        assertEquals(170, job.getShortage());
        assertEquals(206, job.getActualQty());
    }

    @Test
    @DisplayName("거절 — REJECTED")
    void 거절_REJECTED() {
        Order order = reservedOrder(100);
        approvalService.reject(order);
        assertEquals(OrderStatus.REJECTED, order.getStatus());
    }

    @Test
    @DisplayName("비RESERVED 주문 승인 시도 — IllegalStateException")
    void 비RESERVED_승인_거부() {
        Order order = reservedOrder(50);
        order.changeStatus(OrderStatus.CONFIRMED);
        assertThrows(IllegalStateException.class,
                () -> approvalService.approve(order, sampleWith(100)));
    }

    @Test
    @DisplayName("비RESERVED 주문 거절 시도 — IllegalStateException")
    void 비RESERVED_거절_거부() {
        Order order = reservedOrder(50);
        order.changeStatus(OrderStatus.CONFIRMED);
        assertThrows(IllegalStateException.class,
                () -> approvalService.reject(order));
    }
}
