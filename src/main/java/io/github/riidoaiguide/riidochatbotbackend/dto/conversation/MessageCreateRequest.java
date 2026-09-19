package io.github.riidoaiguide.riidochatbotbackend.dto.conversation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 기존 대화에 이어서 묻는 후속 질문.
 *
 * <p>대화를 만들 때(ConversationCreateRequest)와 달리 userId를 받지 않는다 —
 * 주인은 대화에 이미 정해져 있다.
 */
public record MessageCreateRequest(
        @NotBlank(message = "질문을 입력해주세요")
        @Size(max = 1000, message = "질문은 1000자까지 입력할 수 있습니다")
        String query
) {}
