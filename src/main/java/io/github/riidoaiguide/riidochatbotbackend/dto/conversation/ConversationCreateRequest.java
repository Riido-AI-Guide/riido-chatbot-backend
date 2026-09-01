package io.github.riidoaiguide.riidochatbotbackend.dto.conversation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ConversationCreateRequest(
        @NotBlank(message = "질문을 입력해주세요")
        @Size(max = 1000, message = "질문은 1000자까지 입력할 수 있습니다")
        String query,

        // 로그인한 사용자 id. 없으면(null) 주인 없는 대화로 저장된다.
        Long userId
) {}
