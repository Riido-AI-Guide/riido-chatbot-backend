package io.github.riidoaiguide.riidochatbotbackend.repository;

import io.github.riidoaiguide.riidochatbotbackend.domain.MessageFeedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MessageFeedbackRepository extends JpaRepository<MessageFeedback, Long> {

    Optional<MessageFeedback> findByMessage_Id(Long messageId);

    // 대화 상세는 메시지가 여러 개다. 메시지마다 따로 묻지 않도록 한 번에 읽는다
    List<MessageFeedback> findByMessage_IdIn(Collection<Long> messageIds);

    void deleteByMessage_Id(Long messageId);
}
