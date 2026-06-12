package dummy;

import model.entity.Sample;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DummyDataGeneratorTest {

    @TempDir
    Path tempDir;

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

    @Test
    @DisplayName("더미 시료 10개 생성")
    void 더미_시료_10개_생성() {
        DummyDataGenerator gen = new DummyDataGenerator(tempDir.resolve("samples.json").toString());
        List<Sample> result = gen.generateSamples(10);
        assertEquals(10, result.size());
    }

    @Test
    @DisplayName("시료 ID 형식 S-NNN")
    void 시료_ID_형식() {
        DummyDataGenerator gen = new DummyDataGenerator(tempDir.resolve("samples.json").toString());
        List<Sample> result = gen.generateSamples(3);
        assertEquals("S-001", result.get(0).getId());
        assertEquals("S-002", result.get(1).getId());
        assertEquals("S-003", result.get(2).getId());
    }

    @Test
    @DisplayName("시료 수치 유효 범위")
    void 시료_유효값_범위() {
        DummyDataGenerator gen = new DummyDataGenerator(tempDir.resolve("samples.json").toString());
        List<Sample> result = gen.generateSamples(10);
        for (Sample s : result) {
            assertTrue(s.getYield() >= 0.70 && s.getYield() <= 0.99,
                    "수율 범위 초과: " + s.getYield());
            assertTrue(s.getAvgProductionTime() >= 0.2 && s.getAvgProductionTime() <= 1.0,
                    "생산시간 범위 초과: " + s.getAvgProductionTime());
            assertTrue(s.getStock() >= 0, "재고 음수: " + s.getStock());
        }
    }

    @Test
    @DisplayName("generate — 파일 생성 확인")
    void generate_파일_생성_확인() {
        // Arrange
        Path outputPath = tempDir.resolve("samples.json");
        DummyDataGenerator gen = new DummyDataGenerator(outputPath.toString());

        // Act
        gen.generate();

        // Assert
        assertTrue(Files.exists(outputPath), "파일이 생성되어야 함");
    }

    @Test
    @DisplayName("generate — count/seed 지정 파일 생성 확인")
    void generate_count_seed_파일_생성() {
        // Arrange
        Path outputPath = tempDir.resolve("samples5.json");
        DummyDataGenerator gen = new DummyDataGenerator(outputPath.toString());

        // Act
        gen.generate(5, 12345L);

        // Assert
        assertTrue(Files.exists(outputPath), "파일이 생성되어야 함");
        List<Sample> loaded = gen.generateSamples(5, 12345L);
        assertEquals(5, loaded.size());
    }

    @Test
    @DisplayName("formatTime — 정수 생산시간 표시")
    void formatTime_정수_표시() {
        // Act
        String result = DummyDataGenerator.formatTime(1.0);

        // Assert
        assertEquals("1 min/ea", result);
    }

    @Test
    @DisplayName("formatTime — 소수점 생산시간 표시")
    void formatTime_소수점_표시() {
        // Act
        String result = DummyDataGenerator.formatTime(0.8);

        // Assert
        assertEquals("0.8 min/ea", result);
    }

    @Test
    @DisplayName("기본 생성자 — DEFAULT_OUTPUT_PATH 사용")
    void 기본_생성자_사용() {
        // Act / Assert — 예외 없이 생성자 호출 가능
        assertDoesNotThrow(() -> new DummyDataGenerator());
    }

    @Test
    @DisplayName("generate — 출력에 시료 정보 포함")
    void generate_출력에_시료정보_포함() {
        // Arrange
        Path outputPath = tempDir.resolve("samples.json");
        DummyDataGenerator gen = new DummyDataGenerator(outputPath.toString());

        // Act
        gen.generate();

        // Assert
        String out = outBuf.toString();
        assertTrue(out.contains("Dummy"), "Dummy 포함");
        assertTrue(out.contains("min/ea"), "min/ea 포함");
    }
}
