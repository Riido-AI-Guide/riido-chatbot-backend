package io.github.riidoaiguide.riidochatbotbackend.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/**
 * 답변 섹션이 근거로 삼은 문서 1건.
 * 근거 문서 본문은 AI 쪽에 있으므로 여기엔 화면에 필요한 식별자와 경로만 남긴다.
 */
@Embeddable
public class SourceRef {

    @Column(name = "doc_id", nullable = false, length = 500)
    private String docId;

    // 화면에 그대로 표시하는 문서 경로 (예: "휴지통 > 복구 및 영구 삭제")
    @Column(name = "section", nullable = false, length = 500)
    private String section;

    protected SourceRef() {
    }

    SourceRef(String docId, String section) {
        this.docId = docId;
        this.section = section;
    }

    public String getDocId() {
        return docId;
    }

    public String getSection() {
        return section;
    }
}
