# Plan — REVISE_PRD 3항목 구현

## Goal
REVISE_PRD.md 3개 항목 구현: 생산시간 단위 변경 + 전체 UI 리디자인 + DummyDataGenerator 모듈

## Scope

### Item 1: 생산시간 단위 변경
- `SampleView.displaySampleList()` 포맷 `%8.2f분` → `%-14s` + `"%.1f min/ea"` 형식

### Item 2: UI 리디자인 (PDF 예시 기준)

**MainMenuView + MainController.printDashboard()**
- ASCII art S-Semi 로고
- "시스템 현황 yyyy-MM-dd HH:mm:ss" 헤더
- 숫자 천단위 콤마 포맷
- 2-column 메뉴 레이아웃
- "선택 > _" 프롬프트

**SampleView**
- 테이블 헤더: ID / 시료명 / 평균 생산시간 / 수율 / 현재 재고
- 페이지네이션 (PAGE_SIZE=5, [N] 다음페이지 / [P] 이전페이지)

**ApprovalView**
- `displayReservedList(List<Order>, Map<String,String> sampleNames)` — 시료명 표시
- `confirmProductionInfo(sampleName, currentStock, shortage, actualQty, totalTime)` — 상세 정보 표시
- 승인 결과: "상태 변경  RESERVED → [PRODUCING/CONFIRMED]" 형식

**MonitoringView + MonitoringController**
- 서브 메뉴: [1] 주문량 확인  [2] 재고량 확인  [0] 뒤로
- 재고 현황: 잔여율 막대 그래프 (████──── 형식) + %

**ProductionView**
- 현재 생산 중: 새 박스 레이아웃 (│ 주문번호 ... │)
- 대기 큐: 시료명 포함, 하단 주석 ("* 부족분 = ...")

**ReleaseView**
- `displayConfirmedList(List<Order>, Map<String,String> sampleNames)` — 시료명 표시
- `displayReleaseResult(Order, LocalDateTime)` — 처리일시 포함

**ApprovalController, ReleaseController, MonitoringController**
- 시료명 Map 빌드하여 View에 전달
- ReleaseController: LocalDateTime.now() 전달

### Item 3: DummyDataGenerator 모듈
- `src/main/java/dummy/DummyDataGenerator.java`
  - 시료 10개 생성 (SampleFactory 로직 차용: 고정 목록 + 랜덤 수치)
  - `data/samples.json` 덮어쓰기
  - 생성 결과 콘솔 출력
- `build.gradle.kts` — `dummy` 태스크 추가
- `README.md` — 더미 데이터 생성 방법 섹션 추가

## Test Cases

| 테스트 이름 | 입력 | 기대값 |
|---|---|---|
| 더미_시료_10개_생성 | count=10 | 10개 Sample, id S-001~S-010 |
| 더미_시료_ID_형식 | count=3 | id "S-001","S-002","S-003" |
| 더미_시료_유효값_범위 | count=10 | yield 0.70~0.99, avgTime 0.2~1.0, stock ≥ 0 |

## Out of Scope
- 주문 더미 생성 (시료만 10개)
- 기존 도메인 로직 변경
- 새 도메인 테스트 (UI 변경은 비즈니스 로직 없음)
