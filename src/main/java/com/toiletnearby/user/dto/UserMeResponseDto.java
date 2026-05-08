package com.toiletnearby.user.dto;

import java.util.List;

// 현재 인정된 사용자 정보를 응답할 Dto
public record UserMeResponseDto(
        String username,
        List<String> authorities,
        String message
) {
    // Controller에서 꺼낸 인증 정보를 응답 형태로 변환
    public static UserMeResponseDto of(String username, List<String> authorities) {
        return new UserMeResponseDto(
                username,
                authorities,
                "인증된 사용자입니다."
        );
    }
}