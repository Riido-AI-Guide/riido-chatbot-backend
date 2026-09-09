package io.github.riidoaiguide.riidochatbotbackend.dto.bookmark;

import io.github.riidoaiguide.riidochatbotbackend.domain.Conversation;
import io.github.riidoaiguide.riidochatbotbackend.domain.MessageBookmark;
import io.github.riidoaiguide.riidochatbotbackend.dto.conversation.MessageResponse;
import io.github.riidoaiguide.riidochatbotbackend.dto.feedback.MessageFeedbackResponse;

import java.time.Instant;

/**
 * 담아 둔 메시지 한 건. 목록에서 메시지 본문을 그대로 그릴 수 있게 대화 정보까지 함께 준다
 * (어느 대화에서 담았는지 보여주고, 눌렀을 때 그 대화로 보내려면 conversationId가 필요하다).
 */
public record BookmarkResponse(
        Long conversationId,
        String conversationTitle,
        Instant bookmarkedAt,
        MessageResponse message
) {
    public static BookmarkResponse from(MessageBookmark bookmark, MessageFeedbackResponse feedback) {
        Conversation conversation = bookmark.getMessage().getConversation();
        return new BookmarkResponse(
                conversation.getId(),
                conversation.getTitle(),
                bookmark.getCreatedAt(),
                // 목록에 있다는 것 자체가 담긴 메시지라는 뜻이다
                MessageResponse.from(bookmark.getMessage(), feedback, true)
        );
    }
}
