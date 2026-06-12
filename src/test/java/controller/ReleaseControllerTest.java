package controller;

import model.entity.Order;
import model.entity.OrderStatus;
import model.entity.Sample;
import model.repository.JsonOrderRepository;
import model.repository.JsonSampleRepository;
import model.service.OrderService;
import model.service.ReleaseService;
import model.service.SampleService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import view.ReleaseView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class ReleaseControllerTest {

    @TempDir
    Path tempDir;

    private PrintStream originalOut;
    private ByteArrayOutputStream outBuf;

    private JsonSampleRepository sampleRepo;
    private JsonOrderRepository orderRepo;
    private ReleaseService releaseService;
    private OrderService orderService;
    private SampleService sampleService;

    @BeforeEach
    void setUp() {
        originalOut = System.out;
        outBuf = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outBuf));

        sampleRepo = new JsonSampleRepository(tempDir.resolve("samples.json").toString());
        orderRepo = new JsonOrderRepository(tempDir.resolve("orders.json").toString());
        releaseService = new ReleaseService(sampleRepo, orderRepo);
        orderService = new OrderService(sampleRepo, orderRepo);
        sampleService = new SampleService(sampleRepo);
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    private ReleaseController controllerWithInput(String input) {
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        ReleaseView view = new ReleaseView(scanner);
        return new ReleaseController(releaseService, orderService, sampleService, view);
    }

    @Test
    @DisplayName("빈 CONFIRMED 목록 — 즉시 리턴")
    void emptyConfirmedList_returnsImmediately() {
        // Arrange
        ReleaseController controller = controllerWithInput("");

        // Act
        controller.run();

        // Assert
        assertTrue(outBuf.toString().contains("출고 가능 주문이 없습니다"), "없음 메시지 포함");
    }

    @Test
    @DisplayName("CONFIRMED 주문 있음 — 1 입력 시 RELEASE 전환")
    void CONFIRMED_주문_RELEASE_전환() {
        // Arrange
        sampleRepo.save(new Sample("S-001", "SiC 파워기판", 0.8, 0.92, 200));
        Order order = new Order("ORD-001", "S-001", "홍길동", 100);
        order.changeStatus(OrderStatus.CONFIRMED);
        orderRepo.save(order);

        ReleaseController controller = controllerWithInput("1\n");

        // Act
        controller.run();

        // Assert
        Order result = orderRepo.findById("ORD-001").get();
        assertEquals(OrderStatus.RELEASE, result.getStatus());
        assertTrue(outBuf.toString().contains("출고 처리 완료"), "완료 메시지 포함");
    }

    @Test
    @DisplayName("0 입력 — 리턴")
    void zeroIndex_returnsImmediately() {
        // Arrange
        sampleRepo.save(new Sample("S-001", "SiC 파워기판", 0.8, 0.92, 200));
        Order order = new Order("ORD-001", "S-001", "홍길동", 100);
        order.changeStatus(OrderStatus.CONFIRMED);
        orderRepo.save(order);

        ReleaseController controller = controllerWithInput("0\n");

        // Act
        controller.run();

        // Assert
        // 상태 변경 없이 CONFIRMED 유지
        assertEquals(OrderStatus.CONFIRMED, orderRepo.findById("ORD-001").get().getStatus());
    }

    @Test
    @DisplayName("범위 초과 인덱스 — 리턴")
    void outOfRangeIndex_returnsImmediately() {
        // Arrange
        sampleRepo.save(new Sample("S-001", "SiC 파워기판", 0.8, 0.92, 200));
        Order order = new Order("ORD-001", "S-001", "홍길동", 100);
        order.changeStatus(OrderStatus.CONFIRMED);
        orderRepo.save(order);

        ReleaseController controller = controllerWithInput("9\n");

        // Act / Assert
        assertDoesNotThrow(() -> controller.run());
        assertEquals(OrderStatus.CONFIRMED, orderRepo.findById("ORD-001").get().getStatus());
    }

    @Test
    @DisplayName("출고 후 재고 차감 확인")
    void release_deductsStock() {
        // Arrange
        sampleRepo.save(new Sample("S-001", "SiC 파워기판", 0.8, 0.92, 200));
        Order order = new Order("ORD-001", "S-001", "홍길동", 100);
        order.changeStatus(OrderStatus.CONFIRMED);
        orderRepo.save(order);

        ReleaseController controller = controllerWithInput("1\n");

        // Act
        controller.run();

        // Assert
        Sample updatedSample = sampleRepo.findById("S-001").get();
        assertEquals(100, updatedSample.getStock(), "재고 100 차감 후 100 남음");
    }
}
