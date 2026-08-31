package io.github.riidoaiguide.riidochatbotbackend.controller;

import io.github.riidoaiguide.riidochatbotbackend.dto.user.UserLoginRequest;
import io.github.riidoaiguide.riidochatbotbackend.dto.user.UserLoginResponse;
import io.github.riidoaiguide.riidochatbotbackend.dto.user.UserResponse;
import io.github.riidoaiguide.riidochatbotbackend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /** 이름으로 로그인. 없는 이름이면 가입 후 로그인 (isNew=true) */
    @PostMapping("/login")
    public UserLoginResponse login(@Valid @RequestBody UserLoginRequest request) {
        return userService.loginOrSignup(request.name());
    }

    /** 멤버 목록 */
    @GetMapping
    public List<UserResponse> members() {
        return userService.findAll();
    }
}
