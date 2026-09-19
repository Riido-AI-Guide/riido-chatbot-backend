package io.github.riidoaiguide.riidochatbotbackend.controller;

import io.github.riidoaiguide.riidochatbotbackend.dto.conversation.ConversationCreateRequest;
import io.github.riidoaiguide.riidochatbotbackend.dto.conversation.ConversationResponse;
import io.github.riidoaiguide.riidochatbotbackend.dto.conversation.ConversationSummaryResponse;
import io.github.riidoaiguide.riidochatbotbackend.dto.conversation.MessageCreateRequest;
import io.github.riidoaiguide.riidochatbotbackend.service.ConversationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/conversations")
public class ConversationController {

    private final ConversationService conversationService;

    public ConversationController(ConversationService conversationService) {
        this.conversationService = conversationService;
    }

    /** 내 대화 목록 (최신순) */
    @GetMapping
    public List<ConversationSummaryResponse> list(@RequestParam Long userId) {
        return conversationService.list(userId);
    }

    /** 대화 상세 (메시지 포함) */
    @GetMapping("/{conversationId}")
    public ConversationResponse detail(@PathVariable Long conversationId) {
        return conversationService.detail(conversationId);
    }

    /** 기존 대화에 이어서 질문 (멀티턴) */
    @PostMapping("/{conversationId}/messages")
    public ConversationResponse append(
            @PathVariable Long conversationId,
            @Valid @RequestBody MessageCreateRequest request
    ) {
        return conversationService.append(conversationId, request.query());
    }

    @PostMapping
    public ResponseEntity<ConversationResponse> create(@Valid @RequestBody ConversationCreateRequest request) {
        ConversationResponse response = conversationService.create(request.query(), request.userId());
        return ResponseEntity
                .created(URI.create("/conversations/" + response.conversationId()))
                .body(response);
    }
}
