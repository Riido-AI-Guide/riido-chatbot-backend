package io.github.riidoaiguide.riidochatbotbackend.service;

import io.github.riidoaiguide.riidochatbotbackend.domain.User;
import io.github.riidoaiguide.riidochatbotbackend.dto.user.UserLoginResponse;
import io.github.riidoaiguide.riidochatbotbackend.dto.user.UserResponse;
import io.github.riidoaiguide.riidochatbotbackend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 이름으로 로그인한다. 없는 이름이면 새로 가입시키고 로그인한다.
     * 앞뒤 공백은 무시한다 ("윤서"와 " 윤서 "는 같은 사람).
     */
    @Transactional
    public UserLoginResponse loginOrSignup(String name) {
        String normalized = name.strip();

        return userRepository.findByName(normalized)
                .map(user -> UserLoginResponse.from(user, false))
                .orElseGet(() -> UserLoginResponse.from(
                        userRepository.save(new User(normalized)), true));
    }

    @Transactional(readOnly = true)
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(UserResponse::from)
                .toList();
    }
}
