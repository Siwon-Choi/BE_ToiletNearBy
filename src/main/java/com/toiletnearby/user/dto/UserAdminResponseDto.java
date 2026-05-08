package com.toiletnearby.user.dto;

// ADMIN 권한이 필요한 API의 응답 DTO다.
public record UserAdminResponseDto(
        String message
) {
    // 관리자 API 접근 성공 응답을 만든다.
    public static UserAdminResponseDto success() {
        return new UserAdminResponseDto("관리자 권한 접근에 성공했습니다.");
    }
}
