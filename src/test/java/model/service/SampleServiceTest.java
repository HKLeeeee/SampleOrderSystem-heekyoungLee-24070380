package model.service;

import model.entity.Sample;
import model.repository.SampleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import model.repository.JsonSampleRepository;

import static org.junit.jupiter.api.Assertions.*;

class SampleServiceTest {

    @TempDir
    Path tempDir;

    private SampleService service;

    @BeforeEach
    void setUp() {
        SampleRepository repo = new JsonSampleRepository(tempDir.resolve("samples.json").toString());
        service = new SampleService(repo);
    }

    @Test
    @DisplayName("시료 등록 성공")
    void register_success() {
        Sample s = new Sample("S-001", "SiC 웨이퍼", 0.8, 0.92, 0);
        service.register(s);
        assertEquals(1, service.findAll().size());
    }

    @Test
    @DisplayName("중복 ID 등록 거부")
    void register_duplicateId_throws() {
        service.register(new Sample("S-001", "웨이퍼A", 0.8, 0.92, 0));
        assertThrows(IllegalArgumentException.class,
                () -> service.register(new Sample("S-001", "웨이퍼B", 0.8, 0.92, 0)));
    }

    @Test
    @DisplayName("전체 조회 — 3개 반환")
    void findAll_returnsAll() {
        service.register(new Sample("S-001", "A", 0.8, 0.92, 0));
        service.register(new Sample("S-002", "B", 0.8, 0.92, 0));
        service.register(new Sample("S-003", "C", 0.8, 0.92, 0));
        assertEquals(3, service.findAll().size());
    }

    @Test
    @DisplayName("이름 부분일치 검색")
    void searchByName_partialMatch() {
        service.register(new Sample("S-001", "SiC 웨이퍼 6인치", 0.8, 0.92, 0));
        service.register(new Sample("S-002", "GaN 파워기판", 0.8, 0.92, 0));
        service.register(new Sample("S-003", "SiC 웨이퍼 8인치", 0.8, 0.92, 0));

        List<Sample> result = service.search("웨이퍼");
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(s -> s.getName().contains("웨이퍼")));
    }

    @Test
    @DisplayName("ID 검색")
    void ID_검색() {
        service.register(new Sample("S-001", "A", 0.8, 0.92, 0));
        service.register(new Sample("S-002", "B", 0.8, 0.92, 0));

        List<Sample> result = service.search("S-001");
        assertEquals(1, result.size());
        assertEquals("S-001", result.get(0).getId());
    }

    @Test
    @DisplayName("검색 결과 없음 — 빈 리스트")
    void search_noResult() {
        service.register(new Sample("S-001", "A", 0.8, 0.92, 0));
        assertTrue(service.search("없는키워드").isEmpty());
    }

    @Test
    @DisplayName("등록 없이 전체 조회 — 빈 리스트")
    void findAll_empty() {
        assertTrue(service.findAll().isEmpty());
    }

    @Test
    @DisplayName("findById — 등록된 시료 반환")
    void findById_등록된_시료_반환() {
        service.register(new Sample("S-001", "SiC 웨이퍼", 0.8, 0.92, 0));
        Optional<Sample> result = service.findById("S-001");
        assertTrue(result.isPresent());
        assertEquals("S-001", result.get().getId());
    }

    @Test
    @DisplayName("findById — 없는 ID는 빈 Optional")
    void findById_없는_ID_빈_Optional() {
        Optional<Sample> result = service.findById("S-999");
        assertTrue(result.isEmpty());
    }
}
