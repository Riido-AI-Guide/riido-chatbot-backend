package io.github.riidoaiguide.riidochatbotbackend.dto.ai;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/** 검색으로 가져온 문서. content는 목록 조회 등에서 생략될 수 있어 null 가능. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record DocumentDto(
        @JsonProperty("doc_id") String docId,
        @JsonProperty("title") String title,
        @JsonProperty("section") String section,
        @JsonProperty("source_type") String sourceType,
        @JsonProperty("ord_idx") int ordIdx,
        @JsonProperty("content") String content
) {}
