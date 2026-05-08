package com.toiletnearby.user.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


class UserLoginDtoTest {

    @Test
    @DisplayName("서비스 로그인 DTO를 생성한다")
    void createUserLoginDto() {
        UserLoginDto dto = new UserLoginDto("tester", "password123");

        assertThat(dto.username()).isEqualTo("tester");
        assertThat(dto.password()).isEqualTo("password123");
    }
}