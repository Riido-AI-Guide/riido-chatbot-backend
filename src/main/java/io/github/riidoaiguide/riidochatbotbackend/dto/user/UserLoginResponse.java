package io.github.riidoaiguide.riidochatbotbackend.dto.user;

import io.github.riidoaiguide.riidochatbotbackend.domain.User;

public record UserLoginResponse(Long id, String name, boolean isNew) {

    public static UserLoginResponse from(User user, boolean isNew) {
        return new UserLoginResponse(user.getId(), user.getName(), isNew);
    }
}
