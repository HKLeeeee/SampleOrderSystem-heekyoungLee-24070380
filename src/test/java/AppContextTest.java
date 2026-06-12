import controller.MainController;
import model.entity.Order;
import model.entity.OrderStatus;
import model.entity.Sample;
import model.service.ProductionQueue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.nio.file.Path;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class AppContextTest {

    @TempDir
    Path tempDir;

    private AppContext ctx;

    @BeforeEach
    void setUp() {
        ctx = new AppContext(tempDir.toString());
    }

    @Test
    @DisplayName("AppContext 생성 성공 — 모든 서비스 초기화")
    void appContext_생성_성공() {
        assertNotNull(ctx.getSampleService());
        assertNotNull(ctx.getOrderService());
        assertNotNull(ctx.getApprovalService());
        assertNotNull(ctx.getProductionService());
        assertNotNull(ctx.getMonitoringService());
        assertNotNull(ctx.getReleaseService());
    }

    @Test
    @DisplayName("시료 등록 후 조회")
    void 시료_등록_후_조회() {
        ctx.getSampleService().register(new Sample("S-001", "SiC 웨이퍼", 0.8, 0.92, 100));
        assertEquals(1, ctx.getSampleService().findAll().size());
    }

    @Test
    @DisplayName("주문 접수 → 승인(재고 충분) → 출고 — RELEASE, 재고 차감")
    void 주문_접수_승인_출고_흐름() {
        ctx.getSampleService().register(new Sample("S-001", "SiC 웨이퍼", 0.8, 0.92, 100));
        Order order = ctx.getOrderService().placeOrder("S-001", "홍길동", 50, "20260612");

        Sample sample = ctx.getSampleService().findAll().get(0);
        ctx.getApprovalService().approve(order, sample);
        assertEquals(OrderStatus.CONFIRMED, order.getStatus());

        sample = ctx.getSampleService().findAll().get(0);
        ctx.getReleaseService().release(order, sample);
        assertEquals(OrderStatus.RELEASE, order.getStatus());
        assertEquals(50, ctx.getSampleService().findAll().get(0).getStock());
    }

    @Test
    @DisplayName("주문 접수 → 승인(재고 부족) → 생산완료 → 출고 — RELEASE")
    void 생산_흐름() {
        ctx.getSampleService().register(new Sample("S-001", "SiC 웨이퍼", 0.8, 0.92, 10));
        Order order = ctx.getOrderService().placeOrder("S-001", "홍길동", 50, "20260612");

        Sample sample = ctx.getSampleService().findAll().get(0);
        ctx.getApprovalService().approve(order, sample);
        assertEquals(OrderStatus.PRODUCING, order.getStatus());

        ctx.getProductionService().completeCurrentProduction();
        Order confirmed = ctx.getOrderService().findByStatus(OrderStatus.CONFIRMED).get(0);
        assertEquals(OrderStatus.CONFIRMED, confirmed.getStatus());

        sample = ctx.getSampleService().findAll().get(0);
        ctx.getReleaseService().release(confirmed, sample);
        assertEquals(OrderStatus.RELEASE, ctx.getOrderService().findByStatus(OrderStatus.RELEASE).get(0).getStatus());
    }

    @Test
    @DisplayName("AppContext.buildMainController — 비null 반환")
    void buildMainController_smoke() {
        Scanner scanner = new Scanner(new ByteArrayInputStream("0\n".getBytes()));
        MainController ctrl = ctx.buildMainController(scanner);
        assertNotNull(ctrl);
    }
}
