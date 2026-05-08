package com.toiletnearby.user.controller;

import com.toiletnearby.user.domain.User;
import com.toiletnearby.user.service.UserService;
import com.toiletnearby.user.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserRegisterResponseDto> register(@RequestBody UserRegisterRequestDto requestDto){
        User savedUser = userService.register(requestDto.toServiceDto());

        UserRegisterResponseDto responseDto = UserRegisterResponseDto.from(savedUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @PostMapping("/login")
    public ResponseEntity<UserLoginResponseDto> login(@RequestBody UserLoginRequestDto requestDto) {
        UserLoginResultDto loginResult = userService.login(requestDto.toServiceDto());

        return ResponseEntity.ok(UserLoginResponseDto.from(loginResult));
    }

    // jwt 인증 확인 api
    // 토큰이 없으면 Spring Security가 막음
    // 토큰이 있으면 username과 권한이 들어온다.
    @GetMapping("/me")
    public ResponseEntity<UserMeResponseDto> me(Authentication authentication){

        String username = authentication.getName();

        List<String> authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return ResponseEntity.ok(UserMeResponseDto.of(username, authorities));
    }

    @GetMapping("/admin-test")
    public ResponseEntity<UserAdminResponseDto> adminTest() {
        return ResponseEntity.ok(UserAdminResponseDto.success());
    }

}