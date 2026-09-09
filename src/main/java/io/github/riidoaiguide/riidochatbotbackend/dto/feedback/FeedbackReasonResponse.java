package io.github.riidoaiguide.riidochatbotbackend.dto.feedback;

import io.github.riidoaiguide.riidochatbotbackend.domain.FeedbackRating;
import io.github.riidoaiguide.riidochatbotbackend.domain.FeedbackReason;

/** 상세사유 선택지 한 개. 프론트가 한글 문구를 따로 들고 있지 않아도 되게 목록으로 내려준다. */
public record FeedbackReasonResponse(
        FeedbackReason code,
        String label,
        FeedbackRating rating
) {
    public static FeedbackReasonResponse from(FeedbackReason reason) {
        return new FeedbackReasonResponse(reason, reason.getLabel(), reason.getRating());
    }
}
