# Plan.md — Cycle 2

## Goal
Sample 엔티티를 구현한다 — ID·이름·평균생산시간·수율·재고 속성과 입력 검증을 갖춘다.

## Test Cases

| 테스트 이름 | 입력 | 기대값 |
|---|---|---|
| `Sample_정상_생성` | id="S-001", name="SiC 파워기판-6인치", avgTime=0.8, yield=0.92, stock=100 | 예외 없이 생성, getter 값 일치 |
| `Sample_수율_0_이하_거부` | yield=0.0 | `IllegalArgumentException` |
| `Sample_수율_1_초과_거부` | yield=1.1 | `IllegalArgumentException` |
| `Sample_수율_1.0_허용` | yield=1.0 | 예외 없이 생성 |
| `Sample_평균생산시간_0_이하_거부` | avgTime=0.0 | `IllegalArgumentException` |
| `Sample_평균생산시간_음수_거부` | avgTime=-1.0 | `IllegalArgumentException` |
| `Sample_빈_이름_거부` | name="" | `IllegalArgumentException` |
| `Sample_공백만_이름_거부` | name="   " | `IllegalArgumentException` |
| `Sample_null_ID_거부` | id=null | `IllegalArgumentException` |
| `Sample_재고_음수_거부` | stock=-1 | `IllegalArgumentException` |
| `Sample_재고_0_허용` | stock=0 | 예외 없이 생성 |
| `Sample_재고_추가` | addStock(50) 후 조회 | 기존 + 50 |
| `Sample_재고_차감_정상` | deductStock(30), stock=100 | stock=70 |
| `Sample_재고_차감_부족_거부` | deductStock(200), stock=100 | `IllegalStateException` |

## Scope

- `model/entity/Sample.java` — 필드, 생성자(검증 포함), getter, `addStock(int)`, `deductStock(int)`
- `src/test/java/model/entity/SampleTest.java` — 위 테스트 케이스

## Out of Scope

- Order, OrderStatus, ProductionQueue — Cycle 3~5
- Repository / 영속성 — Cycle 6+
- Sample 검색·수정 — Phase 4 기능 구현 시
