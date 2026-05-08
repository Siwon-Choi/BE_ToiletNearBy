package com.toiletnearby.global.exception;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

// API 에러 응답을 일정한 형태로 내려주기 위한 DTO다.
public record ErrorResponseDto(
        int status,
        String error,
        String message,
        String path,
        LocalDateTime timestamp
) {
    // HttpStatus와 메시지, 요청 path를 받아 에러 응답 DTO를 만든다.
    public static ErrorResponseDto of(HttpStatus status, String message, String path) {
        return new ErrorResponseDto(
                status.value(),
                status.getReasonPhrase(),
                message,
                path,
                LocalDateTime.now()
        );
    }
}
