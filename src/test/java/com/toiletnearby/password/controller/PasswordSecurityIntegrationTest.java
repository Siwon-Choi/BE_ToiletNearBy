package com.toiletnearby.password.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// 실제 Security 필터 체인을 켠 상태로 Password API 인증 여부를 테스트한다.
@SpringBootTest
@AutoConfigureMockMvc
class PasswordSecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("비밀번호 조회 API는 JWT 없이 접근할 수 없다")
    void cannotGetPasswordWithoutToken() throws Exception {
        mockMvc.perform(get("/api/password/{tid}", 1L))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("비밀번호 저장 API는 JWT 없이 접근할 수 없다")
    void cannotMakePasswordWithoutToken() throws Exception {
        mockMvc.perform(post("/api/password/make")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "toiletId": 1,
                                  "password": "1234"
                                }
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("비밀번호 수정 API는 JWT 없이 접근할 수 없다")
    void cannotUpdatePasswordWithoutToken() throws Exception {
        mockMvc.perform(put("/api/password/{TOILET_ID}/put", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "password": "5678"
                                }
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("비밀번호 삭제 API는 JWT 없이 접근할 수 없다")
    void cannotDeletePasswordWithoutToken() throws Exception {
        mockMvc.perform(delete("/api/password/{tid}/delete", 1L))
                .andExpect(status().isUnauthorized());
    }
}
