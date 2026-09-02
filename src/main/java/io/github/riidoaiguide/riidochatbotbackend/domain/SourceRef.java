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

    // 근거 버튼에 걸 원문 주소. 링크를 못 붙인 문서는 null이다 (AI는 빈 문자열로 준다).
    @Column(name = "url", length = 1000)
    private String url;

    protected SourceRef() {
    }

    SourceRef(String docId, String section, String url) {
        this.docId = docId;
        this.section = section;
        this.url = url;
    }

    public String getDocId() {
        return docId;
    }

    public String getSection() {
        return section;
    }

    public String getUrl() {
        return url;
    }
}
