package view;

import model.entity.OrderStatus;
import model.entity.Sample;
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

class MonitoringViewTest {

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

    private MonitoringView viewWithInput(String input) {
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        return new MonitoringView(scanner);
    }

    @Test
    @DisplayName("readSubMenu — Scanner 입력값 반환")
    void readSubMenu_입력값_반환() {
        // Arrange
        MonitoringView view = viewWithInput("1\n");

        // Act
        String result = view.readSubMenu();

        // Assert
        assertEquals("1", result);
    }

    @Test
    @DisplayName("readSubMenu — 메뉴 헤더 출력 포함")
    void readSubMenu_메뉴_헤더_출력() {
        // Arrange
        MonitoringView view = viewWithInput("0\n");

        // Act
        view.readSubMenu();

        // Assert
        String out = outBuf.toString();
        assertTrue(out.contains("[4]"), "모니터링 헤더 포함");
        assertTrue(out.contains("[1]"), "[1] 주문량 확인 포함");
        assertTrue(out.contains("[2]"), "[2] 재고량 확인 포함");
    }

    @Test
    @DisplayName("displayOrderCounts — 상태별 건수 출력")
    void displayOrderCounts_상태별_건수_출력() {
        // Arrange
        MonitoringView view = viewWithInput("");
        Map<OrderStatus, Long> counts = Map.of(
                OrderStatus.RESERVED, 3L,
                OrderStatus.CONFIRMED, 2L,
                OrderStatus.PRODUCING, 1L,
                OrderStatus.RELEASE, 5L
        );

        // Act
        view.displayOrderCounts(counts);

        // Assert
        String out = outBuf.toString();
        assertTrue(out.contains("RESERVED"), "RESERVED 포함");
        assertTrue(out.contains("CONFIRMED"), "CONFIRMED 포함");
        assertTrue(out.contains("PRODUCING"), "PRODUCING 포함");
        assertTrue(out.contains("RELEASE"), "RELEASE 포함");
        assertTrue(out.contains("3건"), "RESERVED 3건 포함");
        assertTrue(out.contains("2건"), "CONFIRMED 2건 포함");
    }

    @Test
    @DisplayName("displayOrderCounts — 빈 map 이어도 기본 0건 출력")
    void displayOrderCounts_빈_map_기본값() {
        // Arrange
        MonitoringView view = viewWithInput("");

        // Act
        view.displayOrderCounts(Map.of());

        // Assert
        String out = outBuf.toString();
        assertTrue(out.contains("0건"), "0건 포함");
    }

    @Test
    @DisplayName("displayStockStatus — 시료 정보와 상태 출력 (여유)")
    void displayStockStatus_여유_상태_출력() {
        // Arrange
        MonitoringView view = viewWithInput("");
        Sample sample = new Sample("S-001", "SiC 파워기판", 0.8, 0.92, 200);

        // Act
        view.displayStockStatus(List.of(sample), s -> "여유");

        // Assert
        String out = outBuf.toString();
        assertTrue(out.contains("SiC 파워기판"), "시료명 포함");
        assertTrue(out.contains("200"), "재고 포함");
        assertTrue(out.contains("여유"), "여유 상태 포함");
        assertTrue(out.contains("100%"), "100% 포함");
    }

    @Test
    @DisplayName("displayStockStatus — 부족 상태 출력")
    void displayStockStatus_부족_상태_출력() {
        // Arrange
        MonitoringView view = viewWithInput("");
        Sample sample = new Sample("S-001", "GaN 에피택셜", 0.5, 0.90, 30);

        // Act
        view.displayStockStatus(List.of(sample), s -> "부족");

        // Assert
        String out = outBuf.toString();
        assertTrue(out.contains("부족"), "부족 상태 포함");
        assertTrue(out.contains("30"), "재고 포함");
    }

    @Test
    @DisplayName("displayStockStatus — 고갈 상태 출력 (재고 0)")
    void displayStockStatus_고갈_상태_출력() {
        // Arrange
        MonitoringView view = viewWithInput("");
        Sample sample = new Sample("S-001", "SOI 웨이퍼", 0.8, 0.92, 0);

        // Act
        view.displayStockStatus(List.of(sample), s -> "고갈");

        // Assert
        String out = outBuf.toString();
        assertTrue(out.contains("고갈"), "고갈 상태 포함");
        assertTrue(out.contains("0%"), "0% 포함");
    }

    @Test
    @DisplayName("displayStockStatus — 빈 시료 목록")
    void displayStockStatus_빈_목록() {
        // Arrange
        MonitoringView view = viewWithInput("");

        // Act
        view.displayStockStatus(List.of(), s -> "여유");

        // Assert
        String out = outBuf.toString();
        assertTrue(out.contains("등록된 시료가 없습니다"), "없음 메시지 포함");
    }

    @Test
    @DisplayName("displayMessage — 메시지 출력")
    void displayMessage_출력() {
        // Arrange
        MonitoringView view = viewWithInput("");

        // Act
        view.displayMessage("테스트 메시지");

        // Assert
        assertTrue(outBuf.toString().contains("테스트 메시지"));
    }
}
