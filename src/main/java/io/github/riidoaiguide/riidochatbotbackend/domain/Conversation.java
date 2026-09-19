package io.github.riidoaiguide.riidochatbotbackend.domain;

import jakarta.persistence.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "conversations")
public class Conversation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    private List<Message> messages = new ArrayList<>();

    // 이 대화를 만든 사용자. 주인 없는 대화는 목록에서 영영 찾을 수 없으므로 반드시 있어야 한다.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    protected Conversation() {
    }

    public Conversation(String title, User user) {
        this.title = title;
        this.user = user;
        this.createdAt = Instant.now().truncatedTo(ChronoUnit.SECONDS);
    }

    /** 사용자 질문. 제목·섹션·qnaUuid는 답변에만 있으므로 비워 둔다. */
    public Message addQuestion(String content) {
        return add(new Message(this, Role.USER, content, null, null, null, null));
    }

    /**
     * AI 답변. 이 턴의 제목·유형·식별자와 답한 질문까지 함께 남긴다.
     * 섹션은 반환된 Message에 addSection으로 붙인다.
     *
     * <p>여기 title은 이 답변 말풍선의 제목일 뿐이다 — 대화 제목(Conversation.title)은 건드리지 않는다.
     *
     * @param question 이 답변이 답한 질문 메시지 (addQuestion이 돌려준 것)
     */
    public Message addAnswer(Message question, String content, String title, String answerType, UUID qnaUuid) {
        return add(new Message(this, Role.ASSISTANT, content, title, answerType, qnaUuid, question));
    }

    private Message add(Message message) {
        messages.add(message);
        return message;
    }

    public User getUser() {
        return user;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public List<Message> getMessages() {
        return messages;
    }
}
