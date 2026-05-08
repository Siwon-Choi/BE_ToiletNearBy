package com.toiletnearby.user.controller;

import com.toiletnearby.global.security.jwt.JwtTokenProvider;
import com.toiletnearby.user.domain.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.hasItem;

// 실제 Spring Security 필터 체인을 켠 상태로 인증 흐름을 테스트한다.
@SpringBootTest
@AutoConfigureMockMvc
class UserSecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("JWT 없이 인증이 필요한 API에 접근하면 401 에러 응답을 반환한다")
    void cannotAccessMeWithoutToken() throws Exception {
        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("인증이 필요합니다."))
                .andExpect(jsonPath("$.path").value("/api/users/me"))
                .andExpect(jsonPath("$.timestamp").exists());
    }


    @Test
    @DisplayName("유효하지 않은 JWT로 인증이 필요한 API에 접근하면 401 에러 응답을 반환한다")
    void cannotAccessMeWithInvalidToken() throws Exception {
        mockMvc.perform(get("/api/users/me")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer invalid-token"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("인증이 필요합니다."))
                .andExpect(jsonPath("$.path").value("/api/users/me"))
                .andExpect(jsonPath("$.timestamp").exists());
    }


    @Test
    @DisplayName("유효한 JWT로 인증이 필요한 API에 접근할 수 있다")
    void accessMeWithValidToken() throws Exception {
        String accessToken = jwtTokenProvider.createAccessToken("tester", UserRole.USER);

        mockMvc.perform(get("/api/users/me")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("tester"))
                .andExpect(jsonPath("$.authorities").value(hasItem("USER")))
                .andExpect(jsonPath("$.message").value("인증된 사용자입니다."));
    }

    @Test
    @DisplayName("USER 권한 JWT로 ADMIN API에 접근하면 403 에러 응답을 반환한다")
    void cannotAccessAdminApiWithUserToken() throws Exception {
        String accessToken = jwtTokenProvider.createAccessToken("tester", UserRole.USER);

        mockMvc.perform(get("/api/users/admin-test")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isForbidden())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.error").value("Forbidden"))
                .andExpect(jsonPath("$.message").value("접근 권한이 없습니다."))
                .andExpect(jsonPath("$.path").value("/api/users/admin-test"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("ADMIN 권한 JWT로 ADMIN API에 접근할 수 있다")
    void accessAdminApiWithAdminToken() throws Exception {
        String accessToken = jwtTokenProvider.createAccessToken("admin", UserRole.ADMIN);

        mockMvc.perform(get("/api/users/admin-test")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("관리자 권한 접근에 성공했습니다."));
    }

}
