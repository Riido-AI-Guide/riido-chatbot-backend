package io.github.riidoaiguide.riidochatbotbackend.dto.ai;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record AskResponse(
        @JsonProperty("raw_query") String rawQuery,
        @JsonProperty("cleaned_query") String cleanedQuery,
        @JsonProperty("needs_search") boolean needsSearch,
        String answer,
        @JsonProperty("doc_ids") List<String> docIds,
        List<DocumentDto> documents
) {}