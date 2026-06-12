# Plan.md — Cycle 13

## Goal
AppContext가 모든 Controller 조립을 담당하도록 리팩터링한다 — MainController 파라미터를 줄이고 Main.java를 단순화한다.

## 현재 문제
- MainController 생성자 파라미터 11개
- Main.java가 View·Controller 생성 + 조립 + 실행 모두 담당 (단일 책임 위반)
- printDashboard()를 위해 Service 3개를 Controller에 직접 주입

## 변경 설계
```
AppContext.buildMainController(Scanner) → MainController
MainController(MainMenuView, Map<Integer,Runnable>, AppContext, Scanner)
  - Map<Integer,Runnable>: 서브컨트롤러 위임 핸들러 묶음
  - AppContext: printDashboard()용 데이터 소스
  - 파라미터 4개로 감소
```

## Test Cases (기존 테스트 회귀 없음 확인)

| 테스트 이름 | 기대값 |
|---|---|
| `기존_MainControllerTest_전체_통과` | 4개 테스트 모두 green |
| `기존_AppContextTest_전체_통과` | 4개 테스트 모두 green |
| `AppContext_buildMainController_반환_비null` | `buildMainController(scanner)` != null |

## Scope

- `AppContext.java` — `buildMainController(Scanner)` 메서드 추가, View·Controller 조립 이동
- `controller/MainController.java` — 생성자를 `(MainMenuView, Map<Integer,Runnable>, AppContext, Scanner)` 로 교체
- `Main.java` — `ctx.buildMainController(scanner).run()` 으로 단순화
- `src/test/java/AppContextTest.java` — `buildMainController` smoke 테스트 추가

## Out of Scope

- 각 서브컨트롤러 내부 로직 변경 없음
- Service 레이어 변경 없음
