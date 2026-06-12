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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class OrderServiceTest {

    @TempDir
    Path tempDir;

    private OrderService orderService;
    private SampleService sampleService;

    @BeforeEach
    void setUp() {
        SampleRepository sampleRepo = new JsonSampleRepository(tempDir.resolve("samples.json").toString());
        OrderRepository orderRepo = new JsonOrderRepository(tempDir.resolve("orders.json").toString());
        sampleService = new SampleService(sampleRepo);
        orderService = new OrderService(sampleRepo, orderRepo);

        sampleService.register(new Sample("S-001", "SiC 웨이퍼", 0.8, 0.92, 100));
    }

    @Test
    @DisplayName("주문 접수 성공 — RESERVED 상태, 주문번호 형식")
    void placeOrder_success() {
        Order order = orderService.placeOrder("S-001", "홍길동", 50, "20260612");
        assertNotNull(order);
        assertEquals(OrderStatus.RESERVED, order.getStatus());
        assertTrue(order.getOrderId().matches("ORD-20260612-\\d{4}"));
    }

    @Test
    @DisplayName("미등록 시료 주문 거부")
    void placeOrder_unknownSample_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> orderService.placeOrder("S-999", "홍길동", 50, "20260612"));
    }

    @Test
    @DisplayName("같은 날 3회 접수 — 주문번호 중복 없음")
    void orderId_isUnique() {
        Set<String> ids = new HashSet<>();
        ids.add(orderService.placeOrder("S-001", "고객A", 10, "20260612").getOrderId());
        ids.add(orderService.placeOrder("S-001", "고객B", 20, "20260612").getOrderId());
        ids.add(orderService.placeOrder("S-001", "고객C", 30, "20260612").getOrderId());
        assertEquals(3, ids.size());
    }

    @Test
    @DisplayName("접수된 주문 RESERVED 목록 조회")
    void findByStatus_reserved_returnsPlacedOrders() {
        orderService.placeOrder("S-001", "고객A", 10, "20260612");
        orderService.placeOrder("S-001", "고객B", 20, "20260612");
        orderService.placeOrder("S-001", "고객C", 30, "20260612");
        List<Order> reserved = orderService.findByStatus(OrderStatus.RESERVED);
        assertEquals(3, reserved.size());
    }
}
