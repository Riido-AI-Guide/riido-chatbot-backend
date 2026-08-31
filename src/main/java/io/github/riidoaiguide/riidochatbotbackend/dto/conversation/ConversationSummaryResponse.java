package io.github.riidoaiguide.riidochatbotbackend.dto.conversation;

import io.github.riidoaiguide.riidochatbotbackend.domain.Conversation;

import java.time.Instant;

/** 대화 목록의 한 줄. 본문(messages)은 무거우므로 상세 조회에서만 준다. */
public record ConversationSummaryResponse(
        Long conversationId,
        String title,
        Instant createdAt
) {
    public static ConversationSummaryResponse from(Conversation conversation) {
        return new ConversationSummaryResponse(
                conversation.getId(),
                conversation.getTitle(),
                conversation.getCreatedAt()
        );
    }
}
