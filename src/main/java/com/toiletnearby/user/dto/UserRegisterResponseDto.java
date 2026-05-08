package com.toiletnearby.user.dto;

import com.toiletnearby.user.domain.User;

// 회원가입 후 성공 응답 DTO
public record UserRegisterResponseDto(
        String username,
        String message
) {

    // User 엔티티를 응답 DTO로 변환한다.
    // 비밀번호는 제외하고 보낸다.
    public static UserRegisterResponseDto from(User user) {
        return new UserRegisterResponseDto(
                user.getUsername(),
                "회원가입이 완료되었습니다."
        );
    }
}
