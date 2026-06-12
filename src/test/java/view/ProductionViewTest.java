package view;

import model.entity.ProductionJob;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class ProductionViewTest {

    private PrintStream originalOut;
    private ByteArrayOutputStream outBuf;

    @BeforeEach
    void setUp() {
        originalOut = System.out;
        outBuf = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outBuf));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    private ProductionView viewWithInput(String input) {
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        return new ProductionView(scanner);
    }

    @Test
    @DisplayName("displayHeader — 생산라인 헤더 출력")
    void displayHeader_출력() {
        // Arrange
        ProductionView view = viewWithInput("");

        // Act
        view.displayHeader();

        // Assert
        String out = outBuf.toString();
        assertTrue(out.contains("[5]"), "[5] 포함");
        assertTrue(out.contains("FIFO"), "FIFO 포함");
    }

    @Test
    @DisplayName("displayCurrentJob — null 일 때 IDLE 메시지 출력")
    void displayCurrentJob_null_IDLE_출력() {
        // Arrange
        ProductionView view = viewWithInput("");

        // Act
        view.displayCurrentJob(null, LocalDateTime.now(), null);

        // Assert
        assertTrue(outBuf.toString().contains("IDLE"), "IDLE 포함");
    }

    @Test
    @DisplayName("displayCurrentJob — 작업 있을 때 주문 정보 출력")
    void displayCurrentJob_작업있음_정보_출력() {
        // Arrange
        ProductionView view = viewWithInput("");
        ProductionJob job = new ProductionJob("ORD-001", "S-001", 170, 206, 0.8);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime end = now.plusMinutes(165);

        // Act
        view.displayCurrentJob(job, now, end);

        // Assert
        String out = outBuf.toString();
        assertTrue(out.contains("ORD-001"), "주문번호 포함");
        assertTrue(out.contains("S-001"), "시료 ID 포함");
        assertTrue(out.contains("170"), "부족분 포함");
        assertTrue(out.contains("206"), "실생산량 포함");
    }

    @Test
    @DisplayName("displayCurrentJob — expectedEnd null 일 때 예외 없이 완료")
    void displayCurrentJob_expectedEnd_null_처리() {
        // Arrange
        ProductionView view = viewWithInput("");
        ProductionJob job = new ProductionJob("ORD-001", "S-001", 50, 61, 0.8);

        // Act / Assert
        assertDoesNotThrow(() ->
                view.displayCurrentJob(job, LocalDateTime.now(), null));
    }

    @Test
    @DisplayName("displayWaitingQueue — 빈 큐 메시지 출력")
    void displayWaitingQueue_빈큐_메시지() {
        // Arrange
        ProductionView view = viewWithInput("");

        // Act
        view.displayWaitingQueue(List.of(), LocalDateTime.now());

        // Assert
        assertTrue(outBuf.toString().contains("대기 중인 주문이 없습니다"), "없음 메시지 포함");
    }

    @Test
    @DisplayName("displayWaitingQueue — 대기 작업 있을 때 목록 출력")
    void displayWaitingQueue_대기작업_출력() {
        // Arrange
        ProductionView view = viewWithInput("");
        List<ProductionJob> waiting = List.of(
                new ProductionJob("ORD-002", "S-001", 100, 121, 0.8),
                new ProductionJob("ORD-003", "S-002", 50, 61, 1.0)
        );

        // Act
        view.displayWaitingQueue(waiting, LocalDateTime.now());

        // Assert
        String out = outBuf.toString();
        assertTrue(out.contains("ORD-002"), "첫번째 주문 포함");
        assertTrue(out.contains("ORD-003"), "두번째 주문 포함");
        assertTrue(out.contains("FIFO"), "FIFO 안내 포함");
    }

    @Test
    @DisplayName("askCompleteProduction — Y 입력 시 true 반환")
    void askCompleteProduction_Y_true() {
        // Arrange
        ProductionView view = viewWithInput("Y\n");

        // Act
        boolean result = view.askCompleteProduction();

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("askCompleteProduction — N 입력 시 false 반환")
    void askCompleteProduction_N_false() {
        // Arrange
        ProductionView view = viewWithInput("N\n");

        // Act
        boolean result = view.askCompleteProduction();

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("displayMessage — 메시지 출력")
    void displayMessage_출력() {
        // Arrange
        ProductionView view = viewWithInput("");

        // Act
        view.displayMessage("생산 완료");

        // Assert
        assertTrue(outBuf.toString().contains("생산 완료"));
    }
}
