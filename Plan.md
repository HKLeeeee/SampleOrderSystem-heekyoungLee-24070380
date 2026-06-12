# Plan.md — Cycle 12

## Goal
View·Controller 레이어를 구현하고 Main 루프를 완성한다 — 6개 기능 메뉴가 실데이터로 동작하고 재실행 후 데이터가 유지된다.

## Test Cases (통합 테스트)

| 테스트 이름 | 입력 | 기대값 |
|---|---|---|
| `AppContext_생성_성공` | new AppContext(tempDir) | 예외 없이 모든 서비스·레포 초기화 |
| `AppContext_시료_등록_후_조회` | register → findAll | 1건 반환 |
| `AppContext_주문_접수_승인_출고_흐름` | 시료등록→주문→승인(재고충분)→출고 | 최종 RELEASE, 재고 차감 |
| `AppContext_생산흐름` | 시료등록→주문→승인(재고부족)→생산완료→출고 | 최종 RELEASE, 재고 반영 |

## Scope

- `AppContext.java` — 모든 Service·Repository 조립 (DI 컨테이너 역할)
- `view/SampleView.java` — 시료 관련 콘솔 입출력
- `view/OrderView.java` — 주문 접수 입출력
- `view/ApprovalView.java` — 승인/거절 입출력
- `view/ProductionView.java` — 생산라인 조회 입출력
- `view/MonitoringView.java` — 모니터링 입출력
- `view/ReleaseView.java` — 출고 처리 입출력
- `controller/SampleController.java`
- `controller/OrderController.java`
- `controller/ApprovalController.java`
- `controller/ProductionController.java`
- `controller/MonitoringController.java`
- `controller/ReleaseController.java`
- `controller/MainController.java` — 루프 완성, 요약 대시보드 실데이터
- `Main.java` — AppContext 초기화, 루프 시작
- `src/test/java/AppContextTest.java` — 통합 테스트 4개

## Out of Scope

- 페이지네이션 (시료 목록 10건 이하면 생략 가능)
- 생산 시각 자동 완료 체크 루프 (메뉴 진입 시 수동 "생산 완료 처리" 메뉴로 구현)
