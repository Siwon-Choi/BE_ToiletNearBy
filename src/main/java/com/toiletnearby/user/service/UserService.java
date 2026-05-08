package com.toiletnearby.user.service;

import com.toiletnearby.global.security.jwt.JwtTokenProvider;
import com.toiletnearby.user.domain.User;
import com.toiletnearby.user.domain.vo.EncodedPasswordVo;
import com.toiletnearby.user.domain.vo.RawPasswordVo;
import com.toiletnearby.user.domain.vo.UsernameVo;
import com.toiletnearby.user.dto.UserLoginDto;
import com.toiletnearby.user.dto.UserLoginResultDto;
import com.toiletnearby.user.dto.UserRegisterDto;
import com.toiletnearby.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public User register(UserRegisterDto dto) {
        // Service DTO 값을 도메인 VO로 변환한다.
        UsernameVo username = UsernameVo.from(dto.username());
        RawPasswordVo rawPassword = RawPasswordVo.from(dto.password());

        // username 중복 여부를 확인한다.
        if (userRepository.existsByUsername(username.getValue())) {
            throw new IllegalArgumentException("이미 존재하는 username입니다.");
        }

        // 원문 비밀번호는 저장하지 않고 BCrypt로 암호화한다.
        String encodedValue = passwordEncoder.encode(rawPassword.getValue());
        EncodedPasswordVo encodedPassword = EncodedPasswordVo.from(encodedValue);

        // User 엔티티는 DTO가 아니라 VO를 받는다.
        User user = User.create(username, encodedPassword);

        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public UserLoginResultDto login(UserLoginDto dto) {
        // Dto -> Vo로 변환
        UsernameVo username = UsernameVo.from(dto.username());
        RawPasswordVo rawPassword = RawPasswordVo.from(dto.password());

        User user = userRepository.findByUsername(username.getValue())
                .orElseThrow(() -> new IllegalArgumentException("username 또는 password가 일치하지 않습니다."));

        if (!passwordEncoder.matches(rawPassword.getValue(), user.getPassword())) {
            throw new IllegalArgumentException("username 또는 password가 일치하지 않습니다.");
        }

        // 로그인 성공 시 username과 role을 담은 JWT access token을 만든다.
        String accessToken = jwtTokenProvider.createAccessToken(user.getUsername(), user.getRole());

        return new UserLoginResultDto(user.getUsername(), accessToken);
    }
}
