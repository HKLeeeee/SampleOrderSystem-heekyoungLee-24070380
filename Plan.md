# Plan.md — Cycle 10

## Goal
생산라인 서비스(FR-5)를 구현한다 — 생산 완료 처리 시 재고가 실생산량만큼 증가하고 주문 상태가 PRODUCING→CONFIRMED로 전이된다.

## 생산 진행 모델
시간 시뮬레이션 방식(A): 생산 시작 시각을 기록하고, 현재 시각과 비교해 완료 여부를 판정. 메뉴 진입마다 자동 체크.

## Test Cases

| 테스트 이름 | 입력 | 기대값 |
|---|---|---|
| `생산완료_재고_증가` | job(actualQty=206), sample(stock=30) → completeProduction | sample.stock=30+206=236 |
| `생산완료_주문_CONFIRMED` | PRODUCING 주문 → completeProduction | order.status=CONFIRMED |
| `생산완료_큐_다음진입` | 큐에 job1,job2 → completeProduction(job1) | getCurrentJob()==job2 |
| `생산완료_큐_비면_IDLE` | 큐에 job1 → completeProduction | getCurrentJob()==null |
| `예상완료시각_계산` | job(actualQty=206, avgTime=0.8), startTime=T | expectedEnd = T + 164.8min |

## Scope

- `model/service/ProductionService.java` — `completeCurrentProduction(Sample, Order)`, `getExpectedEndTime(ProductionJob, LocalDateTime)`
- `src/test/java/model/service/ProductionServiceTest.java`

## Out of Scope

- 자동 시간 체크 루프 — Phase 5 통합 시 Main 루프에서 호출
- View / Controller — Phase 5 통합 시
