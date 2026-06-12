package model.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SampleTest {

    @Test
    @DisplayName("Sample 정상 생성 — getter 값 일치")
    void sample_정상_생성() {
        Sample sample = new Sample("S-001", "SiC 파워기판-6인치", 0.8, 0.92, 100);

        assertEquals("S-001", sample.getId());
        assertEquals("SiC 파워기판-6인치", sample.getName());
        assertEquals(0.8, sample.getAvgProductionTime());
        assertEquals(0.92, sample.getYield());
        assertEquals(100, sample.getStock());
    }

    @Test
    @DisplayName("수율 0.0 이하 거부")
    void sample_수율_0_이하_거부() {
        assertThrows(IllegalArgumentException.class,
                () -> new Sample("S-001", "이름", 0.8, 0.0, 0));
    }

    @Test
    @DisplayName("수율 1.1 초과 거부")
    void sample_수율_1_초과_거부() {
        assertThrows(IllegalArgumentException.class,
                () -> new Sample("S-001", "이름", 0.8, 1.1, 0));
    }

    @Test
    @DisplayName("수율 1.0 허용")
    void sample_수율_1_0_허용() {
        assertDoesNotThrow(() -> new Sample("S-001", "이름", 0.8, 1.0, 0));
    }

    @Test
    @DisplayName("평균생산시간 0.0 거부")
    void sample_평균생산시간_0_거부() {
        assertThrows(IllegalArgumentException.class,
                () -> new Sample("S-001", "이름", 0.0, 0.92, 0));
    }

    @Test
    @DisplayName("평균생산시간 음수 거부")
    void sample_평균생산시간_음수_거부() {
        assertThrows(IllegalArgumentException.class,
                () -> new Sample("S-001", "이름", -1.0, 0.92, 0));
    }

    @Test
    @DisplayName("빈 이름 거부")
    void sample_빈_이름_거부() {
        assertThrows(IllegalArgumentException.class,
                () -> new Sample("S-001", "", 0.8, 0.92, 0));
    }

    @Test
    @DisplayName("공백만 이름 거부")
    void sample_공백만_이름_거부() {
        assertThrows(IllegalArgumentException.class,
                () -> new Sample("S-001", "   ", 0.8, 0.92, 0));
    }

    @Test
    @DisplayName("null ID 거부")
    void sample_null_id_거부() {
        assertThrows(IllegalArgumentException.class,
                () -> new Sample(null, "이름", 0.8, 0.92, 0));
    }

    @Test
    @DisplayName("재고 음수 거부")
    void sample_재고_음수_거부() {
        assertThrows(IllegalArgumentException.class,
                () -> new Sample("S-001", "이름", 0.8, 0.92, -1));
    }

    @Test
    @DisplayName("재고 0 허용")
    void sample_재고_0_허용() {
        assertDoesNotThrow(() -> new Sample("S-001", "이름", 0.8, 0.92, 0));
    }

    @Test
    @DisplayName("재고 추가")
    void sample_재고_추가() {
        Sample sample = new Sample("S-001", "이름", 0.8, 0.92, 100);
        sample.addStock(50);
        assertEquals(150, sample.getStock());
    }

    @Test
    @DisplayName("재고 차감 정상")
    void sample_재고_차감_정상() {
        Sample sample = new Sample("S-001", "이름", 0.8, 0.92, 100);
        sample.deductStock(30);
        assertEquals(70, sample.getStock());
    }

    @Test
    @DisplayName("재고 차감 부족 시 예외")
    void sample_재고_차감_부족_거부() {
        Sample sample = new Sample("S-001", "이름", 0.8, 0.92, 100);
        assertThrows(IllegalStateException.class, () -> sample.deductStock(200));
    }
}
