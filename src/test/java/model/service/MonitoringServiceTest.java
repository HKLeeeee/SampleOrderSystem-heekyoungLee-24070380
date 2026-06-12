package model.service;

import model.entity.Order;
import model.entity.OrderStatus;
import model.entity.Sample;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MonitoringServiceTest {

    private MonitoringService service;

    @BeforeEach
    void setUp() {
        service = new MonitoringService();
    }

    @Test
    @DisplayName("상태별 집계 — REJECTED 제외")
    void 상태별_집계_REJECTED_제외() {
        Order r1 = new Order("O-1", "S-001", "A", 10);
        Order r2 = new Order("O-2", "S-001", "B", 10);
        Order c1 = new Order("O-3", "S-001", "C", 10);
        c1.changeStatus(OrderStatus.CONFIRMED);
        Order rj = new Order("O-4", "S-001", "D", 10);
        rj.changeStatus(OrderStatus.REJECTED);

        Map<OrderStatus, Long> counts = service.getOrderCountByStatus(List.of(r1, r2, c1, rj));
        assertEquals(2L, counts.getOrDefault(OrderStatus.RESERVED, 0L));
        assertEquals(1L, counts.getOrDefault(OrderStatus.CONFIRMED, 0L));
        assertFalse(counts.containsKey(OrderStatus.REJECTED));
    }

    @Test
    @DisplayName("재고 0 — 고갈")
    void 재고_고갈() {
        Sample s = new Sample("S-001", "A", 0.8, 0.92, 0);
        assertEquals("고갈", service.getStockStatus(s, List.of()));
    }

    @Test
    @DisplayName("재고 부족 — PRODUCING 주문 수요 초과")
    void 재고_부족() {
        Sample s = new Sample("S-001", "A", 0.8, 0.92, 30);
        Order o = new Order("O-1", "S-001", "고객", 200);
        o.changeStatus(OrderStatus.PRODUCING);
        assertEquals("부족", service.getStockStatus(s, List.of(o)));
    }

    @Test
    @DisplayName("재고 여유 — CONFIRMED 주문 수요 이하")
    void 재고_여유() {
        Sample s = new Sample("S-001", "A", 0.8, 0.92, 200);
        Order o = new Order("O-1", "S-001", "고객", 50);
        o.changeStatus(OrderStatus.CONFIRMED);
        assertEquals("여유", service.getStockStatus(s, List.of(o)));
    }

    @Test
    @DisplayName("주문 없음 — 여유")
    void 주문_없음_여유() {
        Sample s = new Sample("S-001", "A", 0.8, 0.92, 100);
        assertEquals("여유", service.getStockStatus(s, List.of()));
    }

    @Test
    @DisplayName("재고 부족 — RESERVED 주문 수요도 포함")
    void 재고_부족_RESERVED_포함() {
        Sample s = new Sample("S-001", "A", 0.8, 0.92, 30);
        Order o = new Order("O-1", "S-001", "고객", 200); // RESERVED 상태 (기본값)
        assertEquals("부족", service.getStockStatus(s, List.of(o)));
    }

    @Test
    @DisplayName("재고 여유 — RELEASE 주문은 수요에서 제외")
    void 재고_여유_RELEASE_제외() {
        Sample s = new Sample("S-001", "A", 0.8, 0.92, 200);
        Order o = new Order("O-1", "S-001", "고객", 500);
        o.changeStatus(OrderStatus.CONFIRMED);
        o.changeStatus(OrderStatus.RELEASE);
        assertEquals("여유", service.getStockStatus(s, List.of(o)));
    }

    @Test
    @DisplayName("재고 부족 — REJECTED 주문은 수요에서 제외")
    void 재고_부족_REJECTED_제외() {
        Sample s = new Sample("S-001", "A", 0.8, 0.92, 200);
        Order rejected = new Order("O-1", "S-001", "고객", 500);
        rejected.changeStatus(OrderStatus.REJECTED);
        Order normal = new Order("O-2", "S-001", "고객", 100);
        normal.changeStatus(OrderStatus.CONFIRMED);
        assertEquals("여유", service.getStockStatus(s, List.of(rejected, normal)));
    }
}
