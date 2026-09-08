package io.github.riidoaiguide.riidochatbotbackend.controller;

import io.github.riidoaiguide.riidochatbotbackend.domain.FeedbackRating;
import io.github.riidoaiguide.riidochatbotbackend.dto.feedback.FeedbackReasonResponse;
import io.github.riidoaiguide.riidochatbotbackend.dto.feedback.MessageFeedbackRequest;
import io.github.riidoaiguide.riidochatbotbackend.dto.feedback.MessageFeedbackResponse;
import io.github.riidoaiguide.riidochatbotbackend.service.MessageFeedbackService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MessageFeedbackController {

    private final MessageFeedbackService feedbackService;

    public MessageFeedbackController(MessageFeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    /**
     * 답변 평가 남기기. 좋아요/싫어요만 누른 뒤에도, 상세사유를 고른 뒤에도 같은 요청을 보내면 된다
     * (두 번째 요청은 첫 요청이 만든 평가를 고쳐 쓴다).
     */
    @PutMapping("/messages/{messageId}/feedback")
    public MessageFeedbackResponse save(
            @PathVariable Long messageId,
            @Valid @RequestBody MessageFeedbackRequest request
    ) {
        return feedbackService.save(messageId, request.rating(), request.reason());
    }

    /** 평가 취소 */
    @DeleteMapping("/messages/{messageId}/feedback")
    public ResponseEntity<Void> delete(@PathVariable Long messageId) {
        feedbackService.delete(messageId);
        return ResponseEntity.noContent().build();
    }

    /** 상세사유 선택지. rating=GOOD/BAD로 좁힐 수 있고, 없으면 12개 전부 준다. */
    @GetMapping("/feedback-reasons")
    public List<FeedbackReasonResponse> reasons(@RequestParam(required = false) FeedbackRating rating) {
        return feedbackService.reasons(rating);
    }
}
