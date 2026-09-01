package io.github.riidoaiguide.riidochatbotbackend.dto.conversation;

import io.github.riidoaiguide.riidochatbotbackend.domain.SourceRef;

/** 섹션의 근거 문서. section은 화면에 그대로 표시하는 문서 경로다. */
public record SourceResponse(String docId, String section) {

    static SourceResponse from(SourceRef source) {
        return new SourceResponse(source.getDocId(), source.getSection());
    }
}
