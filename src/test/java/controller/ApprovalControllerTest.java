package controller;

import model.entity.Order;
import model.entity.OrderStatus;
import model.entity.Sample;
import model.repository.*;
import model.service.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import view.ApprovalView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class ApprovalControllerTest {

    @TempDir
    Path tempDir;

    private PrintStream originalOut;
    private ByteArrayOutputStream outBuf;

    private SampleRepository sampleRepo;
    private OrderRepository orderRepo;
    private ProductionQueueRepository queueRepo;
    private ProductionQueue queue;
    private ApprovalService approvalService;
    private OrderService orderService;
    private SampleService sampleService;

    @BeforeEach
    void setUp() {
        originalOut = System.out;
        outBuf = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outBuf));

        sampleRepo = new JsonSampleRepository(tempDir.resolve("samples.json").toString());
        orderRepo = new JsonOrderRepository(tempDir.resolve("orders.json").toString());
        queueRepo = new JsonProductionQueueRepository(tempDir.resolve("queue.json").toString());
        queue = new ProductionQueue();
        approvalService = new ApprovalService(orderRepo, queue, queueRepo);
        orderService = new OrderService(sampleRepo, orderRepo);
        sampleService = new SampleService(sampleRepo);
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    private ApprovalController controllerWithInput(String input) {
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        ApprovalView view = new ApprovalView(scanner);
        return new ApprovalController(approvalService, orderService, sampleService, view);
    }

    @Test
    @DisplayName("빈 RESERVED 목록 — 즉시 리턴")
    void 빈_RESERVED_목록_즉시_리턴() {
        // Arrange
        ApprovalController controller = controllerWithInput("");

        // Act / Assert
        assertDoesNotThrow(() -> controller.run());
        assertTrue(outBuf.toString().contains("예약 목록이 없습니다"), "없음 메시지 포함");
    }

    @Test
    @DisplayName("승인 — 재고 충분 시 CONFIRMED 전환")
    void 승인_재고충분_CONFIRMED() {
        // Arrange
        sampleRepo.save(new Sample("S-001", "SiC 파워기판", 0.8, 0.92, 200));
        orderRepo.save(new Order("ORD-001", "S-001", "홍길동", 100));
        // 입력: 번호=1 선택 -> 승인=1
        ApprovalController controller = controllerWithInput("1\n1\n");

        // Act
        controller.run();

        // Assert
        Order result = orderRepo.findById("ORD-001").get();
        assertEquals(OrderStatus.CONFIRMED, result.getStatus());
    }

    @Test
    @DisplayName("승인 — 재고 부족 시 Y 입력으로 PRODUCING 전환")
    void 승인_재고부족_PRODUCING() {
        // Arrange
        sampleRepo.save(new Sample("S-001", "SiC 파워기판", 0.8, 0.92, 30));
        orderRepo.save(new Order("ORD-001", "S-001", "홍길동", 200));
        // 입력: 번호=1 선택 -> 승인=1 -> Y 확인
        ApprovalController controller = controllerWithInput("1\n1\nY\n");

        // Act
        controller.run();

        // Assert
        Order result = orderRepo.findById("ORD-001").get();
        assertEquals(OrderStatus.PRODUCING, result.getStatus());
        assertNotNull(queue.getCurrentJob());
    }

    @Test
    @DisplayName("승인 — 재고 부족 시 N 입력으로 REJECTED 전환")
    void 승인_재고부족_N_REJECTED() {
        // Arrange
        sampleRepo.save(new Sample("S-001", "SiC 파워기판", 0.8, 0.92, 30));
        orderRepo.save(new Order("ORD-001", "S-001", "홍길동", 200));
        // 입력: 번호=1 선택 -> 승인=1 -> N 취소
        ApprovalController controller = controllerWithInput("1\n1\nN\n");

        // Act
        controller.run();

        // Assert
        Order result = orderRepo.findById("ORD-001").get();
        assertEquals(OrderStatus.REJECTED, result.getStatus());
    }

    @Test
    @DisplayName("거절 — REJECTED 전환")
    void 거절_REJECTED() {
        // Arrange
        sampleRepo.save(new Sample("S-001", "SiC 파워기판", 0.8, 0.92, 200));
        orderRepo.save(new Order("ORD-001", "S-001", "홍길동", 100));
        // 입력: 번호=1 선택 -> 거절=2
        ApprovalController controller = controllerWithInput("1\n2\n");

        // Act
        controller.run();

        // Assert
        Order result = orderRepo.findById("ORD-001").get();
        assertEquals(OrderStatus.REJECTED, result.getStatus());
        assertTrue(outBuf.toString().contains("REJECTED"), "REJECTED 메시지 포함");
    }

    @Test
    @DisplayName("잘못된 액션 입력 — 오류 메시지 출력")
    void 잘못된_액션_오류메시지() {
        // Arrange
        sampleRepo.save(new Sample("S-001", "SiC 파워기판", 0.8, 0.92, 200));
        orderRepo.save(new Order("ORD-001", "S-001", "홍길동", 100));
        // 입력: 번호=1 선택 -> 잘못된 액션=9
        ApprovalController controller = controllerWithInput("1\n9\n");

        // Act
        controller.run();

        // Assert
        assertTrue(outBuf.toString().contains("[오류]"), "오류 메시지 포함");
    }

    @Test
    @DisplayName("인덱스 범위 벗어남 — 그냥 리턴")
    void 인덱스_범위_벗어남_리턴() {
        // Arrange
        sampleRepo.save(new Sample("S-001", "SiC 파워기판", 0.8, 0.92, 200));
        orderRepo.save(new Order("ORD-001", "S-001", "홍길동", 100));
        // 입력: 범위 벗어남 번호=5
        ApprovalController controller = controllerWithInput("5\n");

        // Act / Assert
        assertDoesNotThrow(() -> controller.run());
    }
}
