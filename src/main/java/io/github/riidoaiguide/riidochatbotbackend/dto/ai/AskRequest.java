package io.github.riidoaiguide.riidochatbotbackend.dto.ai;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * AI /api/v1/ask 요청.
 * 첫 대화면 history는 빈 목록, conversationId는 null — AI가 단일턴으로 처리한다.
 */
public record AskRequest(
        @JsonProperty("query") String query,
        @JsonProperty("history") List<ConversationTurnDto> history,
        @JsonProperty("conversation_id") String conversationId
) {
    /** 첫 대화용 (히스토리 없음) */
    public static AskRequest firstTurn(String query) {
        return new AskRequest(query, List.of(), null);
    }
}
