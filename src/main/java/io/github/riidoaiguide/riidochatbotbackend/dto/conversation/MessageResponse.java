package io.github.riidoaiguide.riidochatbotbackend.dto.conversation;

import io.github.riidoaiguide.riidochatbotbackend.domain.Message;

import java.time.Instant;
import java.util.List;
import java.util.Locale;

/**
 * 대화 속 메시지 한 건.
 *
 * <p>assistant 메시지는 title·answerType·sections를 함께 준다. 사용자 메시지는 셋 다 비어 있고,
 * 섹션을 저장하기 전에 쌓인 답변도 sections가 비어 있으므로 그때는 content(평문)로 그린다.
 */
public record MessageResponse(
        Long id,
        String role,
        String content,
        String title,
        String answerType,
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
                message.getSections().stream().map(SectionResponse::from).toList(),
                message.getCreatedAt()
        );
    }
}
