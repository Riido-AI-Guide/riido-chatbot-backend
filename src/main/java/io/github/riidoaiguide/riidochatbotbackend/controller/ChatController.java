package io.github.riidoaiguide.riidochatbotbackend.controller;

import io.github.riidoaiguide.riidochatbotbackend.dto.ai.AskResponse;
import io.github.riidoaiguide.riidochatbotbackend.service.ChatService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public ChatResponse ask(@Valid @RequestBody ChatRequest request) {
        AskResponse ai = chatService.ask(request.query());

        List<Source> sources = ai.documents().stream()
                .map(d -> new Source(d.docId(), d.title(), d.section()))
                .toList();

        return new ChatResponse(ai.answer(), sources);
    }

    public record ChatRequest(
            @NotBlank(message = "질문을 입력해주세요")
            String query
    ) {}

    public record ChatResponse(String answer, List<Source> sources) {}

    public record Source(String docId, String title, String section) {}
}