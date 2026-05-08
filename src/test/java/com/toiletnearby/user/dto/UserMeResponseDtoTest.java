package com.toiletnearby.user.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class UserMeResponseDtoTest {

    @Test
    @DisplayName("인증된 사용자 응답 DTO를 생성한다")
    void createUserMeResponseDto() {
        UserMeResponseDto responseDto = UserMeResponseDto.of(
                "tester",
                List.of("USER")
        );

        assertThat(responseDto.username()).isEqualTo("tester");
        assertThat(responseDto.authorities()).containsExactly("USER");
        assertThat(responseDto.message()).isEqualTo("인증된 사용자입니다.");
    }
}
