package io.github.riidoaiguide.riidochatbotbackend.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.BatchSize;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversation conversation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    // 섹션 본문을 이어붙인 평문. AI에 히스토리로 되돌려 보낼 때 쓰고,
    // 섹션이 없는 예전 답변을 그리는 fallback이기도 하다.
    @Column(nullable = false, columnDefinition = "text")
    private String content;

    // 아래 넷은 assistant 답변에만 있다. 사용자 메시지와 섹션 저장 이전의 답변은 비어 있다.
    @Column(length = 200)
    private String title;

    // AI가 준 답변 유형(step, no_answer …). 유형이 늘어날 수 있어 enum으로 굳히지 않는다.
    @Column(name = "answer_type", length = 30)
    private String answerType;

    // AI가 이 턴에 붙인 식별자(UUID). AI가 답변을 자동 채점한 결과가 이 값에 달리므로,
    // 나중에 사용자 good/bad와 대조하거나 대화 삭제 시 품질 로그를 함께 정리할 때 조인 키가 된다.
    @Column(name = "qna_uuid", length = 64)
    private String qnaUuid;

    // 이 답변이 답한 질문 메시지. 답변에만 있고 질문 자신은 비어 있다.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_message_id")
    private Message question;

    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    @BatchSize(size = 100) // 대화 상세는 메시지가 여러 개다. 메시지마다 따로 조회하지 않도록 묶어서 읽는다
    private List<AnswerSection> sections = new ArrayList<>();

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected Message() {
    }

    Message(Conversation conversation, Role role, String content, String title, String answerType,
            String qnaUuid, Message question) {
        this.conversation = conversation;
        this.role = role;
        this.content = content;
        this.title = title;
        this.answerType = answerType;
        this.qnaUuid = qnaUuid;
        this.question = question;
        this.createdAt = Instant.now().truncatedTo(ChronoUnit.SECONDS);
    }

    public AnswerSection addSection(String label, String text) {
        AnswerSection section = new AnswerSection(this, label, text);
        sections.add(section);
        return section;
    }

    public Long getId() {
        return id;
    }

    public Role getRole() {
        return role;
    }

    public String getContent() {
        return content;
    }

    public String getTitle() {
        return title;
    }

    public String getAnswerType() {
        return answerType;
    }

    public String getQnaUuid() {
        return qnaUuid;
    }

    public Message getQuestion() {
        return question;
    }

    /** 답변이 참조하는 질문 메시지의 id. 질문 메시지에서는 null. */
    public Long getQuestionId() {
        return question == null ? null : question.getId();
    }

    public List<AnswerSection> getSections() {
        return sections;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
