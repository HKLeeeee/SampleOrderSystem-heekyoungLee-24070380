package dummy;

import model.entity.Sample;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DummyDataGeneratorTest {

    @TempDir
    Path tempDir;

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
}
