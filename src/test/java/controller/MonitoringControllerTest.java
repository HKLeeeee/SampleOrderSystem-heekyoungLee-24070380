package controller;

import model.entity.Sample;
import model.repository.JsonOrderRepository;
import model.repository.JsonSampleRepository;
import model.service.MonitoringService;
import model.service.OrderService;
import model.service.SampleService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import view.MonitoringView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class MonitoringControllerTest {

    @TempDir
    Path tempDir;

    private PrintStream originalOut;
    private ByteArrayOutputStream outBuf;

    private SampleService sampleService;
    private OrderService orderService;
    private MonitoringService monitoringService;
    private JsonSampleRepository sampleRepo;
    private JsonOrderRepository orderRepo;

    @BeforeEach
    void setUp() {
        originalOut = System.out;
        outBuf = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outBuf));

        sampleRepo = new JsonSampleRepository(tempDir.resolve("samples.json").toString());
        orderRepo = new JsonOrderRepository(tempDir.resolve("orders.json").toString());
        sampleService = new SampleService(sampleRepo);
        orderService = new OrderService(sampleRepo, orderRepo);
        monitoringService = new MonitoringService();
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    private MonitoringController controllerWithInput(String input) {
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        MonitoringView view = new MonitoringView(scanner);
        return new MonitoringController(monitoringService, orderService, sampleService, view);
    }

    @Test
    @DisplayName("1 입력 후 0 — 주문량 확인 후 복귀")
    void 주문량_확인_후_복귀() {
        // Arrange
        MonitoringController controller = controllerWithInput("1\n0\n");

        // Act
        controller.run();

        // Assert
        String out = outBuf.toString();
        assertTrue(out.contains("RESERVED"), "RESERVED 포함");
        assertTrue(out.contains("CONFIRMED"), "CONFIRMED 포함");
    }

    @Test
    @DisplayName("2 입력 후 0 — 재고량 확인 후 복귀")
    void 재고량_확인_후_복귀() {
        // Arrange
        sampleRepo.save(new Sample("S-001", "SiC 파워기판", 0.8, 0.92, 100));
        MonitoringController controller = controllerWithInput("2\n0\n");

        // Act
        controller.run();

        // Assert
        String out = outBuf.toString();
        assertTrue(out.contains("재고 현황"), "재고 현황 포함");
        assertTrue(out.contains("SiC 파워기판"), "시료명 포함");
    }

    @Test
    @DisplayName("0 입력 — 즉시 복귀")
    void 즉시_복귀() {
        // Arrange
        MonitoringController controller = controllerWithInput("0\n");

        // Act / Assert
        assertDoesNotThrow(() -> controller.run());
    }

    @Test
    @DisplayName("잘못된 입력 후 0 — 오류 메시지 출력 후 복귀")
    void 잘못된_입력_오류_후_복귀() {
        // Arrange
        MonitoringController controller = controllerWithInput("9\n0\n");

        // Act
        controller.run();

        // Assert
        assertTrue(outBuf.toString().contains("[오류]"), "오류 메시지 포함");
    }

    @Test
    @DisplayName("주문량 확인 — 재고 현황을 자동으로 표시하지 않음")
    void 주문량_확인_재고현황_미표시() {
        // Arrange: [1] 선택 시 주문량만 표시, 재고 현황은 [2]에서만 표시
        MonitoringController controller = controllerWithInput("1\n0\n");

        // Act
        controller.run();

        // Assert: 주문량 현황은 표시
        String out = outBuf.toString();
        assertTrue(out.contains("RESERVED"), "RESERVED 포함");
        // 재고 현황은 표시되지 않아야 함 (이전 버그: showOrderCounts가 showStockStatus 자동 호출)
        assertFalse(out.contains("재고 현황"), "재고 현황 미표시");
    }
}
