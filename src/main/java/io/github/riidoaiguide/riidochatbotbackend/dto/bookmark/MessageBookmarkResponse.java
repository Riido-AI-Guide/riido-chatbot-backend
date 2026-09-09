package io.github.riidoaiguide.riidochatbotbackend.dto.bookmark;

import io.github.riidoaiguide.riidochatbotbackend.domain.MessageBookmark;

import java.time.Instant;

/** 방금 담은 메시지. 등록 요청의 응답이다. */
public record MessageBookmarkResponse(
        Long messageId,
        Instant bookmarkedAt
) {
    public static MessageBookmarkResponse from(MessageBookmark bookmark) {
        return new MessageBookmarkResponse(bookmark.getMessage().getId(), bookmark.getCreatedAt());
    }
}
