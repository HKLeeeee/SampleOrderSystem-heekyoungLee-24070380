package model.repository;

import model.entity.Sample;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class JsonSampleRepositoryDeleteTest {

    @TempDir
    Path tempDir;

    private JsonSampleRepository repo() {
        return new JsonSampleRepository(tempDir.resolve("samples.json").toString());
    }

    @Test
    @DisplayName("delete — 존재하는 ID 삭제 시 true 반환 + 목록에서 제거")
    void delete_존재하는_ID_삭제() {
        // Arrange
        var repo = repo();
        Sample s = new Sample("S-001", "SiC 웨이퍼", 0.8, 0.92, 100);
        repo.save(s);

        // Act
        boolean result = repo.delete("S-001");

        // Assert
        assertTrue(result);
        assertTrue(repo.findAll().isEmpty());
    }

    @Test
    @DisplayName("delete — 없는 ID 삭제 시 false 반환")
    void delete_없는ID_false반환() {
        // Arrange
        var repo = repo();

        // Act
        boolean result = repo.delete("S-NONE");

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("delete — 여러 시료 중 특정 ID만 삭제")
    void delete_여러시료_중_특정ID_삭제() {
        // Arrange
        var repo = repo();
        repo.save(new Sample("S-001", "A", 0.8, 0.92, 10));
        repo.save(new Sample("S-002", "B", 1.0, 0.85, 50));

        // Act
        boolean result = repo.delete("S-001");

        // Assert
        assertTrue(result);
        assertEquals(1, repo.findAll().size());
        assertEquals("S-002", repo.findAll().get(0).getId());
    }
}
