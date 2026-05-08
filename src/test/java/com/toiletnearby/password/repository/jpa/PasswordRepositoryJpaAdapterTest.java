package com.toiletnearby.password.repository.jpa;

import com.toiletnearby.password.domain.Password;
import com.toiletnearby.password.domain.vo.PasswordValueVo;
import com.toiletnearby.password.repository.PasswordRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

// PasswordRepository JPA Adapter의 실제 DB 동작을 테스트한다.
@DataJpaTest
@Import(PasswordRepositoryJpaAdapter.class)
class PasswordRepositoryJpaAdapterTest {

    @Autowired
    private PasswordRepository passwordRepository;

    @Test
    @DisplayName("toiletId와 userId로 비밀번호를 조회한다")
    void findByToiletIdAndUserId() {
        Password password = Password.create(
                1L,
                "tester",
                PasswordValueVo.from("1234")
        );

        passwordRepository.save(password);

        Optional<Password> result = passwordRepository.findByToiletIdAndUserId(1L, "tester");

        assertThat(result).isPresent();
        assertThat(result.get().getPassword()).isEqualTo("1234");
    }

    @Test
    @DisplayName("toiletId와 userId로 비밀번호를 삭제한다")
    void deleteByToiletIdAndUserId() {
        Password password = Password.create(
                1L,
                "tester",
                PasswordValueVo.from("1234")
        );

        passwordRepository.save(password);

        // 로그인 사용자와 화장실 id 기준으로 저장된 비밀번호를 삭제한다.
        passwordRepository.deleteByToiletIdAndUserId(1L, "tester");

        Optional<Password> result = passwordRepository.findByToiletIdAndUserId(1L, "tester");

        assertThat(result).isEmpty();
    }
}
