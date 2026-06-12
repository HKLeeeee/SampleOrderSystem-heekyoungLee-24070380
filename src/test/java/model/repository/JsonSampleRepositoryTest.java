package model.repository;

import model.entity.Sample;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JsonSampleRepositoryTest {

    @TempDir
    Path tempDir;

    private JsonSampleRepository repo() {
        return new JsonSampleRepository(tempDir.resolve("samples.json").toString());
    }

    @Test
    @DisplayName("시료 저장 후 ID 조회")
    void 시료_저장_후_조회() {
        var repo = repo();
        Sample s = new Sample("S-001", "SiC 웨이퍼", 0.8, 0.92, 100);
        repo.save(s);
        var found = repo.findById("S-001");
        assertTrue(found.isPresent());
        assertEquals("SiC 웨이퍼", found.get().getName());
    }

    @Test
    @DisplayName("전체 목록 조회")
    void 전체_목록_조회() {
        var repo = repo();
        repo.save(new Sample("S-001", "A", 0.8, 0.92, 0));
        repo.save(new Sample("S-002", "B", 1.0, 0.85, 50));
        List<Sample> all = repo.findAll();
        assertEquals(2, all.size());
    }

    @Test
    @DisplayName("중복 ID 저장 시 덮어쓰기")
    void 중복_ID_덮어쓰기() {
        var repo = repo();
        repo.save(new Sample("S-001", "원본", 0.8, 0.92, 10));
        repo.save(new Sample("S-001", "수정", 0.8, 0.92, 20));
        assertEquals(1, repo.findAll().size());
        assertEquals("수정", repo.findById("S-001").get().getName());
    }

    @Test
    @DisplayName("없는 ID 조회 — Optional.empty()")
    void 없는_ID_조회() {
        assertTrue(repo().findById("없음").isEmpty());
    }

    @Test
    @DisplayName("파일 손상 시 빈 목록으로 복구")
    void 파일_손상_빈_목록_복구() throws IOException {
        Path file = tempDir.resolve("samples.json");
        Files.writeString(file, "invalid-json");
        var repo = new JsonSampleRepository(file.toString());
        assertDoesNotThrow(() -> {
            List<Sample> all = repo.findAll();
            assertTrue(all.isEmpty());
        });
    }
}
