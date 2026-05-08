package com.toiletnearby.user.dto;

// 로그인 성공 응답 DTO
public record UserLoginResponseDto(
        String username,
        String tokenType,
        String accessToken,
        String message
) {
    // Service 결과 DTO를 HTTP 응답 DTO로 변환
    public static UserLoginResponseDto from(UserLoginResultDto resultDto) {
        return new UserLoginResponseDto(
                resultDto.username(),
                "Bearer",
                resultDto.accessToken(),
                "로그인이 완료되었습니다."
        );
    }
}
