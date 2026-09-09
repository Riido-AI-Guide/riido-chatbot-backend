package io.github.riidoaiguide.riidochatbotbackend.dto.feedback;

import io.github.riidoaiguide.riidochatbotbackend.domain.FeedbackRating;
import io.github.riidoaiguide.riidochatbotbackend.domain.FeedbackReason;
import jakarta.validation.constraints.NotNull;

/**
 * 답변 평가 요청. 좋아요/싫어요를 누른 직후와 상세사유를 고른 뒤 모두 이 형태로 들어온다.
 *
 * <p>rating은 두 번째 요청에서도 그대로 다시 보낸다 — 요청 하나가 곧 "이 메시지의 평가는 지금 이것"이라
 * 서버가 요청만 보고 상태를 확정할 수 있다.
 */
public record MessageFeedbackRequest(

        @NotNull(message = "좋아요/싫어요를 선택해주세요")
        FeedbackRating rating,

        // 상세사유. 고르지 않고 창을 닫았으면 null — 그래도 rating은 저장된다.
        FeedbackReason reason
) {}
