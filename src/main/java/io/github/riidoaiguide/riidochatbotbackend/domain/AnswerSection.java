package io.github.riidoaiguide.riidochatbotbackend.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.List;

/** 답변을 이루는 한 덩어리. 프론트는 이 단위로 렌더하고 sources를 근거로 단다. */
@Entity
@Table(name = "answer_sections")
public class AnswerSection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "message_id", nullable = false)
    private Message message;

    // 섹션 이름(핵심답변/단계별방법 …). 답변 형식이 깨진 경우 빈 문자열로 온다.
    @Column(nullable = false, length = 100)
    private String label;

    @Column(nullable = false, columnDefinition = "text")
    private String text;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "answer_section_sources", joinColumns = @JoinColumn(name = "section_id"))
    @OrderColumn(name = "ord_idx")
    @BatchSize(size = 100) // 대화 상세는 섹션이 여러 개다. 섹션마다 따로 조회하지 않도록 묶어서 읽는다
    private List<SourceRef> sources = new ArrayList<>();

    protected AnswerSection() {
    }

    AnswerSection(Message message, String label, String text) {
        this.message = message;
        this.label = label;
        this.text = text;
    }

    public void addSource(String docId, String section, String url) {
        sources.add(new SourceRef(docId, section, url));
    }

    public Long getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public String getText() {
        return text;
    }

    public List<SourceRef> getSources() {
        return sources;
    }
}
