package io.github.riidoaiguide.riidochatbotbackend.dto.ai;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * 답변을 이루는 한 덩어리. 프론트는 이 단위로 렌더하고 sources를 근거로 단다.
 * label은 섹션 이름(핵심답변/단계별방법 …)이며, 답변 형식이 깨진 경우 빈 문자열로 온다.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AnswerSectionDto(
        @JsonProperty("label") String label,
        @JsonProperty("text") String text,
        @JsonProperty("sources") List<SourceRefDto> sources
) {
    public AnswerSectionDto {
        // sources는 스펙상 선택 필드다. 아래에서 매번 null을 확인하지 않도록 여기서 빈 목록으로 맞춘다.
        sources = sources == null ? List.of() : List.copyOf(sources);
    }
}
