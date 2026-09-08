package io.github.riidoaiguide.riidochatbotbackend.controller;

import io.github.riidoaiguide.riidochatbotbackend.dto.bookmark.BookmarkResponse;
import io.github.riidoaiguide.riidochatbotbackend.dto.bookmark.MessageBookmarkResponse;
import io.github.riidoaiguide.riidochatbotbackend.service.MessageBookmarkService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MessageBookmarkController {

    private final MessageBookmarkService bookmarkService;

    public MessageBookmarkController(MessageBookmarkService bookmarkService) {
        this.bookmarkService = bookmarkService;
    }

    /** 메시지 담기. 이미 담아 둔 메시지에 다시 보내도 같은 결과다 */
    @PutMapping("/messages/{messageId}/bookmark")
    public MessageBookmarkResponse add(@PathVariable Long messageId) {
        return bookmarkService.add(messageId);
    }

    /** 담기 취소 */
    @DeleteMapping("/messages/{messageId}/bookmark")
    public ResponseEntity<Void> remove(@PathVariable Long messageId) {
        bookmarkService.remove(messageId);
        return ResponseEntity.noContent().build();
    }

    /** 내가 담아 둔 메시지 목록 (최근에 담은 순) */
    @GetMapping("/bookmarks")
    public List<BookmarkResponse> list(@RequestParam Long userId) {
        return bookmarkService.list(userId);
    }
}
