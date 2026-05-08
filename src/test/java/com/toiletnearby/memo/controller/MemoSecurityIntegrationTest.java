package com.toiletnearby.memo.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// 실제 Security 필터 체인을 켠 상태로 메모 API 보안 설정을 테스트한다.
@SpringBootTest
@AutoConfigureMockMvc
class MemoSecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("특정 화장실의 메모 목록 조회는 JWT 없이 접근할 수 있다")
    void getMemosWithoutToken() throws Exception {
        mockMvc.perform(get("/api/toilet/{TOILET_ID}/memos", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("내 메모 조회는 JWT 없이 접근할 수 없다")
    void cannotGetMyMemoWithoutToken() throws Exception {
        mockMvc.perform(get("/api/toilet/mymemo"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("특정 메모 조회는 JWT 없이 접근할 수 없다")
    void cannotGetMemoWithoutToken() throws Exception {
        mockMvc.perform(get("/api/toilet/{TOILET_ID}/memo/{MEMO_ID}", 1L, 1L))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("메모 작성은 JWT 없이 접근할 수 없다")
    void cannotCreateMemoWithoutToken() throws Exception {
        String requestBody = """
                {
                  "toiletid": 1,
                  "contents": "깨끗합니다.",
                  "good": 5
                }
                """;

        mockMvc.perform(post("/api/toilet/memo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("메모 수정은 JWT 없이 접근할 수 없다")
    void cannotUpdateMemoWithoutToken() throws Exception {
        String requestBody = """
                {
                  "contents": "수정 후"
                }
                """;

        mockMvc.perform(put("/api/toilet/{TOILET_ID}/memo/{MEMO_ID}", 1L, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("메모 삭제는 JWT 없이 접근할 수 없다")
    void cannotDeleteMemoWithoutToken() throws Exception {
        mockMvc.perform(delete("/api/toilet/{TOILET_ID}/memo/{MEMO_ID}", 1L, 1L))
                .andExpect(status().isUnauthorized());
    }
}
