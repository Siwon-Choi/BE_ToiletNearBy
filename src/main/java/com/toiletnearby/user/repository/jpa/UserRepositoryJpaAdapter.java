package com.toiletnearby.user.repository.jpa;

import com.toiletnearby.user.domain.User;
import com.toiletnearby.user.domain.vo.UsernameVo;
import com.toiletnearby.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// UserRepository 인터페이스를 JPA 방식으로 구현하는 Adapter다.
// Service는 이 클래스가 아니라 UserRepository 인터페이스에만 의존한다.
@Repository
@RequiredArgsConstructor
public class UserRepositoryJpaAdapter implements UserRepository {

    private final UserJpaRepository userJpaRepository;

    // username 중복 확인 요청을 Spring Data JPA Repository로 위임한다.
    @Override
    public boolean existsByUsername(String username) {
        return userJpaRepository.existsByUsername(UsernameVo.from(username));
    }

    // User 저장 요청을 Spring Data JPA Repository로 위임한다.
    @Override
    public User save(User user) {
        return userJpaRepository.save(user);
    }

    // username 조회 요청을 Spring Data JPA Repository로 위임한다.
    @Override
    public Optional<User> findByUsername(String username) {
        return userJpaRepository.findByUsername(UsernameVo.from(username));
    }
}
