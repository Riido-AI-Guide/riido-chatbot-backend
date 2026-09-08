package io.github.riidoaiguide.riidochatbotbackend.repository;

import io.github.riidoaiguide.riidochatbotbackend.domain.MessageBookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MessageBookmarkRepository extends JpaRepository<MessageBookmark, Long> {

    Optional<MessageBookmark> findByMessage_Id(Long messageId);

    // 대화 상세에서 메시지마다 별 표시를 그린다. 메시지마다 따로 묻지 않도록 한 번에 읽는다
    List<MessageBookmark> findByMessage_IdIn(Collection<Long> messageIds);

    void deleteByMessage_Id(Long messageId);

    /**
     * 한 사용자가 담아 둔 메시지 전부, 최근에 담은 순.
     * 목록에서 메시지와 대화를 함께 그리므로 둘 다 미리 붙여 온다.
     */
    @Query("""
            select bookmark from MessageBookmark bookmark
            join fetch bookmark.message message
            join fetch message.conversation conversation
            where conversation.user.id = :userId
            order by bookmark.id desc
            """)
    List<MessageBookmark> findAllByOwner(@Param("userId") Long userId);
}
