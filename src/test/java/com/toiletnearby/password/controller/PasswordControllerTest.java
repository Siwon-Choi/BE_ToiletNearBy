package com.toiletnearby.password.controller;


import com.toiletnearby.password.domain.Password;
import com.toiletnearby.password.domain.vo.PasswordValueVo;
import com.toiletnearby.password.dto.PasswordCreateRequestDto;
import com.toiletnearby.password.dto.PasswordUpdateRequestDto;
import com.toiletnearby.password.service.PasswordService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// PasswordController의 요청/응답 형태를 테스트한다.
@WebMvcTest(PasswordController.class)
@AutoConfigureMockMvc(addFilters = false)
class PasswordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PasswordService passwordService;

    @Test
    @DisplayName("저장된 비밀번호를 조회한다")
    void getPassword() throws Exception {
        // 인증 사용자 tester가 toiletId 1번의 비밀번호를 조회한다.
        given(passwordService.getPassword(1L, "tester")).willReturn("1234");

        mockMvc.perform(get("/api/password/{tid}", 1L)
                        .principal(new TestingAuthenticationToken("tester", null)))
                .andExpect(status().isOk())
                .andExpect(content().string("1234"));

        then(passwordService).should().getPassword(1L, "tester");
    }

    @Test
    @DisplayName("비밀번호를 저장한다")
    void makePassword() throws Exception {
        Password password = Password.create(1L, "tester", PasswordValueVo.from("1234"));
        PasswordCreateRequestDto requestDto = new PasswordCreateRequestDto(1L, "1234");

        // request body의 toiletId/password와 인증 사용자를 Service DTO로 넘기는지 확인한다.
        given(passwordService.savePassword(argThat(dto ->
                dto.toiletId().equals(1L)
                        && dto.userId().equals("tester")
                        && dto.password().equals("1234")
        ))).willReturn(password);

        mockMvc.perform(post("/api/password/make")
                        .principal(new TestingAuthenticationToken("tester", null))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.toiletId").value(1))
                .andExpect(jsonPath("$.userId").value("tester"))
                .andExpect(jsonPath("$.password").value("1234"));
    }

    @Test
    @DisplayName("비밀번호를 수정한다")
    void updatePassword() throws Exception {
        Password password = Password.create(1L, "tester", PasswordValueVo.from("5678"));
        PasswordUpdateRequestDto requestDto = new PasswordUpdateRequestDto("5678");

        // 새 비밀번호는 URL이 아니라 request body로 받는다.
        // URL은 서버 로그나 브라우저 히스토리에 남을 수 있기 때문이다.
        given(passwordService.updatePassword(argThat(dto ->
                dto.toiletId().equals(1L)
                        && dto.userId().equals("tester")
                        && dto.password().equals("5678")
        ))).willReturn(password);

        mockMvc.perform(put("/api/password/{TOILET_ID}/put", 1L)
                        .principal(new TestingAuthenticationToken("tester", null))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.toiletId").value(1))
                .andExpect(jsonPath("$.userId").value("tester"))
                .andExpect(jsonPath("$.password").value("5678"));
    }

    @Test
    @DisplayName("비밀번호를 삭제한다")
    void deletePassword() throws Exception {
        given(passwordService.deletePassword(1L, "tester")).willReturn("tester");

        mockMvc.perform(delete("/api/password/{tid}/delete", 1L)
                        .principal(new TestingAuthenticationToken("tester", null)))
                .andExpect(status().isOk())
                .andExpect(content().string("tester"));

        then(passwordService).should().deletePassword(1L, "tester");
    }
}
