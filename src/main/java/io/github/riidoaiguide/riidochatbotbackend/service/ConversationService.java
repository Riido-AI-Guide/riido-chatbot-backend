package io.github.riidoaiguide.riidochatbotbackend.service;

import io.github.riidoaiguide.riidochatbotbackend.domain.AnswerSection;
import io.github.riidoaiguide.riidochatbotbackend.domain.Conversation;
import io.github.riidoaiguide.riidochatbotbackend.domain.Message;
import io.github.riidoaiguide.riidochatbotbackend.domain.Role;
import io.github.riidoaiguide.riidochatbotbackend.dto.ai.AnswerSectionDto;
import io.github.riidoaiguide.riidochatbotbackend.dto.ai.AskResponse;
import io.github.riidoaiguide.riidochatbotbackend.dto.ai.ConversationTurnDto;
import io.github.riidoaiguide.riidochatbotbackend.dto.ai.SourceRefDto;
import io.github.riidoaiguide.riidochatbotbackend.dto.conversation.ConversationResponse;
import io.github.riidoaiguide.riidochatbotbackend.dto.conversation.ConversationSummaryResponse;
import io.github.riidoaiguide.riidochatbotbackend.dto.feedback.MessageFeedbackResponse;
import io.github.riidoaiguide.riidochatbotbackend.repository.ConversationRepository;
import io.github.riidoaiguide.riidochatbotbackend.repository.MessageBookmarkRepository;
import io.github.riidoaiguide.riidochatbotbackend.repository.MessageFeedbackRepository;
import io.github.riidoaiguide.riidochatbotbackend.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.web.server.ResponseStatusException;

@Service
public class ConversationService {

    private static final int MAX_TITLE_LENGTH = 200;

    // AI에 보낼 이전 대화 상한. AI 쪽 사용 상한(history_turns=5)과 맞춤
    private static final int MAX_HISTORY_PAIRS = 5;

    // AI 요청 스펙(AskRequest)의 길이 상한. 넘기면 422로 거절당한다
    private static final int MAX_HISTORY_QUESTION_LENGTH = 1000;
    private static final int MAX_HISTORY_ANSWER_LENGTH = 4000;

    private final ChatService chatService;
    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;
    private final MessageFeedbackRepository feedbackRepository;
    private final MessageBookmarkRepository bookmarkRepository;

    public ConversationService(
            ChatService chatService,
            ConversationRepository conversationRepository,
            UserRepository userRepository,
            MessageFeedbackRepository feedbackRepository,
            MessageBookmarkRepository bookmarkRepository
    ) {
        this.chatService = chatService;
        this.conversationRepository = conversationRepository;
        this.userRepository = userRepository;
        this.feedbackRepository = feedbackRepository;
        this.bookmarkRepository = bookmarkRepository;
    }

    @Transactional
    public ConversationResponse create(String query, Long userId) {
        AskResponse ai = chatService.ask(query);

        // 대화 제목은 AI가 이번 턴에 붙인 title을 쓴다. 첫 턴이면 비어 오지 않지만,
        // 그래도 비면 질문 원문으로 대신한다 (제목 없는 대화를 만들지 않기 위해).
        Conversation conversation = new Conversation(toTitle(ai.title(), query));

        // 로그인한 사용자가 보낸 요청이면 대화에 작성자를 연결한다.
        // 모르는 userId면 연결 없이 저장한다 (대화 자체를 잃는 것보다 낫다).
        if (userId != null) {
            userRepository.findById(userId).ifPresent(conversation::assignUser);
        }

        addAnswer(conversation, conversation.addQuestion(query), ai);

        conversationRepository.save(conversation);
        conversationRepository.flush();

        return ConversationResponse.from(conversation);
    }

    /** 기존 대화에 이어서 질문한다. 이전 대화를 AI에 함께 보내 대명사·생략을 풀게 한다. */
    @Transactional
    public ConversationResponse append(Long conversationId, String query) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "존재하지 않는 대화입니다: " + conversationId));

        List<ConversationTurnDto> history = toHistory(conversation);
        AskResponse ai = chatService.ask(query, history, String.valueOf(conversationId));

        // 대화 제목은 그대로 둔다. 후속 턴의 title은 이번 답변에 붙는 말풍선 제목일 뿐이라
        // 이것으로 덮어쓰면 대화 제목이 매 턴 바뀐다.
        addAnswer(conversation, conversation.addQuestion(query), ai);
        conversationRepository.flush();

        return toResponse(conversation);
    }

    /** AI 답변을 제목·섹션·근거까지 그대로 대화에 남긴다. 답한 질문(question)을 함께 걸어 둔다. */
    private void addAnswer(Conversation conversation, Message question, AskResponse ai) {
        Message answer = conversation.addAnswer(
                question,
                ai.answerText(),
                blankToNull(cut(ai.title(), MAX_TITLE_LENGTH)),
                ai.answerType(),
                blankToNull(ai.qnaUuid())
        );

        for (AnswerSectionDto section : ai.answers()) {
            AnswerSection saved = answer.addSection(section.label(), section.text());
            for (SourceRefDto source : section.sources()) {
                saved.addSource(source.docId(), source.section(), blankToNull(source.url()));
            }
        }
    }

    /** 기존 메시지들을 질문-답변 쌍으로 묶어 최근 MAX_HISTORY_PAIRS쌍만 남긴다. */
    private List<ConversationTurnDto> toHistory(Conversation conversation) {
        List<ConversationTurnDto> pairs = new ArrayList<>();
        String pendingQuestion = null;

        for (Message message : conversation.getMessages()) {
            if (message.getRole() == Role.USER) {
                pendingQuestion = message.getContent();
            } else if (pendingQuestion != null) {
                pairs.add(new ConversationTurnDto(
                        cut(pendingQuestion, MAX_HISTORY_QUESTION_LENGTH),
                        cut(message.getContent(), MAX_HISTORY_ANSWER_LENGTH)));
                pendingQuestion = null;
            }
        }

        if (pairs.size() > MAX_HISTORY_PAIRS) {
            return new ArrayList<>(pairs.subList(pairs.size() - MAX_HISTORY_PAIRS, pairs.size()));
        }
        return pairs;
    }

    /** 해당 사용자의 대화 목록, 최신순. 대화가 없으면 빈 목록. */
    @Transactional(readOnly = true)
    public List<ConversationSummaryResponse> list(Long userId) {
        return conversationRepository.findByUser_IdOrderByIdDesc(userId).stream()
                .map(ConversationSummaryResponse::from)
                .toList();
    }

    /** 대화 하나를 메시지까지 포함해 조회. 없는 id면 404. */
    @Transactional(readOnly = true)
    public ConversationResponse detail(Long conversationId) {
        return conversationRepository.findById(conversationId)
                .map(this::toResponse)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "존재하지 않는 대화입니다: " + conversationId));
    }

    /** 이미 남긴 평가와 북마크까지 실어 대화를 내보낸다. 메시지마다 묻지 않도록 둘 다 한 번에 읽는다. */
    private ConversationResponse toResponse(Conversation conversation) {
        List<Long> messageIds = conversation.getMessages().stream()
                .map(Message::getId)
                .toList();

        Map<Long, MessageFeedbackResponse> feedbacks = feedbackRepository.findByMessage_IdIn(messageIds).stream()
                .map(MessageFeedbackResponse::from)
                .collect(Collectors.toMap(MessageFeedbackResponse::messageId, Function.identity()));

        Set<Long> bookmarked = bookmarkRepository.findByMessage_IdIn(messageIds).stream()
                .map(bookmark -> bookmark.getMessage().getId())
                .collect(Collectors.toSet());

        return ConversationResponse.from(conversation, feedbacks, bookmarked);
    }

    private String toTitle(String aiTitle, String query) {
        String title = blankToNull(aiTitle) == null ? query : aiTitle;
        return cut(title.strip(), MAX_TITLE_LENGTH);
    }

    /** 후속 턴의 인사에는 제목이 빈 문자열로 온다. 제목 없는 답변임을 null로 분명히 해 둔다. */
    private static String blankToNull(String text) {
        return text == null || text.isBlank() ? null : text;
    }

    private static String cut(String text, int maxLength) {
        if (text == null) {
            return "";
        }
        return text.length() <= maxLength ? text : text.substring(0, maxLength);
    }
}
