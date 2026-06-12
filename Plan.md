# Plan.md — Cycle 8

## Goal
주문 접수 서비스(FR-2)를 구현한다 — 등록된 시료만 주문 가능하고, RESERVED 상태로 저장되며 ORD-YYYYMMDD-NNNN 주문번호가 발급된다.

## Test Cases

| 테스트 이름 | 입력 | 기대값 |
|---|---|---|
| `주문_접수_성공` | 등록된 시료ID, 고객명, 수량 | Order 저장, 상태=RESERVED, 주문번호 형식 일치 |
| `미등록_시료_주문_거부` | 미등록 시료ID | `IllegalArgumentException` |
| `주문번호_중복_없음` | 같은 날 3회 접수 | 각각 다른 주문번호 |
| `접수된_주문_RESERVED_목록_조회` | 3건 접수 | findByStatus(RESERVED) == 3 |

## Scope

- `model/service/OrderService.java` — `placeOrder(String sampleId, String customerName, int quantity, String date)` → `Order`, `findByStatus(OrderStatus)`
- `src/test/java/model/service/OrderServiceTest.java`

## Out of Scope

- 승인/거절 — Cycle 9
- View / Controller — Phase 5 통합 시
