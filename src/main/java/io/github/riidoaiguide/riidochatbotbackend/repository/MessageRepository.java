package io.github.riidoaiguide.riidochatbotbackend.repository;

import io.github.riidoaiguide.riidochatbotbackend.domain.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
}
