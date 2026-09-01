package io.github.riidoaiguide.riidochatbotbackend.dto.chat;

import io.github.riidoaiguide.riidochatbotbackend.dto.ai.AskResponse;

import java.util.List;

/**
 * 단일턴 질문(/api/chat)의 답변. 대화로 저장하는 쪽은 메시지마다 같은 필드를 들고 있다.
 *
 * <p>title은 이 답변의 제목(말풍선 제목)이다.
 */
public record AnswerResponse(
        String title,
        String answerType,
        List<SectionResponse> sections
) {
    public static AnswerResponse from(AskResponse ai) {
        return new AnswerResponse(
                ai.title(),
                ai.answerType(),
                ai.answers().stream().map(SectionResponse::from).toList()
        );
    }
}
