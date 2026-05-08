package com.toiletnearby.user.dto;

// Controller에서 로그인 요청 body를 받는 DTO
public record UserLoginRequestDto(
        String username,
        String password
) {

    // 요청 DTO를 Service 입력 DTO로 변환한다.
    public UserLoginDto toServiceDto(){
        return new UserLoginDto(username, password);
    }
}