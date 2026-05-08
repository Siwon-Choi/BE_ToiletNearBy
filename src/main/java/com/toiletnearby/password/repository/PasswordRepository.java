package com.toiletnearby.password.repository;

import com.toiletnearby.password.domain.Password;

import java.util.Optional;

public interface PasswordRepository {

    Optional<Password> findByToiletIdAndUserId(Long toiletId, String userId);

    Password save(Password password);

    void deleteByToiletIdAndUserId(Long toiletId, String userId);
}
