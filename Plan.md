# Plan.md — Cycle 9

## Goal
주문 승인/거절 서비스(FR-3)를 구현한다 — 재고 충분 시 즉시 CONFIRMED, 부족 시 생산 큐 등록 + PRODUCING, 거절 시 REJECTED로 전이된다.

## Test Cases

| 테스트 이름 | 입력 | 기대값 |
|---|---|---|
| `승인_재고충분_CONFIRMED` | stock=100, qty=50 | 상태=CONFIRMED, 큐 등록 없음 |
| `승인_재고부족_PRODUCING` | stock=30, qty=200 | 상태=PRODUCING, 큐에 job 등록 |
| `승인_재고부족_큐_job_검증` | stock=30, qty=200, yield=0.92 | job.shortage=170, job.actualQty=206 |
| `거절_REJECTED` | RESERVED 주문 거절 | 상태=REJECTED |
| `비RESERVED_승인_거부` | CONFIRMED 주문 승인 시도 | `IllegalStateException` |
| `비RESERVED_거절_거부` | CONFIRMED 주문 거절 시도 | `IllegalStateException` |

## Scope

- `model/service/ApprovalService.java` — `approve(Order, Sample)` → `ProductionJob | null`, `reject(Order)`
- `src/test/java/model/service/ApprovalServiceTest.java`

## Out of Scope

- 생산 완료 처리 (PRODUCING→CONFIRMED) — Cycle 10
- View / Controller — Phase 5 통합 시
- 재고 예약(가용재고) — 출고 시 차감 방식으로 단순화 (명세 허용)
