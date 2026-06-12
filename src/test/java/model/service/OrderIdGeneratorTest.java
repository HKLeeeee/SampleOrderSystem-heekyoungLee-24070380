package model.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class OrderIdGeneratorTest {

    private OrderIdGenerator generator;

    @BeforeEach
    void setUp() {
        generator = new OrderIdGenerator();
    }

    @Test
    @DisplayName("첫 번째 주문번호 형식 — ORD-20260612-0001")
    void 첫번째_주문번호_형식() {
        String id = generator.generate("20260612");
        assertEquals("ORD-20260612-0001", id);
    }

    @Test
    @DisplayName("같은 날 순번 증가 — 두 번째 발급 시 0002")
    void 같은날_순번_증가() {
        generator.generate("20260612");
        String id = generator.generate("20260612");
        assertEquals("ORD-20260612-0002", id);
    }

    @Test
    @DisplayName("날짜 바뀌면 순번 리셋 — 0001 재시작")
    void 날짜_바뀌면_순번_리셋() {
        generator.generate("20260612");
        generator.generate("20260612");
        String id = generator.generate("20260613");
        assertEquals("ORD-20260613-0001", id);
    }

    @Test
    @DisplayName("10회 연속 발급 — 모두 다른 값")
    void 중복_없음() {
        Set<String> ids = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            ids.add(generator.generate("20260612"));
        }
        assertEquals(10, ids.size());
    }
}
