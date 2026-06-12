# Plan.md — Cycle 6

## Goal
영속성 계층을 구현한다 — SampleRepository·OrderRepository 인터페이스와 JSON 구현체, ProductionQueue 상태 저장소를 갖추고 재실행 후에도 데이터가 유지된다.

## Test Cases

### JsonSampleRepository
| 테스트 이름 | 입력 | 기대값 |
|---|---|---|
| `시료_저장_후_조회` | save(sample) → findById(id) | 동일 sample 반환 |
| `전체_목록_조회` | save(s1), save(s2) → findAll() | [s1, s2] |
| `중복_ID_덮어쓰기` | save(s1), save(s1_수정) → findById(id) | 수정된 s1 |
| `없는_ID_조회` | findById("없음") | Optional.empty() |
| `파일_손상_빈_목록_복구` | JSON 파일 내용 "invalid" → findAll() | 빈 목록 (예외 없음) |

### JsonOrderRepository
| 테스트 이름 | 입력 | 기대값 |
|---|---|---|
| `주문_저장_후_조회` | save(order) → findById(orderId) | 동일 order 반환 |
| `상태별_조회` | save(reserved), save(confirmed) → findByStatus(RESERVED) | [reserved]만 반환 |
| `주문_상태_변경_후_저장_재조회` | order.changeStatus(CONFIRMED) → save → findById | CONFIRMED 상태 |

### JsonProductionQueueRepository
| 테스트 이름 | 입력 | 기대값 |
|---|---|---|
| `큐_저장_후_로드_순서_유지` | save([job1,job2,job3]) → load() | [job1, job2, job3] 순서 유지 |
| `빈_큐_저장_로드` | save([]) → load() | 빈 리스트 |

## Scope

- `build.gradle.kts` — Gson 의존성 추가 (`com.google.code.gson:gson:2.10.1`)
- `model/repository/SampleRepository.java` — 인터페이스 (save, findAll, findById, delete)
- `model/repository/OrderRepository.java` — 인터페이스 (save, findAll, findById, delete, findByStatus)
- `model/repository/ProductionQueueRepository.java` — 인터페이스 (save, load)
- `model/repository/GsonConfig.java` — Gson 싱글톤
- `model/repository/JsonSampleRepository.java` — JSON 구현체
- `model/repository/JsonOrderRepository.java` — JSON 구현체
- `model/repository/JsonProductionQueueRepository.java` — JSON 구현체 (JobList 저장)
- `src/test/java/model/repository/` — 테스트 (임시 파일 사용, @TempDir)

## Out of Scope

- 앱 시작 시 로드 / Main 연결 — Phase 4 기능 구현 시
- OrderIdGenerator 순번 영속화 — Phase 4-2 주문 접수 구현 시
