package com.toiletnearby.global.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

class BCryptConfigTest {

    @Test
    @DisplayName("BCrypt는 원문 비밀번호를 암호화하고 matches로 검증한다")
    void encodeAndMatchPassword() {
        // BCryptConfig에서 PasswordEncoder를 직접 꺼내 테스트한다.
        BCryptConfig config = new BCryptConfig();
        PasswordEncoder passwordEncoder = config.passwordEncoder();

        String rawPassword = "password123";
        String encodedPassword = passwordEncoder.encode(rawPassword);

        // 암호화된 비밀번호는 원문과 같으면 안 된다.
        assertThat(encodedPassword).isNotEqualTo(rawPassword);

        // BCrypt 검증은 equals가 아니라 matches를 사용해야 한다.
        assertThat(passwordEncoder.matches(rawPassword, encodedPassword)).isTrue();
    }
}
