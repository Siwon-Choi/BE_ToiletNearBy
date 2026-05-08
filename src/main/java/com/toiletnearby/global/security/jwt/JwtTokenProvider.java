package com.toiletnearby.global.security.jwt;

import com.toiletnearby.user.domain.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component // 스프링 빈으로 등록
@RequiredArgsConstructor // final 필드를 받는 생성자 생성
public class JwtTokenProvider {
    // Authorization 헤더는 'Bearer ~~~'처럼 오는데, '~~~'이 실제 토큰이니 Bearer를 접두사로 둔다.
    private static final String BEARER_PREFIX = "Bearer ";
    // Jwt 안에는 추가 정보를 넣을 수 있는데, 권한 정보를 role이라는 이름으로 넣음
    private static final String ROLE_CLAIM_NAME = "role";

    private final JwtProperties jwtProperties;
    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;

    // access token을 생성 -> 로그인 성공 시 호출할 예정
    public String createAccessToken(String username, UserRole role) {
        Instant now = Instant.now();
        Instant expiration = now.plusMillis(jwtProperties.getAccessTokenExpirationMillis());

        // JWT payload에 들어갈 값을 Spring Security의 ClaimsSet으로 만든다.
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(username)
                .claim(ROLE_CLAIM_NAME, role.name())
                .issuedAt(now)
                .expiresAt(expiration)
                .build();

        // HS256 대칭키 알고리즘으로 서명한다.
        JwsHeader jwsHeader = JwsHeader.with(MacAlgorithm.HS256).build();

        // JwtEncoder가 서명된 JWT 문자열을 만들어준다.
        return jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims))
                .getTokenValue();
    }

    // Authorization header에서 "Bearer " 뒤의 실제 토큰만 꺼낸다.
    public String resolveToken(String authorizationHeader) {
        // 비어있거나, B"Bearer "로 시작하지 않는다면 null 반환
        if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER_PREFIX)) {
            return null;
        }

        // 뒤에꺼만 반환
        return authorizationHeader.substring(BEARER_PREFIX.length());
    }

    // 토큰의 유효성 검사
    public boolean validateToken(String token) {
        try {
            // 정상 토큰이라면 JwtDecoder가 예외 없이 디코딩한다.
            jwtDecoder.decode(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            // 토큰이 만료되거나, 서명이 틀리거나, 토큰 형식이 이상하거나, 토큰이 null 또는 빈 값일 경우 false
            return false;
        }
    }

    // 토큰에서 username을 꺼낸다.
    public String getUsername(String token) {
        return jwtDecoder.decode(token).getSubject();
    }

    // 토큰에서 role을 꺼낸다.
    public UserRole getRole(String token) {
        String role = jwtDecoder.decode(token).getClaimAsString(ROLE_CLAIM_NAME);
        return UserRole.valueOf(role);
    }
}
