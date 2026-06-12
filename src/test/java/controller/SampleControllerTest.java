package controller;

import model.entity.Sample;
import model.repository.JsonOrderRepository;
import model.repository.JsonSampleRepository;
import model.service.SampleService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import view.SampleView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class SampleControllerTest {

    @TempDir
    Path tempDir;

    private PrintStream originalOut;
    private ByteArrayOutputStream outBuf;
    private SampleService sampleService;

    @BeforeEach
    void setUp() {
        originalOut = System.out;
        outBuf = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outBuf));

        JsonSampleRepository sampleRepo = new JsonSampleRepository(tempDir.resolve("samples.json").toString());
        sampleService = new SampleService(sampleRepo);
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    private SampleController controllerWithInput(String input) {
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        return new SampleController(sampleService, new SampleView(scanner));
    }

    @Test
    @DisplayName("시료 등록 흐름 — 유효 입력 시 시료 등록 완료")
    void 시료_등록_흐름_정상() {
        // Arrange
        SampleController controller = controllerWithInput("1\nS-TEST\n테스트시료\n0.5\n0.9\n100\n0\n");

        // Act
        controller.run();

        // Assert
        String out = outBuf.toString();
        assertTrue(out.contains("등록 완료"), "등록 완료 메시지 포함");
        assertEquals(1, sampleService.findAll().size());
    }

    @Test
    @DisplayName("시료 목록 — 빈 목록 표시 후 복귀")
    void 시료_목록_빈_목록_복귀() {
        // Arrange
        SampleController controller = controllerWithInput("2\n0\n");

        // Act / Assert
        assertDoesNotThrow(() -> controller.run());
        String out = outBuf.toString();
        assertTrue(out.contains("등록된 시료가 없습니다"), "빈 목록 메시지 포함");
    }

    @Test
    @DisplayName("시료 검색 — 검색 결과 없음")
    void 시료_검색_결과_없음() {
        // Arrange
        SampleController controller = controllerWithInput("3\nSiC\n0\n");

        // Act
        controller.run();

        // Assert
        assertTrue(outBuf.toString().contains("검색 결과가 없습니다"), "없음 메시지 포함");
    }

    @Test
    @DisplayName("시료 검색 — 등록된 시료 검색 성공")
    void 시료_검색_성공() {
        // Arrange
        sampleService.register(new Sample("S-001", "SiC 파워기판", 0.8, 0.92, 100));
        SampleController controller = controllerWithInput("3\nSiC\n0\n");

        // Act
        controller.run();

        // Assert
        assertTrue(outBuf.toString().contains("SiC 파워기판"), "시료명 포함");
    }

    @Test
    @DisplayName("뒤로가기 — 0 입력 시 즉시 복귀")
    void 뒤로가기_즉시_복귀() {
        // Arrange
        SampleController controller = controllerWithInput("0\n");

        // Act / Assert
        assertDoesNotThrow(() -> controller.run());
    }

    @Test
    @DisplayName("유효하지 않은 입력 — 오류 메시지 출력 후 계속")
    void 유효하지_않은_입력_오류_메시지() {
        // Arrange
        SampleController controller = controllerWithInput("9\n0\n");

        // Act
        controller.run();

        // Assert
        assertTrue(outBuf.toString().contains("[오류]"), "오류 메시지 포함");
    }

    @Test
    @DisplayName("중복 ID 등록 시 오류 메시지")
    void 중복_ID_등록_오류() {
        // Arrange
        sampleService.register(new Sample("S-001", "기존시료", 0.8, 0.92, 100));
        SampleController controller = controllerWithInput("1\nS-001\n중복시료\n0.5\n0.9\n50\n0\n");

        // Act
        controller.run();

        // Assert
        assertTrue(outBuf.toString().contains("[오류]"), "중복 오류 메시지 포함");
    }
}
