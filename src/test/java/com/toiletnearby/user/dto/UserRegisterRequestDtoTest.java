package com.toiletnearby.user.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserRegisterRequestDtoTest {

    @Test
    @DisplayName("요청 DTO를 서비스 DTO로 변환한다")
    void convertToServiceDto() {
        UserRegisterRequestDto requestDto = new UserRegisterRequestDto("tester", "password123");

        UserRegisterDto serviceDto = requestDto.toServiceDto();

        assertThat(serviceDto.username()).isEqualTo("tester");
        assertThat(serviceDto.password()).isEqualTo("password123");
    }
}
