package io.github.riidoaiguide.riidochatbotbackend.dto.conversation;

import io.github.riidoaiguide.riidochatbotbackend.domain.Conversation;

import java.util.List;

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
