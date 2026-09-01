package io.github.riidoaiguide.riidochatbotbackend.domain;

import jakarta.persistence.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

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

    // 이 대화를 만든 사용자. 로그인 기능 이전의 대화는 주인이 없으므로 nullable.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    protected Conversation() {
    }

    public Conversation(String title) {
        this.title = title;
        this.createdAt = Instant.now().truncatedTo(ChronoUnit.SECONDS);
    }

    /** 사용자 질문. 제목·섹션은 답변에만 있으므로 비워 둔다. */
    public Message addQuestion(String content) {
        return add(new Message(this, Role.USER, content, null, null));
    }

    /**
     * AI 답변. 이 턴의 제목과 유형까지 함께 남긴다.
     * 섹션은 반환된 Message에 addSection으로 붙인다.
     *
     * <p>여기 title은 이 답변 말풍선의 제목일 뿐이다 — 대화 제목(Conversation.title)은 건드리지 않는다.
     */
    public Message addAnswer(String content, String title, String answerType) {
        return add(new Message(this, Role.ASSISTANT, content, title, answerType));
    }

    private Message add(Message message) {
        messages.add(message);
        return message;
    }

    public void assignUser(User user) {
        this.user = user;
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
