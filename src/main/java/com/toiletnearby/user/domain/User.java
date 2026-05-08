package com.toiletnearby.user.domain;

import com.toiletnearby.user.domain.vo.EncodedPasswordVo;
import com.toiletnearby.user.domain.vo.EncodedPasswordVoConverter;
import com.toiletnearby.user.domain.vo.UsernameVo;
import com.toiletnearby.user.domain.vo.UsernameVoConverter;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "users",
        indexes = {
                @Index(name = "idx_users_username", columnList = "username", unique = true)
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 로그인 아이디다.
    @Column(nullable = false, length = 20, unique = true)
    @Convert(converter = UsernameVoConverter.class)
    private UsernameVo username;

    // BCrypt로 암호화된 비밀번호만 저장한다.
    @Column(nullable = false)
    @Convert(converter = EncodedPasswordVoConverter.class)
    private EncodedPasswordVo password;

    // USER, ADMIN 같은 권한을 문자열로 저장한다.
    @Enumerated(EnumType.STRING)
    @Getter
    @Column(nullable = false, length = 20)
    private UserRole role;

    private User(UsernameVo username, EncodedPasswordVo password, UserRole role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // DTO가 아니라 VO를 받는다.
    // 도메인 엔티티가 DTO를 알지 않게 하기 위한 설계다.
    public static User create(UsernameVo username, EncodedPasswordVo encodedPassword) {
        return new User(username, encodedPassword, UserRole.USER);
    }

    public String getUsername() {
        return username.getValue();
    }

    public String getPassword() {
        return password.getValue();
    }

}
