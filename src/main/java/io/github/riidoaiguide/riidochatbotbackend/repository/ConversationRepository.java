package io.github.riidoaiguide.riidochatbotbackend.repository;

import io.github.riidoaiguide.riidochatbotbackend.domain.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {
}
