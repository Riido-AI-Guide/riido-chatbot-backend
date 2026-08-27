package io.github.riidoaiguide.riidochatbotbackend.dto.ai;

import com.fasterxml.jackson.annotation.JsonProperty;

public record DocumentDto(
        @JsonProperty("doc_id") String docId,
        String title,
        String section,
        @JsonProperty("source_type") String sourceType,
        @JsonProperty("ord_idx") int ordIdx,
        String content
) {}