---
name: agentic-tdd
description: >
  Human-in-the-loop TDD 사이클(RED → GREEN → REVIEW). RED 단계에서 Plan.md를
  작성해 사람의 검토를 받은 후 테스트를 작성하고, RED/REVIEW 단계 완료 시 반드시
  사람에게 검토를 요청한다. GREEN/REVIEW 완료 후 커밋. "TDD로 구현", "agentic TDD",
  "human in the loop TDD", "단계별 검토 TDD" 등의 맥락에서 이 스킬을 사용한다.
  절대로 RED와 REVIEW 단계를 사람의 확인 없이 넘어가지 않는다.
---

# Agentic TDD: Human-in-the-Loop RED → GREEN → REVIEW

## 핵심 원칙

사람이 각 사이클의 방향을 결정한다. AI는 계획을 제안하고 실행하지만,
**RED 완료 전**과 **REVIEW 완료 후** 반드시 사람의 확인을 받아야 한다.

```
RED (Plan.md 작성 → 🛑 사람 검토 → Plan.md 커밋 → 테스트 작성 → FAIL 확인 → 🛑 사람 검토)
  ↓ 사람 승인
GREEN (구현 → PASS 확인 → 커밋)
  ↓ 자동 진행
REVIEW (코드 검토 → 커밋 → Plan.md 삭제 커밋 → 🛑 사람 검토)
  ↓ 사람 승인
다음 RED 또는 완료
```

---

## PHASE 1: RED

### Step 1-A: 목표 설정 및 Plan.md 작성

이번 사이클에서 구현할 **단 하나의 동작**을 명확히 정의한다.

Plan.md에 포함할 내용:

- **Goal**: 이번 사이클의 목표 (한 문장)
- **Test cases**: 작성할 테스트 목록 (이름, 입력, 기대값)
- **Scope**: GREEN에서 구현할 코드 범위 (이것 이상은 구현하지 않음)
- **Out of scope**: 이번 사이클에서 구현하지 않을 것

```markdown
# Plan.md

## Goal

[이번 사이클에서 달성할 목표 한 문장]

## Test Cases

| 테스트 이름 | 입력 | 기대값 |
| ----------- | ---- | ------ |
| ...         | ...  | ...    |

## Scope

- 구현할 것: ...

## Out of Scope

- 구현하지 않을 것: ...
```

### 🛑 사람 검토 요청 #1 (Plan.md)

Plan.md 작성 후 **반드시** 다음 메시지를 출력하고 응답을 기다린다:

> **[RED - Plan 검토 요청]**
> Plan.md를 작성했습니다. 검토 후 승인해주시면 Plan을 커밋하고 테스트 코드를 작성하겠습니다.
>
> - 테스트 범위가 적절한가요?
> - Goal 외의 내용이 Scope에 포함되어 있지 않나요?

사람이 수정을 요청하면 Plan.md를 수정한 뒤 다시 검토를 요청한다.
**승인 없이 Plan.md를 커밋하거나 테스트 코드를 작성하지 않는다.**

### Step 1-A-2: Plan.md 커밋

사람이 Plan을 승인하면 즉시 커밋한다.

```bash
git add Plan.md
git commit -m "plan: [Goal 한 줄 요약]"
```

이 커밋이 이번 사이클의 시작점이다. 테스트 코드는 이 커밋 이후에 작성한다.

### Step 1-B: 테스트 코드 작성

Plan.md의 Test Cases를 기반으로 테스트를 작성한다.
Plan에 없는 테스트는 추가하지 않는다.

### Step 1-C: FAIL 확인

테스트를 실행하고 예상한 이유로 실패하는지 확인한다.

```bash
# 이 프로젝트 테스트 실행
./gradlew test
```

- 테스트가 통과? → 이미 구현된 동작. 테스트를 수정한다.
- 컴파일 오류? → 오류만 수정, FAIL 확인 후 계속.
- 실패 이유를 설명할 수 없다면? → 테스트가 잘못됨. 수정한다.

### 🛑 사람 검토 요청 #2 (RED 완료)

FAIL 확인 후 **반드시** 다음 메시지를 출력하고 응답을 기다린다:

> **[RED 완료 - 검토 요청]**
> 테스트가 예상대로 실패하는 것을 확인했습니다.
>
> - 실패 메시지: `[실제 실패 메시지]`
> - 실패 이유: `[기능 미구현 / 컴파일 오류 등]`
>
> GREEN 단계(최소 구현)를 진행할까요?

**사람의 승인 없이 GREEN으로 넘어가지 않는다.**

---

## PHASE 2: GREEN

사람의 승인 후 즉시 진행. 이 단계에서는 중간에 멈추지 않는다.

### Step 2-A: 최소 구현

Plan.md의 Scope 범위 내에서만 코드를 작성한다.

**YAGNI 원칙 적용:**

- Plan에 없는 파라미터, 옵션, 분기 추가 금지
- 테스트를 통과하는 가장 단순한 코드만 작성
- "나중에 필요할 것 같아서" 미리 구현 금지

### Step 2-B: PASS 확인

```bash
./gradlew test
```

- 테스트 실패 → 테스트를 수정하지 말고 프로덕션 코드를 수정한다
- 기존 테스트 깨짐 → 즉시 수정한다

### Step 2-C: GREEN 커밋

모든 테스트 PASS 후 커밋한다.

```bash
git add <변경된 파일>
git commit -m "feat: [Plan의 Goal 내용]"
```

GREEN 커밋 후 자동으로 REVIEW 단계로 진행한다.

---

## PHASE 3: REVIEW

### Step 3-A: 코드 검토

GREEN에서 작성한 코드를 다음 기준으로 검토한다.

| 검토 항목 | 확인 내용                                    |
| --------- | -------------------------------------------- |
| Plan 준수 | Plan.md Scope 외의 코드가 구현되지 않았는가? |
| 최소성    | 테스트를 위해 불필요한 코드가 없는가?        |
| 중복      | 같은 로직이 두 곳 이상에 있는가?             |
| 네이밍    | 변수/메서드 이름이 의도를 드러내는가?        |
| 단일 책임 | 메서드가 한 가지 일만 하는가?                |

### Step 3-B: REVIEW 커밋

리팩터링이 있든 없든 REVIEW 결과를 커밋한다.

```bash
# 리팩터링이 있는 경우
git add <변경된 파일>
git commit -m "refactor: [정리한 내용]"

# 리팩터링이 없는 경우 (빈 커밋으로 REVIEW 완료 기록)
git commit --allow-empty -m "review: [검토 완료, 변경 없음]"
```

### Step 3-C: Plan.md 삭제 커밋

REVIEW 커밋 직후 Plan.md를 소스에서 제거한다.

```bash
git rm Plan.md
git commit -m "chore: remove Plan.md (cycle complete)"
```

이 커밋이 사이클 종료의 명확한 표시다.
다음 사이클이 시작되면 새 Plan.md가 다시 생성된다.

### 🛑 사람 검토 요청 #3 (REVIEW 완료)

REVIEW 커밋 후 **반드시** 다음 메시지를 출력하고 응답을 기다린다:

> **[REVIEW 완료 - 검토 요청]**
> 이번 사이클 REVIEW가 완료됐습니다.
>
> **Plan 준수 여부:**
>
> - ✅ Plan 범위 내 구현됨 / ⚠️ 추가 구현 발견: [내용]
>
> **리팩터링:**
>
> - [리팩터링 내용] / 리팩터링 없음
>
> **다음 제안:**
>
> - [다음 사이클 Goal 제안 또는 완료]
>
> 다음 RED 사이클을 진행할까요, 아니면 다른 방향으로 수정할까요?

사람의 지시에 따라 다음 사이클을 시작하거나 수정한다.
**사람의 응답 없이 다음 RED로 넘어가지 않는다.**

---

## 전체 흐름 요약

```
1. RED
   └─ Plan.md 작성
   └─ 🛑 사람 검토 (Plan 승인)
   └─ 커밋 (plan:)          ← Plan 확정 커밋
   └─ 테스트 작성
   └─ FAIL 확인
   └─ 🛑 사람 검토 (GREEN 진행 승인)

2. GREEN
   └─ 최소 구현
   └─ PASS 확인
   └─ 커밋 (feat:)
   └─ → 자동으로 REVIEW 진행

3. REVIEW
   └─ 코드 검토
   └─ 커밋 (refactor: / review:)
   └─ Plan.md 삭제 커밋 (chore: remove Plan.md)  ← 사이클 종료 표시
   └─ 🛑 사람 검토 (다음 사이클 승인)
```

**한 사이클의 커밋 순서:**

```
plan: ...                        ← RED: Plan 확정
feat: ...                        ← GREEN: 구현 완료
refactor: ... (또는 review: ...) ← REVIEW: 코드 정리
chore: remove Plan.md            ← REVIEW: 사이클 종료
```

---

## Plan.md 위치

프로젝트 루트에 `Plan.md`를 생성/갱신한다.
사이클이 완료되면 내용을 누적하거나 다음 사이클 내용으로 교체한다.

---

## 금지 사항

- RED: 사람 Plan 승인 없이 테스트 코드 작성 시작
- RED: Plan 승인 없이 `plan:` 커밋
- RED: 사람 승인 없이 GREEN 진행
- REVIEW: 사람 승인 없이 다음 RED 진행
- REVIEW: Plan.md 삭제 커밋 없이 사이클 종료
- Plan.md Scope 밖의 코드 구현
- GREEN에서 테스트 실패 시 테스트 수정 (프로덕션 코드를 수정할 것)
- 커밋 없이 다음 단계 진행
