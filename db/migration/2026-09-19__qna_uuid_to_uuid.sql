-- app.messages / app.message_feedbacks 의 qna_uuid 를 varchar(64) → uuid 로 바꾼다.
--
-- 왜 필요한가
--   qna_uuid 는 AI 서버(public.qna_logs.qna_uuid, answer_evaluations.qna_uuid)와 맞춰 보는 조인 키인데
--   그쪽은 uuid, 이쪽만 varchar 였다. AI 서버가 두 평가를 대조할 때마다 ::text 캐스팅을 끼워야 했고,
--   백엔드가 형식이 깨진 값을 한 줄이라도 쌓으면 그 쿼리가 통째로 죽는 구조였다.
--
-- 언제 돌리는가
--   엔티티의 qnaUuid 가 String → UUID 로 바뀐 커밋을 배포하기 **전에** 한 번 돌린다.
--   ddl-auto: update 는 이미 있는 컬럼의 타입을 바꿔 주지 않으므로, 이 스크립트를 돌리지 않으면
--   "column qna_uuid is of type character varying but expression is of type uuid" 로 저장이 실패한다.
--   DB 를 새로 만드는 경우에는 Hibernate 가 처음부터 uuid 로 만들기 때문에 돌릴 필요가 없다.
--
-- 두 번 돌려도 안전하다 (이미 uuid 면 건너뛴다).

BEGIN;

DO $$
DECLARE
    -- uuid 로 바꿀 수 없는 값. 있으면 ALTER 가 테이블 통째로 실패하므로 먼저 NULL 로 비운다.
    -- (qna_uuid 는 nullable 이고, 값이 없는 옛 답변이 이미 섞여 있다)
    uuid_pattern CONSTANT text :=
        '^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$';
    target record;
    dropped bigint;
BEGIN
    FOR target IN
        SELECT table_name
        FROM information_schema.columns
        WHERE table_schema = 'app'
          AND table_name IN ('messages', 'message_feedbacks')
          AND column_name = 'qna_uuid'
          AND data_type <> 'uuid'
    LOOP
        EXECUTE format(
            'UPDATE app.%I SET qna_uuid = NULL
              WHERE qna_uuid IS NOT NULL AND btrim(qna_uuid) !~* %L',
            target.table_name, uuid_pattern);
        GET DIAGNOSTICS dropped = ROW_COUNT;
        IF dropped > 0 THEN
            RAISE NOTICE 'app.%: uuid 형식이 아닌 qna_uuid % 건을 비웠습니다', target.table_name, dropped;
        END IF;

        EXECUTE format(
            'ALTER TABLE app.%I
               ALTER COLUMN qna_uuid TYPE uuid USING NULLIF(btrim(qna_uuid), '''')::uuid',
            target.table_name);
        RAISE NOTICE 'app.%.qna_uuid → uuid', target.table_name;
    END LOOP;
END $$;

COMMIT;
