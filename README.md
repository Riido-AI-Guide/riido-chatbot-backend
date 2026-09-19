# riido-chatbot-backend

리도 AI 가이드 챗봇의 백엔드 서버입니다. 프론트엔드의 질문을 받아 AI 서버(`/api/v1/ask`)에 전달하고, 답변과 참고 문서를 반환합니다.

- Java 25 / Spring Boot 4.1.1 / PostgreSQL

## 로컬 실행 방법

### 1. 설정 파일 준비

`src/main/resources/application-local.yaml.example`을 복사해서 `application-local.yaml`로 이름을 변경합니다.

```bash
cp src/main/resources/application-local.yaml.example src/main/resources/application-local.yaml
```

### 2. 접속 정보 채우기

`application-local.yaml`의 `<HOST>`, `<PORT>`, `<DATABASE>`, `<USERNAME>`, `<PASSWORD>` 자리에 DB 접속 정보를 입력합니다. 접속 정보는 팀 채널에서 확인하세요.

> `application-local.yaml`은 `.gitignore`에 등록되어 있어 커밋되지 않습니다. 실제 접속 정보를 `.example` 파일이나 `application.yaml`에 넣지 마세요.

### 3. 빌드

```bash
./gradlew clean build -x test
```

### 4. 실행

```bash
./gradlew bootRun
```

서버는 `http://localhost:8080`에서 뜹니다. (`local` 프로파일이 기본 활성화)

## DB 마이그레이션

스키마는 아직 `ddl-auto: update`가 만듭니다. 컬럼을 새로 추가하는 변경은 알아서 반영되지만,
**이미 있는 컬럼의 타입을 바꾸는 변경은 Hibernate가 해 주지 않으므로** `db/migration/`의 SQL을 한 번 직접 돌려야 합니다.

| 스크립트 | 내용 | 언제 |
|---|---|---|
| `db/migration/2026-09-19__qna_uuid_to_uuid.sql` | `messages.qna_uuid`·`message_feedbacks.qna_uuid`를 `varchar(64)` → `uuid`로 변경 | 이 변경을 배포하기 **전에** 한 번 |

- 기존 DB(로컬·운영 공용 Supabase)에만 필요합니다. DB를 새로 만들면 Hibernate가 처음부터 `uuid`로 만듭니다.
- 두 번 돌려도 안전합니다 — 이미 `uuid`면 건너뜁니다.
- Supabase 대시보드의 SQL Editor에 붙여넣거나, `psql "<접속 문자열>" -f db/migration/2026-09-19__qna_uuid_to_uuid.sql`로 돌립니다.

## 참고

- AI 서버 주소는 `application.yaml`의 `app.ai.base-url`에서 설정합니다. 기본값은 `http://localhost:8000`이며, 챗봇 API를 호출하려면 AI 서버가 함께 실행 중이어야 합니다.
- CORS 허용 오리진은 `app.cors.allowed-origin`에서 설정합니다. 기본값은 `http://localhost:5173`입니다.

## API

### `POST /conversations`

새 대화를 시작합니다. 질문을 AI 서버에 전달해 답변을 받고, 대화와 메시지 2건(user/assistant)을 DB에 저장한 뒤 `201 Created`로 반환합니다.

요청

```json
{ "query": "ERD 먼저 짜는 게 나을까?" }
```

응답 (`201`, `Location: /conversations/{conversationId}`)

```json
{
  "conversationId": 12,
  "title": "ERD 먼저 짜는 게 나을까?",
  "messages": [
    { "id": 45, "role": "user", "content": "ERD 먼저 짜는 게 나을까?", "createdAt": "2026-08-24T14:20:01Z" },
    { "id": 46, "role": "assistant", "content": "설명", "createdAt": "2026-08-24T14:20:09Z" }
  ]
}
```

- `title`은 최초 질문(최대 200자)입니다.
- `createdAt`은 UTC ISO-8601(초 단위)입니다.
- `query`가 비어 있으면 `400`, AI 서버 호출에 실패하면 `500`을 반환합니다.

### `POST /api/chat`

대화를 저장하지 않고 AI 답변과 참고 문서만 받아오는 단발성 엔드포인트입니다.

```json
{ "query": "리도 사용법 알려줘" }
```

```json
{
  "answer": "...",
  "sources": [
    { "docId": "guide/...", "title": "프로젝트 일정 자동 생성", "section": "..." }
  ]
}
```
