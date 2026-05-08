package com.toiletnearby.password.service;

import com.toiletnearby.password.domain.Password;
import com.toiletnearby.password.dto.PasswordSaveDto;
import com.toiletnearby.password.repository.PasswordRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordServiceTest {

    @Mock
    private PasswordRepository passwordRepository;

    @InjectMocks
    private PasswordService passwordService;

    @Test
    @DisplayName("저장된 비밀번호를 조회한다")
    void getPassword() {
        Password password = Password.create(1L, "tester", com.toiletnearby.password.domain.vo.PasswordValueVo.from("1234"));

        given(passwordRepository.findByToiletIdAndUserId(1L, "tester"))
                .willReturn(Optional.of(password));

        String result = passwordService.getPassword(1L, "tester");

        assertThat(result).isEqualTo("1234");
    }

    @Test
    @DisplayName("비밀번호가 없으면 조회 시 예외가 발생한다")
    void getUnknownPassword() {
        given(passwordRepository.findByToiletIdAndUserId(1L, "tester"))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> passwordService.getPassword(1L, "tester"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("비밀번호가 없습니다.");
    }

    @Test
    @DisplayName("새 비밀번호를 저장한다")
    void saveNewPassword() {
        PasswordSaveDto dto = new PasswordSaveDto(1L, "tester", "1234");

        given(passwordRepository.findByToiletIdAndUserId(1L, "tester"))
                .willReturn(Optional.empty());
        given(passwordRepository.save(any(Password.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        Password result = passwordService.savePassword(dto);

        assertThat(result.getToiletId()).isEqualTo(1L);
        assertThat(result.getUserId()).isEqualTo("tester");
        assertThat(result.getPassword()).isEqualTo("1234");
    }

    @Test
    @DisplayName("이미 있으면 기존 비밀번호를 덮어쓴다")
    void overwritePassword() {
        Password password = Password.create(1L, "tester", com.toiletnearby.password.domain.vo.PasswordValueVo.from("1234"));
        PasswordSaveDto dto = new PasswordSaveDto(1L, "tester", "5678");

        given(passwordRepository.findByToiletIdAndUserId(1L, "tester"))
                .willReturn(Optional.of(password));
        given(passwordRepository.save(password)).willReturn(password);

        Password result = passwordService.savePassword(dto);

        assertThat(result.getPassword()).isEqualTo("5678");
    }

    @Test
    @DisplayName("비밀번호를 삭제한다")
    void deletePassword() {
        String result = passwordService.deletePassword(1L, "tester");

        assertThat(result).isEqualTo("tester");
        then(passwordRepository).should().deleteByToiletIdAndUserId(1L, "tester");
    }
}
