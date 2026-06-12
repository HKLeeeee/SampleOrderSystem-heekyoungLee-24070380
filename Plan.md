# Plan.md — Cycle 11

## Goal
모니터링 서비스(FR-4)와 출고 서비스(FR-6)를 구현한다 — REJECTED 제외 상태별 집계, 재고 상태 판정(여유/부족/고갈), 출고 시 재고 차감 + RELEASE 전이가 정확히 동작한다.

## Test Cases

### MonitoringService (FR-4)
| 테스트 이름 | 입력 | 기대값 |
|---|---|---|
| `상태별_집계_REJECTED_제외` | RESERVED×2, CONFIRMED×1, REJECTED×1 | {RESERVED:2, CONFIRMED:1} REJECTED 없음 |
| `재고_고갈` | stock=0 | 상태="고갈" |
| `재고_부족` | stock=30, PRODUCING 주문(qty=200) | 상태="부족" |
| `재고_여유` | stock=200, CONFIRMED 주문(qty=50) | 상태="여유" |
| `주문_없음_여유` | stock=100, 주문 없음 | 상태="여유" |

### ReleaseService (FR-6)
| 테스트 이름 | 입력 | 기대값 |
|---|---|---|
| `출고_성공_재고_차감` | CONFIRMED 주문(qty=50), stock=100 | stock=50, 상태=RELEASE |
| `출고_재고_부족_차단` | CONFIRMED 주문(qty=200), stock=100 | `IllegalStateException` |
| `비CONFIRMED_출고_거부` | RESERVED 주문 출고 시도 | `IllegalStateException` |

## Scope

- `model/service/MonitoringService.java` — `getOrderCountByStatus()` (Map), `getStockStatus(Sample, List<Order>)` → "여유"/"부족"/"고갈"
- `model/service/ReleaseService.java` — `release(Order, Sample)`
- `src/test/java/model/service/MonitoringServiceTest.java`
- `src/test/java/model/service/ReleaseServiceTest.java`

## Out of Scope

- View / Controller — Phase 5 통합 시
- 출고 처리일시 기록 — Order 필드 추가 필요, View 레이어에서 처리
