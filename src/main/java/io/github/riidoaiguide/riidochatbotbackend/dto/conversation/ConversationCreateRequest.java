package io.github.riidoaiguide.riidochatbotbackend.dto.conversation;

import jakarta.validation.constraints.NotBlank;

public record ConversationCreateRequest(
        @NotBlank(message = "질문을 입력해주세요")
        String query
) {}
