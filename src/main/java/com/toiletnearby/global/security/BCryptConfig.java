package com.toiletnearby.global.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class BCryptConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCrypt는 비밀번호 저장에 사용하는 단방향 해시 알고리즘이다.
        return new BCryptPasswordEncoder();
    }
}
