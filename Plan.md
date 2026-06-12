# Plan.md — Cycle 5

## Goal
ProductionQueue를 구현한다 — FIFO 스케줄링으로 등록 순서대로 처리되고, 현재 생산 중인 작업과 대기 큐가 분리된다.

## Test Cases

| 테스트 이름 | 입력 | 기대값 |
|---|---|---|
| `빈_큐_현재작업_없음` | 빈 큐 | `getCurrentJob()` == null |
| `빈_큐_대기없음` | 빈 큐 | `getWaitingJobs().isEmpty()` == true |
| `첫_등록_즉시_현재작업` | enqueue(job1) | `getCurrentJob()` == job1, 대기 0건 |
| `두번째_등록_대기큐` | enqueue(job1), enqueue(job2) | 현재=job1, 대기=[job2] |
| `FIFO_순서_보장` | enqueue(job1,job2,job3) | 대기 순서 [job2, job3] |
| `완료_후_다음_자동진입` | enqueue(job1,job2), completeCurrentJob() | 현재=job2, 대기=[] |
| `완료_후_큐_비면_현재_null` | enqueue(job1), completeCurrentJob() | 현재=null, 대기=[] |
| `총_대기수_포함_현재` | enqueue(job1,job2,job3) | `size()` == 3 |
| `대기_목록_불변_반환` | getWaitingJobs() 수정 시도 | 원본 큐 영향 없음 |

## Scope

- `model/entity/ProductionJob.java` — orderId, sampleId, shortage, actualQty, avgProductionTime 필드, 생성자, getter
- `model/service/ProductionQueue.java` — `enqueue(ProductionJob)`, `getCurrentJob()`, `getWaitingJobs()`, `completeCurrentJob()`, `size()`
- `src/test/java/model/service/ProductionQueueTest.java`

## Out of Scope

- 생산 시각(startTime, expectedEndTime) 계산 — Phase 4-4 생산라인 기능 구현 시
- Repository / 영속성 — Cycle 6+
- Order 상태 자동 전이 (PRODUCING→CONFIRMED) — Phase 4-3/4-4에서
