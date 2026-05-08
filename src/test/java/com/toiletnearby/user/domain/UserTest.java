package com.toiletnearby.user.domain;

import com.toiletnearby.user.domain.vo.EncodedPasswordVo;
import com.toiletnearby.user.domain.vo.UsernameVo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    @Test
    @DisplayName("회원 생성 시 기본 권한은 USER다")
    void createUserWithDefaultRole() {
        // User 엔티티는 DTO가 아니라 검증된 VO를 받는다.
        UsernameVo username = UsernameVo.from("tester");
        EncodedPasswordVo password = EncodedPasswordVo.from("encoded-password");

        User user = User.create(username, password);

        assertThat(user.getUsername()).isEqualTo("tester");
        assertThat(user.getPassword()).isEqualTo("encoded-password");
        assertThat(user.getRole()).isEqualTo(UserRole.USER);
    }
}
