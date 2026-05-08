package com.toiletnearby.password.repository.jpa;

import com.toiletnearby.password.domain.Password;
import com.toiletnearby.password.repository.PasswordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// PasswordRepository 인터페이스를 JPA 방식으로 구현하는 Adapter
@Repository
@RequiredArgsConstructor
public class PasswordRepositoryJpaAdapter implements PasswordRepository {

    private final PasswordJpaRepository passwordJpaRepository;

    @Override
    public Optional<Password> findByToiletIdAndUserId(Long toiletId, String userId) {
        return passwordJpaRepository.findByToiletIdAndUserId(toiletId, userId);
    }

    @Override
    public Password save(Password password) {
        return passwordJpaRepository.save(password);
    }

    @Override
    public void deleteByToiletIdAndUserId(Long toiletId, String userId) {
        passwordJpaRepository.deleteByToiletIdAndUserId(toiletId, userId);
    }
}
