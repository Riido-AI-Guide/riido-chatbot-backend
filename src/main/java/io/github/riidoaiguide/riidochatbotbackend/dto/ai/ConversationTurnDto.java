package io.github.riidoaiguide.riidochatbotbackend.dto.ai;

import com.fasterxml.jackson.annotation.JsonProperty;

/** AI에 보내는 이전 대화 한 턴 (질문-답변 쌍). AI 스펙: {question, answer} */
public record ConversationTurnDto(
        @JsonProperty("question") String question,
        @JsonProperty("answer") String answer
) {}
