package com.toiletnearby.global.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

class ErrorResponseDtoTest {

    @Test
    @DisplayName("공통 에러 응답 DTO를 생성한다")
    void createErrorResponseDto() {
        ErrorResponseDto responseDto = ErrorResponseDto.of(
                HttpStatus.UNAUTHORIZED,
                "인증이 필요합니다.",
                "/api/users/me"
        );

        assertThat(responseDto.status()).isEqualTo(401);
        assertThat(responseDto.error()).isEqualTo("Unauthorized");
        assertThat(responseDto.message()).isEqualTo("인증이 필요합니다.");
        assertThat(responseDto.path()).isEqualTo("/api/users/me");
        assertThat(responseDto.timestamp()).isNotNull();
    }
}
