package com.toiletnearby.toilet.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// 실제 Security 필터 체인을 켠 상태로 공개 조회 API를 테스트한다.
@SpringBootTest
@AutoConfigureMockMvc
class ToiletSecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("화장실 목록 조회 API는 JWT 없이 접근할 수 있다")
    void getToiletsWithoutToken() throws Exception {
        mockMvc.perform(get("/api/toilets"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("내 메모 조회 API는 JWT 없이 접근할 수 없다")
    void cannotAccessMyMemoWithoutToken() throws Exception {
        mockMvc.perform(get("/api/toilet/mymemo"))
                .andExpect(status().isUnauthorized());
    }
}
