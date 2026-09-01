package io.github.riidoaiguide.riidochatbotbackend.dto.chat;

import io.github.riidoaiguide.riidochatbotbackend.domain.AnswerSection;
import io.github.riidoaiguide.riidochatbotbackend.dto.ai.AnswerSectionDto;

import java.util.List;

/** 답변 한 덩어리. label로 스타일을 정하고 sources를 근거로 단다. */
public record SectionResponse(String label, String text, List<SourceResponse> sources) {

    /** AI 응답에서 (방금 받은 답변) */
    public static SectionResponse from(AnswerSectionDto section) {
        return new SectionResponse(
                section.label(),
                section.text(),
                section.sources().stream().map(SourceResponse::from).toList()
        );
    }

    /** 저장된 답변에서 (대화 조회) */
    public static SectionResponse from(AnswerSection section) {
        return new SectionResponse(
                section.getLabel(),
                section.getText(),
                section.getSources().stream().map(SourceResponse::from).toList()
        );
    }
}
