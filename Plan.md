# Plan.md — Cycle 1

## Goal
MVC 패키지 골격과 메인 메뉴 루프(FR-0)를 구성한다 — 빌드·테스트 harness가 1커맨드로 동작하고, 메뉴 진입·종료가 가능하다.

## Test Cases

| 테스트 이름 | 입력 | 기대값 |
|---|---|---|
| `MainController_생성_성공` | `new MainController(...)` | 예외 없이 생성됨 |
| `MainMenuView_메뉴_출력_포함_문자열` | view.displayMainMenu() | `[1]`~`[6]`, `[0]` 포함한 문자열 반환 |
| `MainController_유효_메뉴_번호_0~6` | 입력값 0~6 | `isValidMenuChoice(n)` == true |
| `MainController_유효_범위_외_거부` | 입력값 -1, 7, 99 | `isValidMenuChoice(n)` == false |

## Scope

- `model/` 패키지 (빈 플레이스홀더 — 엔티티 없음, Phase 2 대상)
- `view/MainMenuView` — 메뉴 문자열 반환 메서드
- `controller/MainController` — 메뉴 번호 유효성 검사 메서드, 생성자
- `Main.java` — 진입점 (빌드·실행 확인용, 실제 루프는 stub)
- `./gradlew test` 로 테스트 실행 확인

## Out of Scope

- 실제 메뉴 입력 루프 (Scanner I/O) — Phase 4 각 기능 연결 시
- 요약 대시보드 실데이터 연결 — Phase 4-7에서
- Sample, Order, Repository 구현 — Phase 2~3
- 각 메뉴 기능(1~6) 실제 동작 — Phase 4 순서대로
