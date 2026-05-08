package com.toiletnearby.user.dto;

// Controller에서 HTTP 요청 body를 받을 DTO다.
public record UserRegisterRequestDto(
        String username,
        String password
) {

    // Controller 요청 DTO를 Service 입력 DTO로 변환한다.
    public UserRegisterDto toServiceDto() {
        return new UserRegisterDto(username, password);
    }
}
