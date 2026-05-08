package com.toiletnearby.global.security.jwt;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.toiletnearby.user.domain.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

// JwtTokenProvider의 토큰 생성, 검증, 정보 추출 기능을 테스트한다.
class JwtTokenProviderTest {

    private static final String SECRET_KEY = "toilet-nearby-clone-secret-key-for-test";

    @Test
    @DisplayName("username과 role을 담은 JWT access token을 생성한다")
    void createAccessToken() {
        JwtTokenProvider jwtTokenProvider = createJwtTokenProvider(SECRET_KEY, 3600000L);

        String token = jwtTokenProvider.createAccessToken("tester", UserRole.USER);

        assertThat(token).isNotBlank();
        assertThat(token).isNotEqualTo("tester");
        assertThat(jwtTokenProvider.validateToken(token)).isTrue();
    }

    @Test
    @DisplayName("JWT에서 username과 role을 꺼낸다")
    void getUsernameAndRole() {
        JwtTokenProvider jwtTokenProvider = createJwtTokenProvider(SECRET_KEY, 3600000L);

        String token = jwtTokenProvider.createAccessToken("tester", UserRole.USER);

        assertThat(jwtTokenProvider.getUsername(token)).isEqualTo("tester");
        assertThat(jwtTokenProvider.getRole(token)).isEqualTo(UserRole.USER);
    }

    @Test
    @DisplayName("잘못된 JWT는 유효하지 않다")
    void invalidToken() {
        JwtTokenProvider jwtTokenProvider = createJwtTokenProvider(SECRET_KEY, 3600000L);

        boolean valid = jwtTokenProvider.validateToken("invalid-token");

        assertThat(valid).isFalse();
    }

    @Test
    @DisplayName("Authorization header에서 Bearer 토큰만 꺼낸다")
    void resolveToken() {
        JwtTokenProvider jwtTokenProvider = createJwtTokenProvider(SECRET_KEY, 3600000L);

        // "Bearer " 부분을 제거하고 실제 JWT 문자열만 반환해야 한다.
        assertThat(jwtTokenProvider.resolveToken("Bearer abc.def.ghi")).isEqualTo("abc.def.ghi");
        // Authorization 헤더 값이 "Bearer "로 시작하지 않으면,
        // 올바른 Bearer 토큰 형식이 아니므로 null을 반환해야 한다
        assertThat(jwtTokenProvider.resolveToken("abc.def.ghi")).isNull();
        // Authorization 헤더 자체가 null이면,
        // 꺼낼 토큰이 없으므로 null을 반환해야 한다.
        assertThat(jwtTokenProvider.resolveToken(null)).isNull();
    }

    // JwtTokenProvider를 jwtEncoder, jwtDecoder, JwtProperties를 통해 반환
    private JwtTokenProvider createJwtTokenProvider(String secretKey, long expirationMillis) {
        JwtProperties jwtProperties = new JwtProperties(secretKey, expirationMillis);
        SecretKey key = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");

        JwtEncoder jwtEncoder = new NimbusJwtEncoder(new ImmutableSecret<>(key));
        JwtDecoder jwtDecoder = NimbusJwtDecoder
                .withSecretKey(key)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();

        return new JwtTokenProvider(jwtProperties, jwtEncoder, jwtDecoder);
    }
}
