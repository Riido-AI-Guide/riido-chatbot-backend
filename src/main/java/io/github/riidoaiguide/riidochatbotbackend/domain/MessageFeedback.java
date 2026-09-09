package io.github.riidoaiguide.riidochatbotbackend.domain;

import jakarta.persistence.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * 답변 한 건에 대한 사용자 평가. 메시지당 한 행이며, 두 번에 나뉘어 채워진다.
 *
 * <p>1차로 좋아요/싫어요(rating)가 저장되고, 사용자가 이어서 상세사유를 고르면 같은 행을 고쳐 쓴다.
 * 사유창을 그냥 닫을 수 있으므로 reason은 비어 있을 수 있다 — 그래도 평가 자체는 남는다.
 */
@Entity
@Table(
        name = "message_feedbacks",
        // 메시지당 평가는 하나. 다시 누르면 새 행이 아니라 이 행을 고친다.
        uniqueConstraints = @UniqueConstraint(name = "uk_message_feedbacks_message", columnNames = "message_id")
)
public class MessageFeedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "message_id", nullable = false)
    private Message message;

    // 평가한 답변의 qna_uuid를 그대로 복사해 둔다. AI 품질 로그와 대조할 때 쓰는 값이라
    // 메시지를 타고 들어가지 않고 여기서 바로 읽을 수 있게 둔다.
    // 섹션 저장 이전에 쌓인 옛 답변에는 없으므로 비어 있을 수 있다.
    @Column(name = "qna_uuid", length = 64)
    private String qnaUuid;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private FeedbackRating rating;

    // 상세사유를 고르지 않고 창을 닫으면 비어 있다.
    @Enumerated(EnumType.STRING)
    @Column(length = 40)
    private FeedbackReason reason;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected MessageFeedback() {
    }

    public MessageFeedback(Message message, FeedbackRating rating, FeedbackReason reason) {
        this.message = message;
        this.qnaUuid = message.getQnaUuid();
        this.createdAt = now();
        update(rating, reason);
    }

    /**
     * 평가를 고쳐 쓴다. 상세사유만 뒤늦게 고른 경우도 이 경로로 들어온다.
     *
     * <p>좋아요를 싫어요로 바꾸면 먼저 고른 사유는 버린다 — 그대로 두면
     * "싫어요인데 사유는 정확한 정보예요" 같은 행이 남는다.
     */
    public void update(FeedbackRating rating, FeedbackReason reason) {
        this.rating = rating;
        this.reason = reason != null && reason.getRating() == rating ? reason : null;
        this.updatedAt = now();
    }

    private static Instant now() {
        return Instant.now().truncatedTo(ChronoUnit.SECONDS);
    }

    public Long getId() {
        return id;
    }

    public Message getMessage() {
        return message;
    }

    public String getQnaUuid() {
        return qnaUuid;
    }

    public FeedbackRating getRating() {
        return rating;
    }

    public FeedbackReason getReason() {
        return reason;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
