package view;

import model.entity.Sample;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class SampleViewTest {

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

    private SampleView viewWithInput(String input) {
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        return new SampleView(scanner);
    }

    @Test
    @DisplayName("displaySampleMenu — 메뉴 항목 포함 출력")
    void displaySampleMenu_메뉴_출력() {
        // Arrange
        SampleView view = viewWithInput("");

        // Act
        view.displaySampleMenu();

        // Assert
        String out = outBuf.toString();
        assertTrue(out.contains("[1]"), "[1] 포함");
        assertTrue(out.contains("[2]"), "[2] 포함");
        assertTrue(out.contains("[3]"), "[3] 포함");
        assertTrue(out.contains("[0]"), "[0] 포함");
    }

    @Test
    @DisplayName("displaySampleList — 시료 목록 출력에 min/ea 포함")
    void displaySampleList_목록_출력_minEa_포함() {
        // Arrange
        SampleView view = viewWithInput("0\n");
        List<Sample> samples = List.of(
                new Sample("S-001", "SiC 파워기판", 0.8, 0.92, 100)
        );

        // Act
        view.displaySampleList(samples);

        // Assert
        String out = outBuf.toString();
        assertTrue(out.contains("min/ea"), "min/ea 포함");
        assertTrue(out.contains("S-001"), "시료 ID 포함");
        assertTrue(out.contains("SiC 파워기판"), "시료명 포함");
        assertTrue(out.contains("100"), "재고 포함");
    }

    @Test
    @DisplayName("displaySampleList — 빈 목록 처리")
    void displaySampleList_빈_목록() {
        // Arrange
        SampleView view = viewWithInput("");

        // Act
        view.displaySampleList(List.of());

        // Assert
        assertTrue(outBuf.toString().contains("등록된 시료가 없습니다"));
    }

    @Test
    @DisplayName("displaySampleList — 평균 생산시간 정수 표시 (1 min/ea)")
    void displaySampleList_정수_시간_표시() {
        // Arrange
        SampleView view = viewWithInput("0\n");
        List<Sample> samples = List.of(
                new Sample("S-002", "정수시료", 1.0, 0.90, 50)
        );

        // Act
        view.displaySampleList(samples);

        // Assert
        assertTrue(outBuf.toString().contains("1 min/ea"), "정수 시간 표시");
    }

    @Test
    @DisplayName("displaySampleList — 페이지 2개 이상일 때 N 입력으로 다음 페이지")
    void displaySampleList_페이지네이션_다음페이지() {
        // Arrange
        SampleView view = viewWithInput("N\n0\n");
        List<Sample> samples = List.of(
                new Sample("S-001", "시료1", 0.8, 0.92, 10),
                new Sample("S-002", "시료2", 0.8, 0.92, 20),
                new Sample("S-003", "시료3", 0.8, 0.92, 30),
                new Sample("S-004", "시료4", 0.8, 0.92, 40),
                new Sample("S-005", "시료5", 0.8, 0.92, 50),
                new Sample("S-006", "시료6", 0.8, 0.92, 60)
        );

        // Act / Assert — 예외 없이 완료
        assertDoesNotThrow(() -> view.displaySampleList(samples));
        String out = outBuf.toString();
        assertTrue(out.contains("S-001"), "첫 페이지 포함");
    }

    @Test
    @DisplayName("inputNewSample — Scanner 입력으로 SampleInput 반환")
    void inputNewSample_SampleInput_반환() {
        // Arrange
        SampleView view = viewWithInput("S-TEST\n테스트시료\n0.5\n0.9\n100\n");

        // Act
        SampleView.SampleInput input = view.inputNewSample();

        // Assert
        assertEquals("S-TEST", input.id());
        assertEquals("테스트시료", input.name());
        assertEquals(0.5, input.avgProductionTime());
        assertEquals(0.9, input.yield());
        assertEquals(100, input.stock());
    }

    @Test
    @DisplayName("inputSearchKeyword — 입력 키워드 반환")
    void inputSearchKeyword_키워드_반환() {
        // Arrange
        SampleView view = viewWithInput("SiC\n");

        // Act
        String keyword = view.inputSearchKeyword();

        // Assert
        assertEquals("SiC", keyword);
    }

    @Test
    @DisplayName("readLine — 입력값 반환")
    void readLine_입력값_반환() {
        // Arrange
        SampleView view = viewWithInput("1\n");

        // Act
        String result = view.readLine();

        // Assert
        assertEquals("1", result);
    }

    @Test
    @DisplayName("displayMessage — 메시지 출력")
    void displayMessage_출력() {
        // Arrange
        SampleView view = viewWithInput("");

        // Act
        view.displayMessage("등록 완료");

        // Assert
        assertTrue(outBuf.toString().contains("등록 완료"));
    }
}
