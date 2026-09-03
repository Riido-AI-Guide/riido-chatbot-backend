package io.github.riidoaiguide.riidochatbotbackend.dto.conversation;

import io.github.riidoaiguide.riidochatbotbackend.domain.SourceRef;

/**
 * 섹션의 근거 문서.
 *
 * <p>화면에 표시하는 건 section(문서 경로)이고, 이동만 url로 한다 —
 * url의 앵커는 GitBook이 발행한 id라 사람이 읽을 수 있는 형태가 아니다(예: {@code #undefined-2}).
 * 링크를 못 붙인 문서는 url이 null이므로 버튼을 걸기 전에 확인해야 한다.
 */
public record SourceResponse(String docId, String section, String url) {

    static SourceResponse from(SourceRef source) {
        return new SourceResponse(source.getDocId(), source.getSection(), source.getUrl());
    }
}
