package io.github.riidoaiguide.riidochatbotbackend.service;

import io.github.riidoaiguide.riidochatbotbackend.domain.FeedbackRating;
import io.github.riidoaiguide.riidochatbotbackend.domain.FeedbackReason;
import io.github.riidoaiguide.riidochatbotbackend.domain.Message;
import io.github.riidoaiguide.riidochatbotbackend.domain.MessageFeedback;
import io.github.riidoaiguide.riidochatbotbackend.domain.Role;
import io.github.riidoaiguide.riidochatbotbackend.dto.feedback.FeedbackReasonResponse;
import io.github.riidoaiguide.riidochatbotbackend.dto.feedback.MessageFeedbackResponse;
import io.github.riidoaiguide.riidochatbotbackend.repository.MessageFeedbackRepository;
import io.github.riidoaiguide.riidochatbotbackend.repository.MessageRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;

@Service
public class MessageFeedbackService {

    private final MessageRepository messageRepository;
    private final MessageFeedbackRepository feedbackRepository;

    public MessageFeedbackService(
            MessageRepository messageRepository,
            MessageFeedbackRepository feedbackRepository
    ) {
        this.messageRepository = messageRepository;
        this.feedbackRepository = feedbackRepository;
    }

    /**
     * 답변에 평가를 남긴다. 좋아요/싫어요만 누른 1차 요청과 상세사유까지 고른 2차 요청이 모두 여기로 온다.
     * 이미 남긴 평가가 있으면 새로 만들지 않고 그 행을 고쳐 쓴다.
     */
    @Transactional
    public MessageFeedbackResponse save(Long messageId, FeedbackRating rating, FeedbackReason reason) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "존재하지 않는 메시지입니다: " + messageId));

        if (message.getRole() != Role.ASSISTANT) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "답변에만 평가를 남길 수 있습니다: " + messageId);
        }

        // 좋아요에 싫어요 사유를 붙이는 요청은 프론트의 실수다. 조용히 버리지 않고 알려준다.
        if (reason != null && reason.getRating() != rating) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "%s에 선택할 수 없는 사유입니다: %s".formatted(rating, reason));
        }

        MessageFeedback feedback = feedbackRepository.findByMessage_Id(messageId).orElse(null);
        if (feedback == null) {
            feedback = feedbackRepository.save(new MessageFeedback(message, rating, reason));
        } else {
            feedback.update(rating, reason);
        }

        return MessageFeedbackResponse.from(feedback);
    }

    /** 평가 취소(따봉 해제). 남긴 적 없는 메시지여도 조용히 넘어간다 — 결과가 같기 때문이다. */
    @Transactional
    public void delete(Long messageId) {
        feedbackRepository.deleteByMessage_Id(messageId);
    }

    /** 상세사유 선택지 전체. rating으로 좁혀 볼 수 있다. */
    @Transactional(readOnly = true)
    public List<FeedbackReasonResponse> reasons(FeedbackRating rating) {
        List<FeedbackReason> reasons = rating == null
                ? Arrays.asList(FeedbackReason.values())
                : FeedbackReason.of(rating);
        return reasons.stream().map(FeedbackReasonResponse::from).toList();
    }
}
