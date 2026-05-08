package com.toiletnearby.password.domain;

import com.toiletnearby.password.domain.vo.PasswordValueVo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class PasswordTest {

    @Test
    @DisplayName("화장실 비밀번호를 생성한다")
    void createPassword() {
        Password password = Password.create(
                1L,
                "tester",
                PasswordValueVo.from("1234")
        );

        assertThat(password.getToiletId()).isEqualTo(1L);
        assertThat(password.getUserId()).isEqualTo("tester");
        assertThat(password.getPassword()).isEqualTo("1234");
    }

    @Test
    @DisplayName("비밀번호를 수정한다")
    void updatePassword() {
        Password password = Password.create(
                1L,
                "tester",
                PasswordValueVo.from("1234")
        );

        password.updatePassword(PasswordValueVo.from("5678"));

        assertThat(password.getPassword()).isEqualTo("5678");
    }

    @Test
    @DisplayName("toiletId는 1 이상이어야 한다")
    void toiletIdMustBePositive() {
        assertThatThrownBy(() -> Password.create(
                0L,
                "tester",
                PasswordValueVo.from("1234")
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("toiletId는 1 이상이어야 합니다.");
    }
}
