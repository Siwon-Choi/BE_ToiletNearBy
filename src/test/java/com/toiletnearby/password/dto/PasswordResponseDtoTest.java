package com.toiletnearby.password.dto;

import com.toiletnearby.password.domain.Password;
import com.toiletnearby.password.domain.vo.PasswordValueVo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PasswordResponseDtoTest {

    @Test
    @DisplayName("Password 엔티티를 응답 DTO로 변환한다")
    void fromPassword() {
        Password password = Password.create(
                1L,
                "tester",
                PasswordValueVo.from("1234")
        );

        PasswordResponseDto responseDto = PasswordResponseDto.from(password);

        assertThat(responseDto.toiletId()).isEqualTo(1L);
        assertThat(responseDto.userId()).isEqualTo("tester");
        assertThat(responseDto.password()).isEqualTo("1234");
    }
}
