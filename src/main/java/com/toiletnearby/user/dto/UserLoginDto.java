package com.toiletnearby.user.dto;

// Service로 전달할 로그인 DTO이다.
public record UserLoginDto (
        String username,
        String password
){
}