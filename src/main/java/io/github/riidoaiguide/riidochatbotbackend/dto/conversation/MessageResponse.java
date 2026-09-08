package io.github.riidoaiguide.riidochatbotbackend.dto.conversation;

import io.github.riidoaiguide.riidochatbotbackend.domain.Message;

import java.time.Instant;
import java.util.List;
import java.util.Locale;

/**
 * 대화 속 메시지 한 건.
 *
 * <p>assistant 메시지는 title·answerType·sections·qnaUuid와 답한 질문의 id(questionId)를 함께 준다.
 * 사용자 메시지는 이들이 모두 비어 있고, 섹션을 저장하기 전에 쌓인 답변도 sections가 비어 있으므로
 * 그때는 content(평문)로 그린다.
 */
public record MessageResponse(
        Long id,
        String role,
        String content,
        String title,
        String answerType,
        // AI가 이 턴에 붙인 식별자. 사용자 good/bad 평가를 AI 품질 로그와 대조할 때 쓴다
        String qnaUuid,
        // 이 답변이 답한 질문 메시지의 id. 질문 메시지에서는 null
        Long questionId,
        List<SectionResponse> sections,
        Instant createdAt
) {
    public static MessageResponse from(Message message) {
        return new MessageResponse(
                message.getId(),
                message.getRole().name().toLowerCase(Locale.ROOT),
                message.getContent(),
                message.getTitle(),
                message.getAnswerType(),
                message.getQnaUuid(),
                message.getQuestionId(),
                message.getSections().stream().map(SectionResponse::from).toList(),
                message.getCreatedAt()
        );
    }
}
