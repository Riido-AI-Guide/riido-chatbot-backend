package io.github.riidoaiguide.riidochatbotbackend.domain;

import jakarta.persistence.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * 나중에 다시 보려고 담아 둔 메시지. 담았거나 담지 않았거나 둘 중 하나라
 * 행의 존재 자체가 곧 상태다 — 켜고 끄는 플래그 컬럼을 따로 두지 않는다.
 *
 * <p>평가(MessageFeedback)와 마찬가지로 누가 담았는지는 두지 않는다.
 * 메시지가 속한 대화의 주인이 곧 담은 사람이다.
 */
@Entity
@Table(
        name = "message_bookmarks",
        // 같은 메시지를 두 번 담을 수는 없다
        uniqueConstraints = @UniqueConstraint(name = "uk_message_bookmarks_message", columnNames = "message_id")
)
public class MessageBookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "message_id", nullable = false)
    private Message message;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected MessageBookmark() {
    }

    public MessageBookmark(Message message) {
        this.message = message;
        this.createdAt = Instant.now().truncatedTo(ChronoUnit.SECONDS);
    }

    public Long getId() {
        return id;
    }

    public Message getMessage() {
        return message;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
