package com.toiletnearby.memo.controller;

import com.toiletnearby.memo.domain.Memo;
import com.toiletnearby.memo.domain.vo.MemoContentsVo;
import com.toiletnearby.memo.domain.vo.MemoGoodVo;
import com.toiletnearby.memo.service.MemoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// MemoController의 요청/응답 형태를 테스트한다.
@WebMvcTest(MemoController.class)
@AutoConfigureMockMvc(addFilters = false)
class MemoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MemoService memoService;

    @Test
    @DisplayName("특정 화장실의 특정 메모를 조회한다")
    void getMemo() throws Exception {
        Memo memo = createMemo(1L, 1L, "tester", "깨끗합니다.", 5);

        given(memoService.getMemo(1L, 1L)).willReturn(List.of(memo));

        mockMvc.perform(get("/api/toilet/{TOILET_ID}/memo/{MEMO_ID}", 1L, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].toiletid").value(1))
                .andExpect(jsonPath("$[0].memoid").value(1))
                .andExpect(jsonPath("$[0].userid").value("tester"))
                .andExpect(jsonPath("$[0].contents").value("깨끗합니다."))
                .andExpect(jsonPath("$[0].good").value(5));

        then(memoService).should().getMemo(1L, 1L);
    }

    @Test
    @DisplayName("로그인한 사용자의 메모 목록을 조회한다")
    void getMyMemo() throws Exception {
        Memo memo = createMemo(1L, 1L, "tester", "내 메모", 5);

        given(memoService.getMyMemos("tester")).willReturn(List.of(memo));

        mockMvc.perform(get("/api/toilet/mymemo")
                        .principal(new TestingAuthenticationToken("tester", null)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userid").value("tester"))
                .andExpect(jsonPath("$[0].contents").value("내 메모"));

        then(memoService).should().getMyMemos("tester");
    }

    @Test
    @DisplayName("특정 화장실의 모든 메모를 조회한다")
    void getMemos() throws Exception {
        Memo memo = createMemo(1L, 1L, "tester", "화장실 메모", 5);

        given(memoService.getMemosByToiletId(1L)).willReturn(List.of(memo));

        mockMvc.perform(get("/api/toilet/{TOILET_ID}/memos", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].toiletid").value(1))
                .andExpect(jsonPath("$[0].contents").value("화장실 메모"));

        then(memoService).should().getMemosByToiletId(1L);
    }


    // 사용자가 요청 body를 조작했을 때, 서버가 작성자를 인증 정보 기준으로 저장하는지를 테스트
    @Test
    @DisplayName("메모 작성 시 요청 body의 userid가 아니라 실제 인증 사용자 id를 사용한다")
    void createMemo() throws Exception {
        // attacker가 로그인한 상태에서 요청 body에 tester라고 넣어 보낸 상황이다.
        // 서버는 요청 body의 userid를 믿으면 안 되고, 인증된 사용자 attacker를 사용해야 한다.
        String requestBody = """
            {
              "toiletid": 1,
              "userid": "tester",
              "contents": "깨끗합니다.",
              "good": 5
            }
            """;

        Memo savedMemo = createMemo(1L, 1L, "attacker", "깨끗합니다.", 5);

        given(memoService.createMemo(argThat(dto ->
                dto.toiletId().equals(1L)
                        && dto.userId().equals("attacker")
                        && dto.contents().equals("깨끗합니다.")
                        && dto.good() == 5
        ))).willReturn(savedMemo);

        mockMvc.perform(post("/api/toilet/memo")
                        .principal(new TestingAuthenticationToken("attacker", null))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                // tester가 아닌 인증 객체에 있던 attacker인지를 확인한다.
                .andExpect(jsonPath("$.userid").value("attacker"))
                .andExpect(jsonPath("$.contents").value("깨끗합니다."));

        // Service에 DTO를 넘길 때도, 요청 body의 tester가 아니라 attacker인지 확인한다.
        then(memoService).should().createMemo(argThat(dto ->
                dto.toiletId().equals(1L)
                        && dto.userId().equals("attacker")
                        && dto.contents().equals("깨끗합니다.")
                        && dto.good() == 5
        ));
    }


    @Test
    @DisplayName("본인 메모의 내용을 수정한다")
    void updateMemo() throws Exception {
        String requestBody = """
                {
                  "contents": "수정 후"
                }
                """;

        given(memoService.updateMemo(argThat(dto ->
                dto.toiletId().equals(1L)
                        && dto.memoId().equals(2L)
                        && dto.userId().equals("tester")
                        && dto.contents().equals("수정 후")
        ))).willReturn(10L);

        mockMvc.perform(put("/api/toilet/{TOILET_ID}/memo/{MEMO_ID}", 1L, 2L)
                        .principal(new TestingAuthenticationToken("tester", null))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().string("10"));

        then(memoService).should().updateMemo(argThat(dto ->
                dto.toiletId().equals(1L)
                        && dto.memoId().equals(2L)
                        && dto.userId().equals("tester")
                        && dto.contents().equals("수정 후")
        ));
    }

    @Test
    @DisplayName("메모 평점을 수정한다")
    void updateGood() throws Exception {
        String requestBody = """
                {
                  "good": 4
                }
                """;

        mockMvc.perform(put("/api/toilet/{TOILET_ID}/memo/{MEMO_ID}/good", 1L, 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());

        then(memoService).should().updateGood(argThat(dto ->
                dto.toiletId().equals(1L)
                        && dto.memoId().equals(2L)
                        && dto.good() == 4
        ));
    }

    @Test
    @DisplayName("본인 메모를 삭제한다")
    void deleteMemo() throws Exception {
        given(memoService.deleteMemo(1L, 2L, "tester")).willReturn(10L);

        mockMvc.perform(delete("/api/toilet/{TOILET_ID}/memo/{MEMO_ID}", 1L, 2L)
                        .principal(new TestingAuthenticationToken("tester", null)))
                .andExpect(status().isOk())
                .andExpect(content().string("10"));

        then(memoService).should().deleteMemo(1L, 2L, "tester");
    }

    private Memo createMemo(Long toiletId, Long memoId, String userId, String contents, int good) {
        return Memo.create(
                toiletId,
                memoId,
                userId,
                MemoContentsVo.from(contents),
                MemoGoodVo.from(good)
        );
    }
}
