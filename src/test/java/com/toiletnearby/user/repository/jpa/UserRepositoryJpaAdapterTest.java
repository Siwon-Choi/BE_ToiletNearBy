package com.toiletnearby.user.repository.jpa;

import com.toiletnearby.user.domain.User;
import com.toiletnearby.user.domain.vo.EncodedPasswordVo;
import com.toiletnearby.user.domain.vo.UsernameVo;
import com.toiletnearby.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

// JPA Repository Adapter가 실제 DB 저장소와 잘 연결되는지 확인한다.
@DataJpaTest
@Import(UserRepositoryJpaAdapter.class)
class UserRepositoryJpaAdapterTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("UserRepository 인터페이스로 회원을 저장하고 username으로 조회한다")
    void saveAndFindByUsername() {
        // UserService는 이 UserRepository 인터페이스만 알면 된다.
        User user = User.create(
                UsernameVo.from("tester"),
                EncodedPasswordVo.from("encoded-password")
        );

        userRepository.save(user);

        Optional<User> foundUser = userRepository.findByUsername("tester");

        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo("tester");
    }

    @Test
    @DisplayName("UserRepository 인터페이스로 username 중복 여부를 확인한다")
    void existsByUsername() {
        User user = User.create(
                UsernameVo.from("tester"),
                EncodedPasswordVo.from("encoded-password")
        );

        userRepository.save(user);

        assertThat(userRepository.existsByUsername("tester")).isTrue();
        assertThat(userRepository.existsByUsername("unknown")).isFalse();
    }
}
