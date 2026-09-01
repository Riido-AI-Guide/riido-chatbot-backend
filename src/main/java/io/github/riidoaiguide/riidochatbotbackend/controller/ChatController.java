package io.github.riidoaiguide.riidochatbotbackend.controller;

import io.github.riidoaiguide.riidochatbotbackend.dto.ai.AskResponse;
import io.github.riidoaiguide.riidochatbotbackend.dto.chat.AnswerResponse;
import io.github.riidoaiguide.riidochatbotbackend.service.ChatService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.web.bind.annotation.*;

/** 저장 없이 한 번 묻고 답만 받는 단일턴 엔드포인트. 대화로 남기려면 /conversations를 쓴다. */
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public AnswerResponse ask(@Valid @RequestBody ChatRequest request) {
        AskResponse ai = chatService.ask(request.query());
        return AnswerResponse.from(ai);
    }

    public record ChatRequest(
            @NotBlank(message = "질문을 입력해주세요")
            @Size(max = 1000, message = "질문은 1000자까지 입력할 수 있습니다")
            String query
    ) {}
}
