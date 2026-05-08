package com.toiletnearby.user.repository;

import com.toiletnearby.user.domain.User;

import java.util.Optional;

// UserService가 필요로 하는 저장소 기능만 정의하는 인터페이스다.
public interface UserRepository {

    // 회원가입 시 username 중복 여부를 확인할 때 사용한다.
    boolean existsByUsername(String username);

    // 회원가입이 끝난 User 엔티티를 저장한다.
    User save(User user);

    // 로그인할 때 username으로 사용자를 찾기 위해 사용한다.
    Optional<User> findByUsername(String username);
}
