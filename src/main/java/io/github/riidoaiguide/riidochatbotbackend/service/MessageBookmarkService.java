package io.github.riidoaiguide.riidochatbotbackend.service;

import io.github.riidoaiguide.riidochatbotbackend.domain.Message;
import io.github.riidoaiguide.riidochatbotbackend.domain.MessageBookmark;
import io.github.riidoaiguide.riidochatbotbackend.domain.Role;
import io.github.riidoaiguide.riidochatbotbackend.dto.bookmark.BookmarkResponse;
import io.github.riidoaiguide.riidochatbotbackend.dto.bookmark.MessageBookmarkResponse;
import io.github.riidoaiguide.riidochatbotbackend.dto.feedback.MessageFeedbackResponse;
import io.github.riidoaiguide.riidochatbotbackend.repository.MessageBookmarkRepository;
import io.github.riidoaiguide.riidochatbotbackend.repository.MessageFeedbackRepository;
import io.github.riidoaiguide.riidochatbotbackend.repository.MessageRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class MessageBookmarkService {

    private final MessageRepository messageRepository;
    private final MessageBookmarkRepository bookmarkRepository;
    private final MessageFeedbackRepository feedbackRepository;

    public MessageBookmarkService(
            MessageRepository messageRepository,
            MessageBookmarkRepository bookmarkRepository,
            MessageFeedbackRepository feedbackRepository
    ) {
        this.messageRepository = messageRepository;
        this.bookmarkRepository = bookmarkRepository;
        this.feedbackRepository = feedbackRepository;
    }

    /** 메시지를 담는다. 이미 담아 둔 메시지면 그대로 둔다 — 두 번 눌러도 처음 담은 시각이 남는다. */
    @Transactional
    public MessageBookmarkResponse add(Long messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "존재하지 않는 메시지입니다: " + messageId));

        if (message.getRole() != Role.ASSISTANT) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "답변만 담을 수 있습니다: " + messageId);
        }

        MessageBookmark bookmark = bookmarkRepository.findByMessage_Id(messageId)
                .orElseGet(() -> bookmarkRepository.save(new MessageBookmark(message)));

        return MessageBookmarkResponse.from(bookmark);
    }

    /** 담아 둔 메시지를 뺀다. 담은 적 없는 메시지여도 조용히 넘어간다 — 결과가 같기 때문이다. */
    @Transactional
    public void remove(Long messageId) {
        bookmarkRepository.deleteByMessage_Id(messageId);
    }

    /** 담아 둔 메시지 목록, 최근에 담은 순. 하나도 없으면 빈 목록. */
    @Transactional(readOnly = true)
    public List<BookmarkResponse> list(Long userId) {
        List<MessageBookmark> bookmarks = bookmarkRepository.findAllByOwner(userId);

        // 목록에도 답변 말풍선을 그대로 그리므로 남긴 평가가 함께 보여야 한다.
        // 메시지마다 묻지 않도록 한 번에 읽는다.
        List<Long> messageIds = bookmarks.stream()
                .map(bookmark -> bookmark.getMessage().getId())
                .toList();

        Map<Long, MessageFeedbackResponse> feedbacks = feedbackRepository.findByMessage_IdIn(messageIds).stream()
                .map(MessageFeedbackResponse::from)
                .collect(Collectors.toMap(MessageFeedbackResponse::messageId, Function.identity()));

        return bookmarks.stream()
                .map(bookmark -> BookmarkResponse.from(bookmark, feedbacks.get(bookmark.getMessage().getId())))
                .toList();
    }
}
