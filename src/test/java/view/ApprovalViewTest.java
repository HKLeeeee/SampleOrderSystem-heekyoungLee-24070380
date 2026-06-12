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
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class ApprovalViewTest {

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

    private ApprovalView viewWithInput(String input) {
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        return new ApprovalView(scanner);
    }

    @Test
    @DisplayName("displayReservedList — 주문 목록 출력")
    void displayReservedList_주문_목록_출력() {
        // Arrange
        ApprovalView view = viewWithInput("");
        Order order = new Order("ORD-001", "S-001", "홍길동", 100);
        Map<String, String> names = Map.of("S-001", "SiC 파워기판");

        // Act
        view.displayReservedList(List.of(order), names);

        // Assert
        String out = outBuf.toString();
        assertTrue(out.contains("ORD-001"), "주문번호 포함");
        assertTrue(out.contains("홍길동"), "고객명 포함");
        assertTrue(out.contains("SiC 파워기판"), "시료명 포함");
        assertTrue(out.contains("RESERVED"), "상태 포함");
    }

    @Test
    @DisplayName("displayReservedList — 빈 목록 메시지 출력")
    void displayReservedList_빈_목록_메시지() {
        // Arrange
        ApprovalView view = viewWithInput("");

        // Act
        view.displayReservedList(List.of(), Map.of());

        // Assert
        assertTrue(outBuf.toString().contains("예약 목록이 없습니다"), "없음 메시지 포함");
    }

    @Test
    @DisplayName("selectOrderIndex — 유효 번호 입력 시 0-based 인덱스 반환")
    void selectOrderIndex_유효번호_반환() {
        // Arrange
        ApprovalView view = viewWithInput("1\n");

        // Act
        int idx = view.selectOrderIndex(3);

        // Assert
        assertEquals(0, idx);
    }

    @Test
    @DisplayName("selectOrderIndex — 0 입력 시 -1 반환")
    void selectOrderIndex_0_입력_minus1() {
        // Arrange
        ApprovalView view = viewWithInput("0\n");

        // Act
        int idx = view.selectOrderIndex(3);

        // Assert
        assertEquals(-1, idx);
    }

    @Test
    @DisplayName("selectOrderIndex — 숫자 아닌 입력 시 -1 반환")
    void selectOrderIndex_비숫자_minus1() {
        // Arrange
        ApprovalView view = viewWithInput("abc\n");

        // Act
        int idx = view.selectOrderIndex(3);

        // Assert
        assertEquals(-1, idx);
    }

    @Test
    @DisplayName("selectAction — 입력값 반환")
    void selectAction_입력값_반환() {
        // Arrange
        ApprovalView view = viewWithInput("1\n");

        // Act
        String action = view.selectAction();

        // Assert
        assertEquals("1", action);
    }

    @Test
    @DisplayName("confirmProductionInfo — Y 입력 시 true 반환")
    void confirmProductionInfo_Y_입력_true() {
        // Arrange
        ApprovalView view = viewWithInput("Y\n");

        // Act
        boolean result = view.confirmProductionInfo("SiC 파워기판", 30, 170, 206, 164.8);

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("confirmProductionInfo — N 입력 시 false 반환")
    void confirmProductionInfo_N_입력_false() {
        // Arrange
        ApprovalView view = viewWithInput("N\n");

        // Act
        boolean result = view.confirmProductionInfo("SiC 파워기판", 30, 170, 206, 164.8);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("confirmProductionInfo — 부족분/실생산량 정보 출력 포함")
    void confirmProductionInfo_정보_출력_포함() {
        // Arrange
        ApprovalView view = viewWithInput("Y\n");

        // Act
        view.confirmProductionInfo("SiC 파워기판", 30, 170, 206, 164.8);

        // Assert
        String out = outBuf.toString();
        assertTrue(out.contains("SiC 파워기판"), "시료명 포함");
        assertTrue(out.contains("170"), "부족분 포함");
        assertTrue(out.contains("206"), "실생산량 포함");
    }

    @Test
    @DisplayName("displayApprovalResult — 상태 전이 정보 출력")
    void displayApprovalResult_상태전이_출력() {
        // Arrange
        ApprovalView view = viewWithInput("");

        // Act
        view.displayApprovalResult("ORD-001", OrderStatus.RESERVED, OrderStatus.CONFIRMED);

        // Assert
        String out = outBuf.toString();
        assertTrue(out.contains("ORD-001"), "주문번호 포함");
        assertTrue(out.contains("RESERVED"), "이전 상태 포함");
        assertTrue(out.contains("CONFIRMED"), "다음 상태 포함");
    }

    @Test
    @DisplayName("displayMessage — 메시지 출력")
    void displayMessage_출력() {
        // Arrange
        ApprovalView view = viewWithInput("");

        // Act
        view.displayMessage("승인 완료");

        // Assert
        assertTrue(outBuf.toString().contains("승인 완료"));
    }
}
