package io.github.riidoaiguide.riidochatbotbackend.dto.conversation;

import jakarta.validation.constraints.NotBlank;

public record ConversationCreateRequest(
        @NotBlank(message = "질문을 입력해주세요")
        String query,

        // 로그인한 사용자 id. 없으면(null) 주인 없는 대화로 저장된다.
        Long userId
) {}
