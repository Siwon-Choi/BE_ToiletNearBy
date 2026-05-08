package com.toiletnearby.global.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;



@RequiredArgsConstructor
@Configuration // 설정 클래스 -> 안에 있는 @Bean 메서드들을 스프링 빈으로 등록
@EnableWebSecurity // 스프링 시큐리티 설정을 활성화 -> 명시적으로 활성화
public class SecurityConfig {

    private final SecurityExceptionHandler securityExceptionHandler;

    // securityFilterChain -> 어떤 요청을 허용/차단할지, JWT 인증을 어떻게 쓸지 설정
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // Jwt 기반 REST API에서는 기본 로그인 x
                .httpBasic(AbstractHttpConfigurer::disable)
                // Jwt -> 서버 세션 기반 CSRF 보호 x
                .csrf(AbstractHttpConfigurer::disable)
                // CORS는 킨다
                .cors(Customizer.withDefaults())
                // Jwt는 서버 세션에 로그인 상태 저장 x
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 인증/권한에 대한 예외 처리
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(securityExceptionHandler)
                        .accessDeniedHandler(securityExceptionHandler)
                )
                // 회원가입과 로그인은 토큰 없이
                .authorizeHttpRequests(auth -> auth
                        // 브라우저의 사전 CORS 요청 통과
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // 회원가입과 로그인 통과
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/users/register",
                                "/api/users/login"
                        ).permitAll()
                        // 메모 개인/상세/변경 API는 인증 필요
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/toilet/mymemo",
                                "/api/toilet/*/memo/*"
                        ).authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/toilet/memo").authenticated()
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/toilet/*/memo/*",
                                "/api/toilet/*/memo/*/good"
                        ).authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/toilet/*/memo/*").authenticated()
                        // 화장실 비밀번호 API는 로그인 사용자 기준으로 동작하므로 인증 필요
                        .requestMatchers(HttpMethod.GET, "/api/password/*").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/password/make").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/password/*/put").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/password/*/delete").authenticated()
                        // 화장실 조회와 화장실별 메모 목록 조회는 로그인 없이 접근 가능하다.
                        .requestMatchers(
                                HttpMethod.GET, "/api/toilets",
                                "/api/toiletsNearBy/**",
                                "/api/toilet/*",
                                "/api/toilet/*/memos",
                                "/api/places/search"
                        ).permitAll()

                        // 관리자 api는 ADMIN 권한 있어야지 호출 가능
                        .requestMatchers(HttpMethod.GET, "/api/users/admin-test").hasAuthority("ADMIN")
                        // 나머지는 인증 필요
                        .anyRequest().authenticated()
                )
                // Authorization: Bearer 토큰 인증 처리를 Spring Security Resource Server에 위임
                .oauth2ResourceServer(oauth2 -> oauth2
                        // Authorization 헤더에서 Bearer 토큰 추출
                        // Jwt 서명 검증
                        // 만료 시간 검증
                        // Jwt claim 읽기 -> sub(토큰 주인), role(권한->커스텀), iat, exp
                        // 인증 객체 Authentication 생성
                        // SecurityContext(인증된 사용자 정보를 담아두는 공간)에 저장
                        .authenticationEntryPoint(securityExceptionHandler)
                        .accessDeniedHandler(securityExceptionHandler)
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                        // -> jwtAuthenticationConverter()를 사용해서 Spring Security용 Authentication 객체로 변환
                )
                .build();
    }


    // jwtAuthenticationConverter -> Jwt 안의 "role" 값을 권한으로 바꾼다
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();

        // JWT Payload 안의 "role" claim 값을 문자열로 꺼낸다.
        // JWT Payload가 { "sub": "tester", "role": "USER" } 라면
        // role 변수에는 "USER"가 들어간다.
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            String role = jwt.getClaimAsString("role");

            // role claim이 없거나 빈 문자열이면 권한이 없다고 판단한다.
            // 이 경우 Authentication 객체는 만들어질 수 있지만,
            // authorities 목록은 비어 있게 된다.
            if(role == null || role.isBlank()) {
                return List.of();
            }

            // 꺼낸 role 문자열을 Spring Security가 이해하는 권한 객체로 변환한다.
            return List.of(new SimpleGrantedAuthority(role));
        });

        // role claim을 권한으로 변환하도록 설정된 JwtAuthenticationConverter를 반환한다.
        // 이 Bean은 SecurityConfig의 oauth2ResourceServer().jwt() 설정에서 사용된다.
        return converter;
    }

    // corsConfigurationSource -> 어떤 프론트 주소에서 api 요청을 허용할지 설정한다.
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of(
                "http://localhost:5173",
                "http://127.0.0.1:5173",
                "http://localhost:5174",
                "http://127.0.0.1:5174",
                "http://localhost:63342"
        ));

        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));

        // 나중에 토큰을 응답 Header에 담을 경우 프론트에서 Authorization을 읽을 수 있게 한다.
        configuration.setExposedHeaders(List.of(HttpHeaders.AUTHORIZATION));

        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
