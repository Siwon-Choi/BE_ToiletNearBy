package com.toiletnearby.user.dto;

import com.toiletnearby.user.domain.User;
import com.toiletnearby.user.domain.vo.EncodedPasswordVo;
import com.toiletnearby.user.domain.vo.UsernameVo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserRegisterResponseDtoTest {

    @Test
    @DisplayName("User 엔티티로 회원가입 응답 DTO를 생성한다")
    void createResponseFromUser() {
        // given: 회원가입 후 저장된 User 엔티티를 준비한다.
        User user = User.create(
                UsernameVo.from("tester"),
                EncodedPasswordVo.from("encoded-password")
        );

        // when: User 엔티티를 응답 DTO로 변환한다.
        UserRegisterResponseDto responseDto = UserRegisterResponseDto.from(user);

        // then: record DTO는 getUsername()이 아니라 username()으로 값을 꺼낸다.
        assertThat(responseDto.username()).isEqualTo("tester");
        assertThat(responseDto.message()).isEqualTo("회원가입이 완료되었습니다.");
    }
}
