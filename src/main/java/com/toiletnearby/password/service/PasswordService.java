package com.toiletnearby.password.service;

import com.toiletnearby.password.domain.Password;
import com.toiletnearby.password.domain.vo.PasswordValueVo;
import com.toiletnearby.password.dto.PasswordSaveDto;
import com.toiletnearby.password.repository.PasswordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

// 화장실 비밀번호 관련 비즈니스 흐름을 담당한다.
@Service
@RequiredArgsConstructor
@Transactional
public class PasswordService {

    private final PasswordRepository passwordRepository;

    // 로그인 사용자가 특정 화장실에 저장한 비밀번호를 조회한다.
    @Transactional(readOnly = true)
    public String getPassword(Long toiletId, String userId) {
        return getPasswordOrThrow(toiletId, userId).getPassword();
    }

    // 비밀번호를 새로 저장한다. 이미 있으면 기존 값을 덮어쓴다.
    public Password savePassword(PasswordSaveDto dto) {
        validateRequiredUserId(dto.userId());

        PasswordValueVo passwordValue = PasswordValueVo.from(dto.password());

        Password password = passwordRepository.findByToiletIdAndUserId(dto.toiletId(), dto.userId())
                .orElseGet(() -> Password.create(dto.toiletId(), dto.userId(), passwordValue));

        password.updatePassword(passwordValue);

        return passwordRepository.save(password);
    }

    // 이미 저장된 비밀번호를 수정한다.
    public Password updatePassword(PasswordSaveDto dto) {
        Password password = getPasswordOrThrow(dto.toiletId(), dto.userId());

        password.updatePassword(PasswordValueVo.from(dto.password()));

        return passwordRepository.save(password);
    }

    // 로그인 사용자의 특정 화장실 비밀번호를 삭제한다.
    public String deletePassword(Long toiletId, String userId) {
        validatePositiveToiletId(toiletId);
        validateRequiredUserId(userId);

        passwordRepository.deleteByToiletIdAndUserId(toiletId, userId);

        return userId;
    }

    // toiletId + userId 기준으로 비밀번호를 찾는다.
    private Password getPasswordOrThrow(Long toiletId, String userId) {
        validatePositiveToiletId(toiletId);
        validateRequiredUserId(userId);

        return passwordRepository.findByToiletIdAndUserId(toiletId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "비밀번호가 없습니다."));
    }

    private void validatePositiveToiletId(Long toiletId) {
        if (toiletId == null || toiletId < 1) {
            throw new IllegalArgumentException("toiletId는 1 이상이어야 합니다.");
        }
    }

    private void validateRequiredUserId(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("userId는 필수입니다.");
        }
    }
}
