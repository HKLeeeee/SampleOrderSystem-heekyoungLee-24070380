package model.service;

import model.entity.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
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

    @Test
    @DisplayName("initFrom — 기존 주문 로드 후 순번 연속 (재시작 중복 방지)")
    void initFrom_순번_연속() {
        // 기존 주문 3개 존재 (마지막 순번 0003)
        Order o1 = new Order("ORD-20260612-0001", "S-001", "고객A", 10);
        Order o2 = new Order("ORD-20260612-0002", "S-001", "고객B", 10);
        Order o3 = new Order("ORD-20260612-0003", "S-001", "고객C", 10);

        generator.initFrom(List.of(o1, o2, o3));

        // 재시작 후 다음 발급은 0004이어야 함
        assertEquals("ORD-20260612-0004", generator.generate("20260612"));
    }

    @Test
    @DisplayName("initFrom — 다른 날짜 혼재 시 날짜별 독립 관리")
    void initFrom_날짜별_독립() {
        Order o1 = new Order("ORD-20260611-0005", "S-001", "고객A", 10);
        Order o2 = new Order("ORD-20260612-0002", "S-001", "고객B", 10);

        generator.initFrom(List.of(o1, o2));

        assertEquals("ORD-20260611-0006", generator.generate("20260611"));
        assertEquals("ORD-20260612-0003", generator.generate("20260612"));
    }

    @Test
    @DisplayName("initFrom — 빈 목록으로 초기화 시 0001부터 시작")
    void initFrom_빈목록_0001부터() {
        generator.initFrom(List.of());
        assertEquals("ORD-20260612-0001", generator.generate("20260612"));
    }
}
