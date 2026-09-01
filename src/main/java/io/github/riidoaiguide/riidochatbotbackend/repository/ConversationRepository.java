package io.github.riidoaiguide.riidochatbotbackend.repository;

import io.github.riidoaiguide.riidochatbotbackend.domain.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    // "user 연관의 id가 일치하는 대화를 id 내림차순(최신순)으로" — 이름이 곧 SQL
    List<Conversation> findByUser_IdOrderByIdDesc(Long userId);
}
