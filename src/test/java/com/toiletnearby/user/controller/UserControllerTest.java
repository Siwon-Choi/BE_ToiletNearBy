package com.toiletnearby.user.controller;

import com.toiletnearby.user.domain.User;
import com.toiletnearby.user.domain.vo.EncodedPasswordVo;
import com.toiletnearby.user.domain.vo.UsernameVo;
import com.toiletnearby.user.dto.UserLoginResultDto;
import com.toiletnearby.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    // 실제 서버를 띄우지 않고도 Controller API를 테스트하게 해주는 도구
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Test
    @DisplayName("회원가입 API는 요청을 받아 회원가입 결과를 응답한다")
    void registerUser() throws Exception {
        // 회원가입 시 보낼 요청
        String requestBody = """
                {
                  "username": "tester",
                  "password": "password123"
                }
                """;

        // 회원가입 성공 시 반환할 가짜 User
        User savedUser = User.create(
                UsernameVo.from("tester"),
                EncodedPasswordVo.from("encoded-password")
        );

        // 해당 인자로 회원가입 성공 시 saveUser를 반환한다.
        given(userService.register(argThat(dto ->
                dto.username().equals("tester")
                        && dto.password().equals("password123")
        ))).willReturn(savedUser);

        // 실제 실행
        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value("tester"))
                .andExpect(jsonPath("$.message").value("회원가입이 완료되었습니다."));

        // 테스트 중 register이 tester와 password123이란 것으로 실행됐는지를 마지막으로 확인한다.
        then(userService).should().register(argThat(dto ->
                dto.username().equals("tester")
                        && dto.password().equals("password123")
        ));
    }

    @Test
    @DisplayName("로그인 API는 요청을 받아 로그인 결과를 응답한다")
    void loginUser() throws Exception {
        // 사용자가 로그인 시 보내는 요청
        String requestBody = """
                {
                  "username": "tester",
                  "password": "password123"
                }
                """;

        // Service가 반환할 가짜 로그인 결과값
        UserLoginResultDto loginResult = new UserLoginResultDto("tester", "access-token");

        // userService의 로그인이 해당 인자로 해서 성공을 하면, 위의 가짜 로그인 결과값을 리턴한다.
        given(userService.login(argThat(dto ->
                dto != null
                        && dto.username().equals("tester")
                        && dto.password().equals("password123")
        ))).willReturn(loginResult);

        // 실제 test
        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("tester"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.accessToken").value("access-token"))
                .andExpect(jsonPath("$.message").value("로그인이 완료되었습니다."));

        // 테스트 중 userService.login()이 실제로 호출됐는지를 확인한다.
        then(userService).should().login(argThat(dto ->
                dto != null
                        && dto.username().equals("tester")
                        && dto.password().equals("password123")
        ));
    }
}
