package com.toiletnearby.user.domain.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

// 암호화된 password
class EncodedPasswordVoTest {

    @Test
    @DisplayName("암호화된 비밀번호를 생성한다")
    void createEncodedPassword() {
        EncodedPasswordVo password = EncodedPasswordVo.from("encoded-password");

        assertThat(password.getValue()).isEqualTo("encoded-password");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    @DisplayName("암호화된 비밀번호는 비어 있을 수 없다")
    void encodedPasswordIsRequired(String value) {
        assertThatThrownBy(() -> EncodedPasswordVo.from(value))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("encodedPassword는 필수입니다.");
    }
}
