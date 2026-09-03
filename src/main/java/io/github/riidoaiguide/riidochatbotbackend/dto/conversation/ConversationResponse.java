package io.github.riidoaiguide.riidochatbotbackend.dto.conversation;

import io.github.riidoaiguide.riidochatbotbackend.domain.Conversation;

import java.util.List;

/**
 * 대화 하나 전체. 질문 직후에도 같은 형태로 준다 —
 * 방금 받은 답변은 messages의 마지막 항목이며, 제목·섹션·근거를 그대로 달고 있다.
 */
public record ConversationResponse(
        Long conversationId,
        String title,
        List<MessageResponse> messages
) {
    public static ConversationResponse from(Conversation conversation) {
        return new ConversationResponse(
                conversation.getId(),
                conversation.getTitle(),
                conversation.getMessages().stream()
                        .map(MessageResponse::from)
                        .toList()
        );
    }
}
