package com.toiletnearby.user.service;

import com.toiletnearby.global.security.jwt.JwtTokenProvider;
import com.toiletnearby.user.domain.User;
import com.toiletnearby.user.domain.UserRole;
import com.toiletnearby.user.domain.vo.EncodedPasswordVo;
import com.toiletnearby.user.domain.vo.UsernameVo;
import com.toiletnearby.user.dto.UserLoginDto;
import com.toiletnearby.user.dto.UserLoginResultDto;
import com.toiletnearby.user.dto.UserRegisterDto;
import com.toiletnearby.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Mock
    private JwtTokenProvider jwtTokenProvider;


    @Test
    @DisplayName("회원가입 시 비밀번호를 암호화해서 저장한다")
    void registerUser() {
        UserRegisterDto dto = new UserRegisterDto("tester", "password123");

        given(userRepository.existsByUsername("tester")).willReturn(false);
        given(passwordEncoder.encode("password123")).willReturn("encoded-password");
        given(userRepository.save(any(User.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        User savedUser = userService.register(dto);

        assertThat(savedUser.getUsername()).isEqualTo("tester");
        assertThat(savedUser.getPassword()).isEqualTo("encoded-password");
        assertThat(savedUser.getPassword()).isNotEqualTo("password123");

        then(passwordEncoder).should().encode("password123");
        then(userRepository).should().save(any(User.class));
    }

    @Test
    @DisplayName("이미 존재하는 username으로는 가입할 수 없다")
    void cannotRegisterDuplicatedUsername() {
        UserRegisterDto dto = new UserRegisterDto("tester", "password123");

        given(userRepository.existsByUsername("tester")).willReturn(true);

        assertThatThrownBy(() -> userService.register(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 존재하는 username입니다.");

        then(passwordEncoder).should(never()).encode(anyString());
        then(userRepository).should(never()).save(any(User.class));
    }

    @Test
    @DisplayName("로그인 시 username과 password가 일치하면 User를 반환한다")
    void loginUser() {
        UserLoginDto dto = new UserLoginDto("tester", "password123");
        User user = User.create(
                UsernameVo.from("tester"),
                EncodedPasswordVo.from("encoded-password")
        );

        given(userRepository.findByUsername("tester")).willReturn(Optional.of(user));
        given(passwordEncoder.matches("password123", "encoded-password")).willReturn(true);
        given(jwtTokenProvider.createAccessToken("tester", UserRole.USER)).willReturn("access-token");

        UserLoginResultDto loginResult = userService.login(dto);



        assertThat(loginResult.username()).isEqualTo("tester");
        assertThat(loginResult.accessToken()).isEqualTo("access-token");

        then(passwordEncoder).should().matches("password123", "encoded-password");
        then(jwtTokenProvider).should().createAccessToken("tester", UserRole.USER);
    }

    @Test
    @DisplayName("존재하지 않는 username으로 로그인할 수 없다")
    void cannotLoginWithUnknownUsername() {
        UserLoginDto dto = new UserLoginDto("tester", "password123");

        given(userRepository.findByUsername("tester")).willReturn(Optional.empty());

        assertThatThrownBy(() -> userService.login(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("username 또는 password가 일치하지 않습니다.");

        then(passwordEncoder).should(never()).matches(anyString(), anyString());
    }

    @Test
    @DisplayName("password가 일치하지 않으면 로그인할 수 없다")
    void cannotLoginWithWrongPassword() {
        UserLoginDto dto = new UserLoginDto("tester", "wrong-password");
        User user = User.create(
                UsernameVo.from("tester"),
                EncodedPasswordVo.from("encoded-password")
        );

        given(userRepository.findByUsername("tester")).willReturn(Optional.of(user));
        given(passwordEncoder.matches("wrong-password", "encoded-password")).willReturn(false);

        assertThatThrownBy(() -> userService.login(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("username 또는 password가 일치하지 않습니다.");

        then(jwtTokenProvider).should(never()).createAccessToken(anyString(), any());
    }
}
