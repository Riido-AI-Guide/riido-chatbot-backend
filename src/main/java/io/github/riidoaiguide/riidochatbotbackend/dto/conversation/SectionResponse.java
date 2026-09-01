package io.github.riidoaiguide.riidochatbotbackend.dto.conversation;

import io.github.riidoaiguide.riidochatbotbackend.domain.AnswerSection;

import java.util.List;

/** 답변 한 덩어리. label로 스타일을 정하고 sources를 근거로 단다. */
public record SectionResponse(String label, String text, List<SourceResponse> sources) {

    static SectionResponse from(AnswerSection section) {
        return new SectionResponse(
                section.getLabel(),
                section.getText(),
                section.getSources().stream().map(SourceResponse::from).toList()
        );
    }
}
