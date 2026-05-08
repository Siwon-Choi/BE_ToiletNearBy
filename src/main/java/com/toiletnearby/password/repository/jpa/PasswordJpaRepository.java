package com.toiletnearby.password.repository.jpa;

import com.toiletnearby.password.domain.Password;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordJpaRepository extends JpaRepository<Password, Long> {

    Optional<Password> findByToiletIdAndUserId(Long toiletId, String userId);

    void deleteByToiletIdAndUserId(Long toiletId, String userId);
}
