package io.github.riidoaiguide.riidochatbotbackend.dto.conversation;

import io.github.riidoaiguide.riidochatbotbackend.domain.Conversation;
import io.github.riidoaiguide.riidochatbotbackend.dto.feedback.MessageFeedbackResponse;

import java.util.List;
import java.util.Map;

/**
 * 대화 하나 전체. 질문 직후에도 같은 형태로 준다 —
 * 방금 받은 답변은 messages의 마지막 항목이며, 제목·섹션·근거를 그대로 달고 있다.
 */
public record ConversationResponse(
        Long conversationId,
        String title,
        List<MessageResponse> messages
) {
    /** 아직 평가가 있을 수 없는 자리(대화를 막 만든 직후)에서 쓴다. */
    public static ConversationResponse from(Conversation conversation) {
        return from(conversation, Map.of());
    }

    /**
     * @param feedbacks 메시지 id → 그 메시지에 남긴 평가. 평가가 없는 메시지는 담기지 않는다
     */
    public static ConversationResponse from(
            Conversation conversation,
            Map<Long, MessageFeedbackResponse> feedbacks
    ) {
        return new ConversationResponse(
                conversation.getId(),
                conversation.getTitle(),
                conversation.getMessages().stream()
                        .map(message -> MessageResponse.from(message, feedbacks.get(message.getId())))
                        .toList()
        );
    }
}
