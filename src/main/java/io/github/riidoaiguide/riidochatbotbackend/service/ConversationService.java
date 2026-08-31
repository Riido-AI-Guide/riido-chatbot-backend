package io.github.riidoaiguide.riidochatbotbackend.service;

import io.github.riidoaiguide.riidochatbotbackend.domain.Conversation;
import io.github.riidoaiguide.riidochatbotbackend.domain.Role;
import io.github.riidoaiguide.riidochatbotbackend.dto.ai.AskResponse;
import io.github.riidoaiguide.riidochatbotbackend.dto.conversation.ConversationResponse;
import io.github.riidoaiguide.riidochatbotbackend.dto.conversation.ConversationSummaryResponse;
import io.github.riidoaiguide.riidochatbotbackend.repository.ConversationRepository;
import io.github.riidoaiguide.riidochatbotbackend.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import org.springframework.web.server.ResponseStatusException;

@Service
public class ConversationService {

    private static final int MAX_TITLE_LENGTH = 200;

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
