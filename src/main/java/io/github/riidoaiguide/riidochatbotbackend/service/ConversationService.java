package io.github.riidoaiguide.riidochatbotbackend.service;

import io.github.riidoaiguide.riidochatbotbackend.domain.Conversation;
import io.github.riidoaiguide.riidochatbotbackend.domain.Message;
import io.github.riidoaiguide.riidochatbotbackend.domain.Role;
import io.github.riidoaiguide.riidochatbotbackend.dto.ai.AskResponse;
import io.github.riidoaiguide.riidochatbotbackend.dto.ai.ConversationTurnDto;
import io.github.riidoaiguide.riidochatbotbackend.dto.conversation.ConversationResponse;
import io.github.riidoaiguide.riidochatbotbackend.dto.conversation.ConversationSummaryResponse;
import io.github.riidoaiguide.riidochatbotbackend.repository.ConversationRepository;
import io.github.riidoaiguide.riidochatbotbackend.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.server.ResponseStatusException;

@Service
public class ConversationService {

    private static final int MAX_TITLE_LENGTH = 200;

    // AI에 보낼 이전 대화 상한. AI 쪽 사용 상한(history_turns=5)과 맞춤
    private static final int MAX_HISTORY_PAIRS = 5;

    private final ChatService chatService;
    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;

    public ConversationService(
            ChatService chatService,
            ConversationRepository conversationRepository,
            UserRepository userRepository
    ) {
        this.chatService = chatService;
        this.conversationRepository = conversationRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ConversationResponse create(String query, Long userId) {
        AskResponse ai = chatService.ask(query);

        Conversation conversation = new Conversation(toTitle(query));

        // 로그인한 사용자가 보낸 요청이면 대화에 작성자를 연결한다.
        // 모르는 userId면 연결 없이 저장한다 (대화 자체를 잃는 것보다 낫다).
        if (userId != null) {
            userRepository.findById(userId).ifPresent(conversation::assignUser);
        }

        conversation.addMessage(Role.USER, query);
        conversation.addMessage(Role.ASSISTANT, ai.answer());

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

        conversation.addMessage(Role.USER, query);
        conversation.addMessage(Role.ASSISTANT, ai.answer());
        conversationRepository.flush();

        return ConversationResponse.from(conversation);
    }

    /** 기존 메시지들을 질문-답변 쌍으로 묶어 최근 MAX_HISTORY_PAIRS쌍만 남긴다. */
    private List<ConversationTurnDto> toHistory(Conversation conversation) {
        List<ConversationTurnDto> pairs = new ArrayList<>();
        String pendingQuestion = null;

        for (Message message : conversation.getMessages()) {
            if (message.getRole() == Role.USER) {
                pendingQuestion = message.getContent();
            } else if (pendingQuestion != null) {
                pairs.add(new ConversationTurnDto(pendingQuestion, message.getContent()));
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
                .map(ConversationResponse::from)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "존재하지 않는 대화입니다: " + conversationId));
    }

    private String toTitle(String query) {
        String trimmed = query.strip();
        return trimmed.length() <= MAX_TITLE_LENGTH ? trimmed : trimmed.substring(0, MAX_TITLE_LENGTH);
    }
}
