package controller;

import model.entity.Order;
import model.entity.OrderStatus;
import model.entity.ProductionJob;
import model.entity.Sample;
import model.repository.*;
import model.service.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import view.ProductionView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class ProductionControllerTest {

    @TempDir
    Path tempDir;

    private PrintStream originalOut;
    private ByteArrayOutputStream outBuf;

    private SampleRepository sampleRepo;
    private OrderRepository orderRepo;
    private ProductionQueueRepository queueRepo;
    private ProductionQueue queue;
    private ProductionService productionService;

    @BeforeEach
    void setUp() {
        originalOut = System.out;
        outBuf = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outBuf));

        sampleRepo = new JsonSampleRepository(tempDir.resolve("samples.json").toString());
        orderRepo = new JsonOrderRepository(tempDir.resolve("orders.json").toString());
        queueRepo = new JsonProductionQueueRepository(tempDir.resolve("queue.json").toString());
        queue = new ProductionQueue();
        productionService = new ProductionService(queue, sampleRepo, orderRepo, queueRepo);
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    private ProductionController controllerWithInput(String input) {
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        ProductionView view = new ProductionView(scanner);
        return new ProductionController(productionService, queue, view);
    }

    @Test
    @DisplayName("큐 비어있음 — 완료 묻지 않고 IDLE 표시")
    void 큐_비어있음_IDLE_표시() {
        // Arrange
        ProductionController controller = controllerWithInput("");

        // Act
        controller.run();

        // Assert
        String out = outBuf.toString();
        assertTrue(out.contains("IDLE"), "IDLE 포함");
        assertFalse(out.contains("생산 완료 처리를 하시겠습니까"), "완료 질문 없음");
    }

    @Test
    @DisplayName("생산 중 — Y 입력으로 completeCurrentProduction 실행")
    void 생산중_Y_입력_생산완료() {
        // Arrange
        sampleRepo.save(new Sample("S-001", "SiC 파워기판", 0.8, 0.92, 30));
        Order order = new Order("ORD-001", "S-001", "홍길동", 200);
        order.changeStatus(OrderStatus.PRODUCING);
        orderRepo.save(order);
        queue.enqueue(new ProductionJob("ORD-001", "S-001", 200, 170, 206, 0.8));

        ProductionController controller = controllerWithInput("Y\n");

        // Act
        controller.run();

        // Assert
        String out = outBuf.toString();
        assertTrue(out.contains("CONFIRMED"), "완료 후 CONFIRMED 메시지 포함");
        assertNull(queue.getCurrentJob(), "큐가 비어있음");
    }

    @Test
    @DisplayName("생산 중 — N 입력으로 생산 완료 취소")
    void 생산중_N_입력_취소() {
        // Arrange
        sampleRepo.save(new Sample("S-001", "SiC 파워기판", 0.8, 0.92, 30));
        Order order = new Order("ORD-001", "S-001", "홍길동", 200);
        order.changeStatus(OrderStatus.PRODUCING);
        orderRepo.save(order);
        queue.enqueue(new ProductionJob("ORD-001", "S-001", 200, 170, 206, 0.8));

        ProductionController controller = controllerWithInput("N\n");

        // Act
        controller.run();

        // Assert
        assertNotNull(queue.getCurrentJob(), "큐에 작업 남아있음");
        assertEquals(OrderStatus.PRODUCING, orderRepo.findById("ORD-001").get().getStatus());
    }

    @Test
    @DisplayName("대기 큐 있을 때 — 목록 출력")
    void 대기_큐_있을때_목록_출력() {
        // Arrange
        sampleRepo.save(new Sample("S-001", "SiC 파워기판", 0.8, 0.92, 30));
        Order o1 = new Order("ORD-001", "S-001", "고객A", 100);
        o1.changeStatus(OrderStatus.PRODUCING);
        orderRepo.save(o1);
        Order o2 = new Order("ORD-002", "S-001", "고객B", 50);
        o2.changeStatus(OrderStatus.PRODUCING);
        orderRepo.save(o2);

        queue.enqueue(new ProductionJob("ORD-001", "S-001", 100, 70, 85, 0.8));
        queue.enqueue(new ProductionJob("ORD-002", "S-001", 80, 50, 61, 0.8));

        ProductionController controller = controllerWithInput("N\n");

        // Act
        controller.run();

        // Assert
        String out = outBuf.toString();
        assertTrue(out.contains("ORD-001"), "현재 작업 포함");
        assertTrue(out.contains("ORD-002"), "대기 작업 포함");
    }
}
