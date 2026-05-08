package com.toiletnearby.password.domain;

import com.toiletnearby.password.domain.vo.PasswordValueVo;
import com.toiletnearby.password.domain.vo.PasswordValueVoConverter;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "passwords",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_passwords_toilet_user", columnNames = {"toilet_id", "user_id"})
        },
        indexes = {
                @Index(name = "idx_passwords_user_id", columnList = "user_id"),
                @Index(name = "idx_passwords_toilet_user", columnList = "toilet_id,user_id")
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Password {

    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "toilet_id", nullable = false)
    @Getter
    private Long toiletId;

    @Column(name = "user_id", nullable = false, length = 50)
    @Getter
    private String userId;

    @Column(nullable = false, length = 255)
    @Convert(converter = PasswordValueVoConverter.class)
    private PasswordValueVo password;

    private Password(Long toiletId, String userId, PasswordValueVo password) {
        this.toiletId = toiletId;
        this.userId = userId;
        this.password = password;
    }

    // 새 화장실 비밀번호를 생성한다.
    public static Password create(Long toiletId, String userId, PasswordValueVo password) {
        validatePositiveToiletId(toiletId);
        validateRequiredUserId(userId);
        validateRequiredPassword(password);

        return new Password(toiletId, userId, password);
    }

    // 저장된 비밀번호 값을 수정한다.
    public void updatePassword(PasswordValueVo password) {
        validateRequiredPassword(password);

        this.password = password;
    }

    // 문자열 비밀번호 값만 필요하므로 VO 내부 값을 반환한다.
    public String getPassword() {
        return password.getValue();
    }

    private static void validatePositiveToiletId(Long toiletId) {
        if (toiletId == null || toiletId < 1) {
            throw new IllegalArgumentException("toiletId는 1 이상이어야 합니다.");
        }
    }

    private static void validateRequiredUserId(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("userId는 필수입니다.");
        }
    }

    private static void validateRequiredPassword(PasswordValueVo password) {
        if (password == null) {
            throw new IllegalArgumentException("password는 필수입니다.");
        }
    }
}
