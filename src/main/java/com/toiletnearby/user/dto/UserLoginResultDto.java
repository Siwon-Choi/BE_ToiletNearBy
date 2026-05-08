package com.toiletnearby.user.dto;

// Service가 로그인 성공 결과로 Controller에 전달할 DTO다.
public record UserLoginResultDto(
        String username,
        String accessToken
) {
}
