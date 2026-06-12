package view;

import model.entity.Order;
import model.entity.OrderStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class ReleaseViewTest {

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

    private ReleaseView viewWithInput(String input) {
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        return new ReleaseView(scanner);
    }

    @Test
    @DisplayName("displayConfirmedList — 주문 목록 출력")
    void displayConfirmedList_주문_목록_출력() {
        // Arrange
        ReleaseView view = viewWithInput("");
        Order order = new Order("ORD-001", "S-001", "홍길동", 50);
        order.changeStatus(OrderStatus.CONFIRMED);
        Map<String, String> names = Map.of("S-001", "SiC 파워기판");

        // Act
        view.displayConfirmedList(List.of(order), names);

        // Assert
        String out = outBuf.toString();
        assertTrue(out.contains("ORD-001"), "주문번호 포함");
        assertTrue(out.contains("홍길동"), "고객명 포함");
        assertTrue(out.contains("SiC 파워기판"), "시료명 포함");
        assertTrue(out.contains("CONFIRMED"), "제목에 CONFIRMED 포함");
    }

    @Test
    @DisplayName("displayConfirmedList — 빈 목록 메시지 출력")
    void displayConfirmedList_빈_목록_메시지() {
        // Arrange
        ReleaseView view = viewWithInput("");

        // Act
        view.displayConfirmedList(List.of(), Map.of());

        // Assert
        assertTrue(outBuf.toString().contains("출고 가능 주문이 없습니다"), "없음 메시지 포함");
    }

    @Test
    @DisplayName("selectOrderIndex — 유효 번호 입력 시 0-based 인덱스 반환")
    void selectOrderIndex_유효번호_반환() {
        // Arrange
        ReleaseView view = viewWithInput("2\n");

        // Act
        int idx = view.selectOrderIndex(3);

        // Assert
        assertEquals(1, idx);
    }

    @Test
    @DisplayName("selectOrderIndex — 0 입력 시 -1 반환")
    void selectOrderIndex_0_입력_minus1() {
        // Arrange
        ReleaseView view = viewWithInput("0\n");

        // Act
        int idx = view.selectOrderIndex(3);

        // Assert
        assertEquals(-1, idx);
    }

    @Test
    @DisplayName("selectOrderIndex — 숫자 아닌 입력 시 -1 반환")
    void selectOrderIndex_비숫자_minus1() {
        // Arrange
        ReleaseView view = viewWithInput("x\n");

        // Act
        int idx = view.selectOrderIndex(3);

        // Assert
        assertEquals(-1, idx);
    }

    @Test
    @DisplayName("displayReleaseResult — 출고 결과 정보 출력")
    void displayReleaseResult_출력() {
        // Arrange
        ReleaseView view = viewWithInput("");
        Order order = new Order("ORD-001", "S-001", "홍길동", 50);
        order.changeStatus(OrderStatus.CONFIRMED);
        order.changeStatus(OrderStatus.RELEASE);
        LocalDateTime processedAt = LocalDateTime.of(2026, 6, 12, 10, 30, 0);

        // Act
        view.displayReleaseResult(order, processedAt);

        // Assert
        String out = outBuf.toString();
        assertTrue(out.contains("ORD-001"), "주문번호 포함");
        assertTrue(out.contains("50"), "수량 포함");
        assertTrue(out.contains("2026-06-12"), "날짜 포함");
        assertTrue(out.contains("RELEASE"), "상태 포함");
    }

    @Test
    @DisplayName("displayMessage — 메시지 출력")
    void displayMessage_출력() {
        // Arrange
        ReleaseView view = viewWithInput("");

        // Act
        view.displayMessage("출고 완료");

        // Assert
        assertTrue(outBuf.toString().contains("출고 완료"));
    }
}
