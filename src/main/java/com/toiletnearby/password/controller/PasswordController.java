package com.toiletnearby.password.controller;

import com.toiletnearby.password.domain.Password;
import com.toiletnearby.password.dto.PasswordCreateRequestDto;
import com.toiletnearby.password.dto.PasswordResponseDto;
import com.toiletnearby.password.dto.PasswordUpdateRequestDto;
import com.toiletnearby.password.service.PasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

// 화장실 비밀번호 API를 담당한다.
@RestController
@RequiredArgsConstructor
public class PasswordController {

    private final PasswordService passwordService;

    // 로그인한 사용자가 특정 화장실에 저장한 비밀번호를 조회한다.
    @GetMapping("/api/password/{tid}")
    public String getPassword(
            @PathVariable Long tid,
            Authentication authentication
    ) {
        return passwordService.getPassword(tid, getAuthenticatedUserId(authentication));
    }

    // 로그인한 사용자가 특정 화장실 비밀번호를 저장한다.
    @PostMapping("/api/password/make")
    public PasswordResponseDto makePassword(
            @RequestBody PasswordCreateRequestDto requestDto,
            Authentication authentication
    ) {
        Password password = passwordService.savePassword(
                requestDto.toSaveDto(getAuthenticatedUserId(authentication))
        );

        return PasswordResponseDto.from(password);
    }

    // 로그인한 사용자가 특정 화장실 비밀번호를 삭제한다.
    @DeleteMapping("/api/password/{tid}/delete")
    public String deletePassword(
            @PathVariable Long tid,
            Authentication authentication
    ) {
        return passwordService.deletePassword(tid, getAuthenticatedUserId(authentication));
    }

    // 로그인한 사용자가 특정 화장실 비밀번호를 수정한다.
    // 비밀번호가 URL 로그에 남지 않도록 새 비밀번호는 request body로 받는다.
    @PutMapping("/api/password/{TOILET_ID}/put")
    public PasswordResponseDto updatePassword(
            @PathVariable("TOILET_ID") Long toiletId,
            @RequestBody PasswordUpdateRequestDto requestDto,
            Authentication authentication
    ) {
        Password password = passwordService.updatePassword(
                requestDto.toSaveDto(toiletId, getAuthenticatedUserId(authentication))
        );

        return PasswordResponseDto.from(password);
    }

    // JWT 인증 객체에서 로그인 사용자 id를 꺼낸다.
    private String getAuthenticatedUserId(Authentication authentication) {
        if (authentication == null || authentication.getName() == null || authentication.getName().isBlank()) {
            throw new IllegalArgumentException("인증 정보가 없습니다.");
        }

        return authentication.getName();
    }
}
