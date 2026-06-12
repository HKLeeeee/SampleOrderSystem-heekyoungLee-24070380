package model.service;

import model.entity.ProductionJob;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProductionQueueTest {

    private ProductionQueue queue;
    private ProductionJob job1;
    private ProductionJob job2;
    private ProductionJob job3;

    @BeforeEach
    void setUp() {
        queue = new ProductionQueue();
        job1 = new ProductionJob("ORD-001", "S-001", 200, 170, 206, 0.8);
        job2 = new ProductionJob("ORD-002", "S-002", 80, 50, 61, 1.0);
        job3 = new ProductionJob("ORD-003", "S-003", 60, 30, 37, 0.5);
    }

    @Test
    @DisplayName("빈 큐 — 현재 작업 없음")
    void 빈_큐_현재작업_없음() {
        assertNull(queue.getCurrentJob());
    }

    @Test
    @DisplayName("빈 큐 — 대기 목록 없음")
    void 빈_큐_대기없음() {
        assertTrue(queue.getWaitingJobs().isEmpty());
    }

    @Test
    @DisplayName("첫 등록 — 즉시 현재 작업, 대기 0건")
    void 첫_등록_즉시_현재작업() {
        queue.enqueue(job1);
        assertEquals(job1, queue.getCurrentJob());
        assertTrue(queue.getWaitingJobs().isEmpty());
    }

    @Test
    @DisplayName("두 번째 등록 — 현재=job1, 대기=[job2]")
    void 두번째_등록_대기큐() {
        queue.enqueue(job1);
        queue.enqueue(job2);
        assertEquals(job1, queue.getCurrentJob());
        assertEquals(List.of(job2), queue.getWaitingJobs());
    }

    @Test
    @DisplayName("FIFO 순서 보장 — 대기 [job2, job3]")
    void FIFO_순서_보장() {
        queue.enqueue(job1);
        queue.enqueue(job2);
        queue.enqueue(job3);
        List<ProductionJob> waiting = queue.getWaitingJobs();
        assertEquals(job2, waiting.get(0));
        assertEquals(job3, waiting.get(1));
    }

    @Test
    @DisplayName("완료 후 다음 자동 진입 — 현재=job2, 대기=[]")
    void 완료_후_다음_자동진입() {
        queue.enqueue(job1);
        queue.enqueue(job2);
        queue.completeCurrentJob();
        assertEquals(job2, queue.getCurrentJob());
        assertTrue(queue.getWaitingJobs().isEmpty());
    }

    @Test
    @DisplayName("완료 후 큐 비면 현재=null")
    void 완료_후_큐_비면_현재_null() {
        queue.enqueue(job1);
        queue.completeCurrentJob();
        assertNull(queue.getCurrentJob());
        assertTrue(queue.getWaitingJobs().isEmpty());
    }

    @Test
    @DisplayName("size() — 현재 + 대기 포함 전체 수")
    void 총_대기수_포함_현재() {
        queue.enqueue(job1);
        queue.enqueue(job2);
        queue.enqueue(job3);
        assertEquals(3, queue.size());
    }

    @Test
    @DisplayName("getWaitingJobs() — 반환 리스트 수정이 원본에 영향 없음")
    void 대기_목록_불변_반환() {
        queue.enqueue(job1);
        queue.enqueue(job2);
        List<ProductionJob> waiting = queue.getWaitingJobs();
        assertThrows(UnsupportedOperationException.class, () -> waiting.add(job3));
    }
}
