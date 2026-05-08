package com.toiletnearby.user.dto;

// Service로 전달할 회원가입 DTO다.
public record UserRegisterDto(String username, String password) {
}