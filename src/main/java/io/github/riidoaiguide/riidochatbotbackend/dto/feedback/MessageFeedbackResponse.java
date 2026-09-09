package io.github.riidoaiguide.riidochatbotbackend.dto.feedback;

import io.github.riidoaiguide.riidochatbotbackend.domain.FeedbackRating;
import io.github.riidoaiguide.riidochatbotbackend.domain.FeedbackReason;
import io.github.riidoaiguide.riidochatbotbackend.domain.MessageFeedback;

import java.time.Instant;

/**
 * 저장된 평가. 평가 API의 응답이자, 대화 상세의 메시지마다 실려 나가는 형태이기도 하다
 * (대화를 다시 열었을 때 이미 누른 평가를 그려야 한다).
 */
public record MessageFeedbackResponse(
        Long messageId,
        String qnaUuid,
        FeedbackRating rating,
        // 상세사유를 고르지 않았으면 둘 다 null
        FeedbackReason reason,
        String reasonLabel,
        Instant updatedAt
) {
    public static MessageFeedbackResponse from(MessageFeedback feedback) {
        FeedbackReason reason = feedback.getReason();
        return new MessageFeedbackResponse(
                feedback.getMessage().getId(),
                feedback.getQnaUuid(),
                feedback.getRating(),
                reason,
                reason == null ? null : reason.getLabel(),
                feedback.getUpdatedAt()
        );
    }
}
