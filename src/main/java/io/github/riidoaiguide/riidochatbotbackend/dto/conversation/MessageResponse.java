package io.github.riidoaiguide.riidochatbotbackend.dto.conversation;

import io.github.riidoaiguide.riidochatbotbackend.domain.Message;
import io.github.riidoaiguide.riidochatbotbackend.dto.feedback.MessageFeedbackResponse;

import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * 대화 속 메시지 한 건.
 *
 * <p>assistant 메시지는 title·answerType·sections·qnaUuid와 답한 질문의 id(questionId)를 함께 준다.
 * 사용자 메시지는 이들이 모두 비어 있고, 섹션을 저장하기 전에 쌓인 답변도 sections가 비어 있으므로
 * 그때는 content(평문)로 그린다.
 *
 * <p>feedback은 이 답변에 이미 남긴 좋아요/싫어요다. 평가하지 않았으면 null이다.
 * bookmarked는 이 메시지를 담아 뒀는지 여부다.
 */
public record MessageResponse(
        Long id,
        String role,
        String content,
        String title,
        String answerType,
        // AI가 이 턴에 붙인 식별자. 사용자 good/bad 평가를 AI 품질 로그와 대조할 때 쓴다
        UUID qnaUuid,
        // 이 답변이 답한 질문 메시지의 id. 질문 메시지에서는 null
        Long questionId,
        List<SectionResponse> sections,
        // 이미 남긴 평가. 평가 전이면 null
        MessageFeedbackResponse feedback,
        // 담아 둔(북마크한) 메시지인지
        boolean bookmarked,
        Instant createdAt
) {
    public static MessageResponse from(Message message, MessageFeedbackResponse feedback, boolean bookmarked) {
        return new MessageResponse(
                message.getId(),
                message.getRole().name().toLowerCase(Locale.ROOT),
                message.getContent(),
                message.getTitle(),
                message.getAnswerType(),
                message.getQnaUuid(),
                message.getQuestionId(),
                message.getSections().stream().map(SectionResponse::from).toList(),
                feedback,
                bookmarked,
                message.getCreatedAt()
        );
    }
}
