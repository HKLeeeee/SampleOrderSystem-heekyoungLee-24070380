# Plan.md — Cycle 4

## Goal
생산 계산기와 주문번호 생성기를 구현한다 — 실생산량 공식(보정계수 0.9 상수화)과 ORD-YYYYMMDD-NNNN 형식 주문번호 발급이 정확히 동작한다.

## Test Cases

### ProductionCalculator
| 테스트 이름 | 입력 (주문량, 재고, 수율, avgTime) | 기대값 |
|---|---|---|
| `부족분_계산` | order=200, stock=30 | shortage=170 |
| `실생산량_PRD_검증예시` | shortage=170, yield=0.92 | actualQty=206 (ceil(170/(0.92×0.9))) |
| `실생산량_소수점_올림` | shortage=50, yield=0.92 | actualQty=61 (ceil(50/0.828)) |
| `총생산시간_계산` | actualQty=206, avgTime=0.8 | totalTime=164.8 |
| `재고_충분_부족분_0_이하` | order=50, stock=100 | shortage=0 이하 → 생산 불필요 (isProductionNeeded=false) |
| `부족분_정확히_0` | order=100, stock=100 | isProductionNeeded=false |

### OrderIdGenerator
| 테스트 이름 | 입력 | 기대값 |
|---|---|---|
| `첫번째_주문번호_형식` | date="20260612", 첫 발급 | "ORD-20260612-0001" |
| `같은날_순번_증가` | date="20260612", 두 번째 | "ORD-20260612-0002" |
| `날짜_바뀌면_순번_리셋` | date="20260613" | "ORD-20260613-0001" |
| `중복_없음` | 10회 연속 발급 | 모두 다른 값 |

## Scope

- `model/service/ProductionCalculator.java` — 정적 유틸 클래스, `CORRECTION_FACTOR = 0.9` 상수, `calcShortage`, `calcActualQty`, `calcTotalTime`, `isProductionNeeded`
- `model/service/OrderIdGenerator.java` — 날짜별 순번 관리, `generate(String date)` 메서드
- `src/test/java/model/service/ProductionCalculatorTest.java`
- `src/test/java/model/service/OrderIdGeneratorTest.java`

## Out of Scope

- ProductionQueue (FIFO) — Cycle 5
- Repository / 영속성 — Cycle 6+
- 주문번호를 파일에 저장하는 영속성 — Cycle 6+
