package io.github.riidoaiguide.riidochatbotbackend.dto.ai;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * AI /api/v1/ask 응답.
 * 평문 answer 필드는 없다 — 본문은 answers(섹션 목록)에만 담겨 오며 어느 경로에서도 비지 않는다.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AskResponse(
        // 이 턴의 식별자. AI가 답변을 자동 채점한 결과가 이 값에 붙으므로 메시지와 함께 저장한다.
        // 백엔드가 발급하는 메시지 id와는 다른 값이다.
        @JsonProperty("qna_uuid") UUID qnaUuid,
        @JsonProperty("raw_query") String rawQuery,
        @JsonProperty("cleaned_query") String cleanedQuery,
        @JsonProperty("needs_search") boolean needsSearch,
        @JsonProperty("conversation_id") String conversationId,
        @JsonProperty("title") String title,
        @JsonProperty("answer_type") String answerType,
        @JsonProperty("answers") List<AnswerSectionDto> answers,
        @JsonProperty("doc_ids") List<String> docIds,
        @JsonProperty("documents") List<DocumentDto> documents
) {
    public AskResponse {
        answers = answers == null ? List.of() : List.copyOf(answers);
        docIds = docIds == null ? List.of() : List.copyOf(docIds);
        documents = documents == null ? List.of() : List.copyOf(documents);
    }

    /**
     * 섹션 본문을 이어붙인 평문. 저장과 히스토리 전달에 쓴다.
     * label은 넣지 않는다 — AI가 재작성에 쓰는 건 앞부분 일부라 라벨이 자리를 잡아먹는다.
     */
    public String answerText() {
        return answers.stream()
                .map(AnswerSectionDto::text)
                .filter(text -> text != null && !text.isBlank())
                .collect(Collectors.joining("\n\n"));
    }
}
