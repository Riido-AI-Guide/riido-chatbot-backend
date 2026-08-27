package io.github.riidoaiguide.riidochatbotbackend.service;

import io.github.riidoaiguide.riidochatbotbackend.domain.Conversation;
import io.github.riidoaiguide.riidochatbotbackend.domain.Role;
import io.github.riidoaiguide.riidochatbotbackend.dto.ai.AskResponse;
import io.github.riidoaiguide.riidochatbotbackend.dto.conversation.ConversationResponse;
import io.github.riidoaiguide.riidochatbotbackend.repository.ConversationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConversationService {

    private static final int MAX_TITLE_LENGTH = 200;

    private final ChatService chatService;
    private final ConversationRepository conversationRepository;

    public ConversationService(ChatService chatService, ConversationRepository conversationRepository) {
        this.chatService = chatService;
        this.conversationRepository = conversationRepository;
    }

    @Transactional
    public ConversationResponse create(String query) {
        AskResponse ai = chatService.ask(query);

        Conversation conversation = new Conversation(toTitle(query));
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
