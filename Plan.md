# Plan.md — Cycle 7

## Goal
시료 관리 서비스(FR-1)를 구현한다 — 등록(중복 ID 거부), 전체 조회, 이름 검색이 SampleService 단위 테스트로 검증된다.

## Test Cases

### SampleService
| 테스트 이름 | 입력 | 기대값 |
|---|---|---|
| `시료_등록_성공` | 새 ID로 등록 | 저장소에 저장됨 |
| `중복_ID_등록_거부` | 동일 ID 두 번 등록 | `IllegalArgumentException` |
| `전체_조회` | 3개 등록 후 findAll | 3개 반환 |
| `이름_부분일치_검색` | "웨이퍼" 검색 | 이름에 "웨이퍼" 포함된 것만 반환 |
| `ID_검색` | "S-001" 검색 | ID "S-001" 포함된 것 반환 |
| `검색_결과_없음` | "없는키워드" 검색 | 빈 리스트 |
| `등록_없음_전체조회` | 등록 없이 findAll | 빈 리스트 |

## Scope

- `model/service/SampleService.java` — `register(Sample)`, `findAll()`, `search(String keyword)`
- `src/test/java/model/service/SampleServiceTest.java`

## Out of Scope

- SampleView / SampleController — UI 레이어, 테스트 불가, Phase 5 통합 시
- 페이지네이션 — View 레이어 책임
- 시료 수정/삭제 — 명세 범위 외
