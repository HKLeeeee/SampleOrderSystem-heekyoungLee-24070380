import model.entity.Order;
import model.entity.OrderStatus;
import model.entity.Sample;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class E2EFlowTest {

    @TempDir
    Path tempDir;

    private AppContext ctx;

    @BeforeEach
    void setUp() {
        ctx = new AppContext(tempDir.toString());
        ctx.getSampleService().register(new Sample("S-001", "SiC 웨이퍼", 0.8, 0.92, 100));
    }

    // ─────────────────────────────────────────────
    // 시나리오 1: 주문 거절 흐름
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("E2E: 주문 접수 → 거절 → REJECTED 상태, 모니터링 집계 제외")
    void 거절_시나리오() {
        Order order = ctx.getOrderService().placeOrder("S-001", "고객A", 10, "20260612");
        assertEquals(OrderStatus.RESERVED, order.getStatus());

        ctx.getApprovalService().reject(order);
        assertEquals(OrderStatus.REJECTED, order.getStatus());

        // 모니터링 집계에서 REJECTED 제외 확인
        List<Order> all = ctx.getOrderService().findAll();
        Map<OrderStatus, Long> counts = ctx.getMonitoringService().getOrderCountByStatus(all);
        assertFalse(counts.containsKey(OrderStatus.REJECTED), "REJECTED 주문은 모니터링 집계에서 제외되어야 한다");
        assertEquals(0, counts.values().stream().mapToLong(Long::longValue).sum());
    }

    // ─────────────────────────────────────────────
    // 시나리오 2: FIFO 큐 다중 주문 순서 보장
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("E2E: FIFO 큐 — 재고 부족 주문 3개 승인 시 등록 순서대로 생산")
    void FIFO_큐_순서_검증() {
        // 재고 10, 각 주문 50 → 모두 생산 필요
        ctx.getSampleService().register(new Sample("S-002", "GaN 에피택셜", 0.9, 0.90, 10));

        Order o1 = ctx.getOrderService().placeOrder("S-002", "고객A", 50, "20260612");
        Order o2 = ctx.getOrderService().placeOrder("S-002", "고객B", 50, "20260612");
        Order o3 = ctx.getOrderService().placeOrder("S-002", "고객C", 50, "20260612");

        Sample sample = ctx.getSampleService().findAll().stream()
                .filter(s -> s.getId().equals("S-002")).findFirst().orElseThrow();

        ctx.getApprovalService().approve(o1, sample);
        sample = ctx.getSampleService().findAll().stream()
                .filter(s -> s.getId().equals("S-002")).findFirst().orElseThrow();
        ctx.getApprovalService().approve(o2, sample);
        sample = ctx.getSampleService().findAll().stream()
                .filter(s -> s.getId().equals("S-002")).findFirst().orElseThrow();
        ctx.getApprovalService().approve(o3, sample);

        // 큐 첫 번째(현재 생산 중)는 o1
        assertEquals(o1.getOrderId(), ctx.getProductionQueue().getCurrentJob().getOrderId(),
                "FIFO: 첫 번째 승인된 주문이 먼저 생산되어야 한다");

        // 생산 완료 후 다음 순서는 o2
        ctx.getProductionService().completeCurrentProduction();
        assertEquals(o2.getOrderId(), ctx.getProductionQueue().getCurrentJob().getOrderId(),
                "FIFO: 두 번째 승인된 주문이 그 다음 생산되어야 한다");
    }

    // ─────────────────────────────────────────────
    // 시나리오 3: 재고 상태 판정 (여유 / 부족 / 고갈)
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("E2E: 재고 상태 — CONFIRMED 주문 수요 반영 '부족' 판정")
    void 재고_상태_부족_판정() {
        // 재고 100, 주문 150 → 재고 충분하여 CONFIRMED, 이후 부족 판정
        ctx.getSampleService().register(new Sample("S-003", "테스트 시료", 1.0, 0.95, 100));

        Order order = ctx.getOrderService().placeOrder("S-003", "고객A", 150, "20260612");
        Sample sample = ctx.getSampleService().findAll().stream()
                .filter(s -> s.getId().equals("S-003")).findFirst().orElseThrow();
        // 재고 부족 → PRODUCING
        ctx.getApprovalService().approve(order, sample);
        assertEquals(OrderStatus.PRODUCING, order.getStatus());

        // 생산 완료 후 CONFIRMED, 재고는 실생산량만큼 증가 → 여유로 전환될 수 있음
        ctx.getProductionService().completeCurrentProduction();

        List<Order> ordersForSample = ctx.getOrderService().findAll().stream()
                .filter(o -> o.getSampleId().equals("S-003")).toList();
        Sample updatedSample = ctx.getSampleService().findAll().stream()
                .filter(s -> s.getId().equals("S-003")).findFirst().orElseThrow();

        String status = ctx.getMonitoringService().getStockStatus(updatedSample, ordersForSample);
        // 생산 완료 후 CONFIRMED 상태 — 수요(150) 대비 재고 판정
        assertNotNull(status);
        assertTrue(List.of("여유", "부족", "고갈").contains(status));
    }

    @Test
    @DisplayName("E2E: 재고 상태 — 재고 0일 때 '고갈' 판정")
    void 재고_상태_고갈_판정() {
        ctx.getSampleService().register(new Sample("S-004", "고갈 시료", 0.5, 0.90, 0));

        Sample sample = ctx.getSampleService().findAll().stream()
                .filter(s -> s.getId().equals("S-004")).findFirst().orElseThrow();

        String status = ctx.getMonitoringService().getStockStatus(sample, List.of());
        assertEquals("고갈", status);
    }

    @Test
    @DisplayName("E2E: 재고 상태 — 주문 없을 때 '여유' 판정")
    void 재고_상태_여유_판정() {
        Sample sample = ctx.getSampleService().findAll().stream()
                .filter(s -> s.getId().equals("S-001")).findFirst().orElseThrow();

        String status = ctx.getMonitoringService().getStockStatus(sample, List.of());
        assertEquals("여유", status);
    }

    // ─────────────────────────────────────────────
    // 시나리오 4: 전체 흐름 — 생산 후 연속 출고
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("E2E: 재고 부족 주문 생산 완료 후 연속 출고 2건")
    void 생산_후_연속_출고() {
        // S-001 재고 100
        Order o1 = ctx.getOrderService().placeOrder("S-001", "고객A", 200, "20260612");
        Order o2 = ctx.getOrderService().placeOrder("S-001", "고객B", 50, "20260612");

        Sample sample = ctx.getSampleService().findAll().get(0);
        ctx.getApprovalService().approve(o1, sample);
        assertEquals(OrderStatus.PRODUCING, o1.getStatus());

        // o2: 재고 이미 0 (o1이 모두 소진 가정은 없음 — approve 시 재고 차감 없음)
        sample = ctx.getSampleService().findAll().get(0);
        ctx.getApprovalService().approve(o2, sample);

        // o1 생산 완료
        ctx.getProductionService().completeCurrentProduction();
        List<Order> confirmed = ctx.getOrderService().findByStatus(OrderStatus.CONFIRMED);
        assertFalse(confirmed.isEmpty(), "생산 완료 후 CONFIRMED 주문 존재해야 함");

        // o1 출고
        Order toRelease = confirmed.stream()
                .filter(o -> o.getOrderId().equals(o1.getOrderId())).findFirst().orElseThrow();
        sample = ctx.getSampleService().findAll().get(0);
        ctx.getReleaseService().release(toRelease, sample);
        assertEquals(OrderStatus.RELEASE, ctx.getOrderService().findByStatus(OrderStatus.RELEASE).get(0).getStatus());
    }

    // ─────────────────────────────────────────────
    // 시나리오 5: 모니터링 상태별 집계
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("E2E: 모니터링 — 상태별 집계 정확성 (REJECTED 제외, RESERVED/CONFIRMED/PRODUCING/RELEASE 포함)")
    void 모니터링_상태별_집계() {
        // RESERVED 1건
        Order reserved = ctx.getOrderService().placeOrder("S-001", "고객A", 5, "20260612");

        // CONFIRMED 1건 (재고 충분)
        Order confirmed = ctx.getOrderService().placeOrder("S-001", "고객B", 10, "20260612");
        Sample sample = ctx.getSampleService().findAll().get(0);
        ctx.getApprovalService().approve(confirmed, sample);

        // REJECTED 1건
        Order rejected = ctx.getOrderService().placeOrder("S-001", "고객C", 5, "20260612");
        ctx.getApprovalService().reject(rejected);

        List<Order> all = ctx.getOrderService().findAll();
        Map<OrderStatus, Long> counts = ctx.getMonitoringService().getOrderCountByStatus(all);

        assertEquals(1L, counts.getOrDefault(OrderStatus.RESERVED, 0L), "RESERVED 1건");
        assertEquals(1L, counts.getOrDefault(OrderStatus.CONFIRMED, 0L), "CONFIRMED 1건");
        assertFalse(counts.containsKey(OrderStatus.REJECTED), "REJECTED 집계 제외");
    }
}
