package com.toiletnearby.user.repository.jpa;

import com.toiletnearby.user.domain.User;
import com.toiletnearby.user.domain.vo.UsernameVo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// Spring Data JPA가 실제 DB 접근 코드를 만들어주는 인터페이스다.
// Service가 직접 사용하지 않고, Adapter가 이 인터페이스를 감싼다.
public interface UserJpaRepository extends JpaRepository<User, Long> {

    // users 테이블에서 username 중복 여부를 확인한다.
    boolean existsByUsername(UsernameVo username);

    // users 테이블에서 username으로 회원을 조회한다.
    Optional<User> findByUsername(UsernameVo username);
}
