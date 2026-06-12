# Plan.md — Cycle 3

## Goal
Order 엔티티와 OrderStatus를 구현한다 — 허용된 상태 전이만 통과하고 금지 전이는 예외를 던진다.

## Test Cases

### OrderStatus 허용 전이 (5종)
| 테스트 이름 | 전이 | 기대값 |
|---|---|---|
| `RESERVED_to_CONFIRMED` | RESERVED → CONFIRMED | 성공 |
| `RESERVED_to_PRODUCING` | RESERVED → PRODUCING | 성공 |
| `RESERVED_to_REJECTED` | RESERVED → REJECTED | 성공 |
| `PRODUCING_to_CONFIRMED` | PRODUCING → CONFIRMED | 성공 |
| `CONFIRMED_to_RELEASE` | CONFIRMED → RELEASE | 성공 |

### OrderStatus 금지 전이 (예외)
| 테스트 이름 | 전이 | 기대값 |
|---|---|---|
| `CONFIRMED_to_PRODUCING_금지` | CONFIRMED → PRODUCING | `IllegalStateException` |
| `REJECTED_to_CONFIRMED_금지` | REJECTED → CONFIRMED | `IllegalStateException` |
| `RELEASE_to_CONFIRMED_금지` | RELEASE → CONFIRMED | `IllegalStateException` |
| `PRODUCING_to_RESERVED_금지` | PRODUCING → RESERVED | `IllegalStateException` |

### Order 엔티티
| 테스트 이름 | 입력 | 기대값 |
|---|---|---|
| `Order_정상_생성` | orderId, sampleId, customerName, quantity | 예외 없이 생성, getter 일치, 초기 상태 RESERVED |
| `Order_고객명_빈값_거부` | customerName="" | `IllegalArgumentException` |
| `Order_수량_0_거부` | quantity=0 | `IllegalArgumentException` |
| `Order_수량_음수_거부` | quantity=-1 | `IllegalArgumentException` |
| `Order_상태전이_위임` | order.changeStatus(CONFIRMED) | status == CONFIRMED |

## Scope

- `model/entity/OrderStatus.java` — enum (5종) + `transitionTo(OrderStatus)` 메서드
- `model/entity/Order.java` — 필드, 생성자(검증), getter, `changeStatus(OrderStatus)`
- `src/test/java/model/entity/OrderStatusTest.java`
- `src/test/java/model/entity/OrderTest.java`

## Out of Scope

- 주문번호 생성기 (ORD-YYYYMMDD-NNNN) — Cycle 4
- ProductionQueue — Cycle 5
- Repository / 영속성 — Cycle 6+
- 생산 계산기 — Cycle 4(생산량 계산기와 함께)
