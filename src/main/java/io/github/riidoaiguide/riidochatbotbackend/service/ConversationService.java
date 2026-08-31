package io.github.riidoaiguide.riidochatbotbackend.service;

import io.github.riidoaiguide.riidochatbotbackend.domain.Conversation;
import io.github.riidoaiguide.riidochatbotbackend.domain.Role;
import io.github.riidoaiguide.riidochatbotbackend.dto.ai.AskResponse;
import io.github.riidoaiguide.riidochatbotbackend.dto.conversation.ConversationResponse;
import io.github.riidoaiguide.riidochatbotbackend.repository.ConversationRepository;
import io.github.riidoaiguide.riidochatbotbackend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    private String toTitle(String query) {
        String trimmed = query.strip();
        return trimmed.length() <= MAX_TITLE_LENGTH ? trimmed : trimmed.substring(0, MAX_TITLE_LENGTH);
    }
}
