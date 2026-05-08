package com.toiletnearby.global.security.jwt;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

// application.properties에 있는 Jwt 설정값을 들고 있는 클래스
@Getter
@Component
public class JwtProperties {

    private final String secretKey;
    private final long accessTokenExpirationMillis;

    public JwtProperties(
            @Value("${jwt.secret-key}") String secretKey,
            @Value("${jwt.access-token-expiration-millis}") long accessTokenExpirationMillis
    ) {
        this.secretKey = secretKey;
        this.accessTokenExpirationMillis = accessTokenExpirationMillis;
    }

}
