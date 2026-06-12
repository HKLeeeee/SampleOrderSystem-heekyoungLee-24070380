# SampleOrderSystem

가상의 반도체 회사 **S-Semi**의 시료(Sample) 생산·주문·재고·출고를 관리하는 콘솔 기반 시스템.

---

## 시스템 개요

| 항목 | 내용 |
|---|---|
| 운영 형태 | 콘솔 기반, 메뉴 번호 직접 입력 |
| 언어 | Java |
| 빌드 도구 | Gradle (Kotlin DSL) |
| 테스트 | JUnit 5 |
| 아키텍처 | MVC (model / view / controller) |
| 영속성 | 파일 기반 JSON (재실행 후 데이터 유지) |

**역할 흐름:** 고객(이메일 요청) → 주문 담당자(접수) → 생산 담당자(승인·거절·생산·출고)

---

## 빠른 시작

```bash
# 빌드 + 테스트
./gradlew test

# 실행
./gradlew run

# 더미 데이터 생성 (시료 10개 → data/samples.json 덮어쓰기)
./gradlew dummy
```

## 더미 데이터 생성

`./gradlew dummy` 명령으로 반도체 시료 10개를 자동 생성하여 `data/samples.json`에 저장합니다.

```
================================================================
  Dummy 데이터 생성 완료
================================================================
  저장 경로: data/samples.json (10건)

  ID       시료명                  평균 생산시간    수율    현재 재고
  ------------------------------------------------------------------
  S-001    실리콘 웨이퍼-8인치      0.78 min/ea      90%      0ea
  S-002    GaN 에피택셜-4인치       0.95 min/ea      91%    349ea
  ...
================================================================
```

- 고정 시드(42) 사용 → 동일 환경에서 동일 결과 보장
- 수율: 70~99%, 평균 생산시간: 0.2~1.0 min/ea, 재고: 10~500ea (10% 확률로 고갈)
- 기존 `data/samples.json`을 덮어씀 (주문/큐 데이터는 유지)

---

## 메인 메뉴

진입 시 요약 대시보드(등록 시료 수 / 총 재고 / 전체 주문 수 / 생산라인 대기 건수) 표시.

```
[1] 시료 관리
[2] 시료 주문 (예약 접수)
[3] 주문 승인/거절
[4] 모니터링
[5] 생산라인 조회
[6] 출고 처리
[0] 종료
```

---

## 핵심 도메인 규칙

### 주문 상태 머신

```
RESERVED ──거절──> REJECTED  (종결, 모니터링 집계 제외)
RESERVED ──승인──┬─ 재고 충분 ──> CONFIRMED ──출고──> RELEASE
                 └─ 재고 부족 ──> PRODUCING ──생산완료──> CONFIRMED ──출고──> RELEASE
```

허용 전이 이외의 상태 변경은 예외 처리.

### 실생산량 계산 공식

```
부족분      = 주문량 − 현재 재고
실생산량    = ceil(부족분 / (수율 × 0.9))   ← 0.9: 오차 보정계수
총 생산시간 = 평균 생산시간(min/ea) × 실생산량
```

예시: 재고 30, 주문 200, 수율 0.92, 0.8 min/ea  
→ 부족분 170 → 실생산량 `ceil(170 / (0.92 × 0.9))` = **206 ea** / 165 min

### 생산 라인

- 단일 라인, 한 번에 하나의 주문만 처리
- **FIFO 스케줄링** (승인 순서)
- 주문이 들어온 시료에 대해서만 생산 (선제 생산 금지)
- 생산 완료 → 재고에 실생산량 반영 + 주문 `PRODUCING → CONFIRMED`

### 생산 진행 모델

**수동 완료 방식** 채택: 생산라인 조회([5])에서 현재 생산 중인 주문 및 대기 큐를 확인하고, 담당자가 직접 "생산 완료 처리"를 실행한다. 완료 시 재고 반영 및 상태 전이(`PRODUCING → CONFIRMED`)가 즉시 처리된다. 예상 완료 시각은 승인 시각 기준으로 산출하여 표시한다.

### 재고 상태 판정

| 상태 | 조건 |
|---|---|
| 고갈 | 재고 == 0 |
| 부족 | 미출고 주문 수요 > 현재 재고 |
| 여유 | 현재 재고 ≥ 미출고 주문 수요 |

잔여율 = `재고 / (재고 + 미충족 수요)` × 100 (%)

---

## 프로젝트 구조

```
src/
├── main/java/
│   ├── model/
│   │   ├── entity/       # Sample, Order, ProductionJob
│   │   ├── repository/   # 인터페이스 + JSON 구현체
│   │   └── service/      # 비즈니스 로직, 상태 전이
│   ├── view/             # 콘솔 입출력 (비즈니스 로직 금지)
│   ├── controller/       # 흐름 제어, Model ↔ View 중개
│   └── Main.java
└── test/java/            # 도메인 단위 테스트 (JUnit 5)
```

---

## 테스트 현황

JUnit 5 기반 단위 테스트 30개 파일, 전 계층(Model / Controller / View) 커버.

| 계층 | 테스트 파일 |
|---|---|
| Model – Entity | `OrderStatusTest`, `OrderTest`, `SampleTest` |
| Model – Repository | `JsonOrderRepositoryTest`, `JsonSampleRepositoryTest`, `JsonProductionQueueRepositoryTest` 등 |
| Model – Service | `ApprovalServiceTest`, `ProductionCalculatorTest`, `ProductionQueueTest`, `OrderIdGeneratorTest`, `ReleaseServiceTest`, `SampleServiceTest` 등 |
| Controller | `ApprovalControllerTest`, `MainControllerTest`, `MonitoringControllerTest`, `ProductionControllerTest`, `ReleaseControllerTest`, `SampleControllerTest` |
| View | `ApprovalViewTest`, `MonitoringViewTest`, `ProductionViewTest`, `ReleaseViewTest`, `SampleViewTest` |
| 기타 | `DummyDataGeneratorTest`, `AppContextTest` |

주요 검증 항목:
- 주문 상태 전이 (허용/비허용 경계, 불법 전이 예외)
- 실생산량 계산 공식 (수율·보정계수 0.9·ceil, 경계값)
- 생산 큐 FIFO 순서
- 출고 시 재고 차감
- OrderIdGenerator 재시작 후 순번 연속성

---

## 개발 진행 단계

```
Phase 1. 프로젝트 골격 + Harness           ✅ 완료
Phase 2. 도메인 모델 + 단위 테스트         ✅ 완료
Phase 3. 영속성 계층 (JSON Repository)     ✅ 완료
Phase 4. 기능 구현                         ✅ 완료
  4-1 시료 관리  4-2 주문 접수  4-3 승인/거절
  4-4 생산라인  4-5 모니터링  4-6 출고
Phase 5. 메인 메뉴 통합 + 시나리오 검증   ✅ 완료
Phase 6. 리팩터링 + 문서 정리              ✅ 완료
```

---

## 커밋 규칙

`feat:` / `fix:` / `test:` / `refactor:` / `docs:` (Conventional Commits)
