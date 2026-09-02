package io.github.riidoaiguide.riidochatbotbackend.dto.ai;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/** 답변 섹션 하나가 근거로 삼은 문서 1건. AI 스펙: {doc_id, section, url} */
@JsonIgnoreProperties(ignoreUnknown = true)
public record SourceRefDto(
        @JsonProperty("doc_id") String docId,
        @JsonProperty("section") String section,
        // 근거 버튼에 걸 원문 주소. 링크를 못 붙인 문서는 빈 문자열로 온다.
        @JsonProperty("url") String url
) {}
