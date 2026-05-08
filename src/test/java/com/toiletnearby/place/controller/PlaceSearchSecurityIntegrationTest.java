package com.toiletnearby.place.controller;

import com.toiletnearby.place.dto.PlaceSearchResponseDto;
import com.toiletnearby.place.service.PlaceSearchService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// 실제 Security 필터 체인을 켠 상태로 장소 검색 공개 여부를 테스트한다.
@SpringBootTest
@AutoConfigureMockMvc
class PlaceSearchSecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PlaceSearchService placeSearchService;

    @Test
    @DisplayName("장소 검색 API는 JWT 없이 접근할 수 있다")
    void searchPlacesWithoutToken() throws Exception {
        // 장소 검색은 공개 조회 기능이므로 인증 없이 호출할 수 있다.
        given(placeSearchService.search("서울역", 1, 10, null, null, null))
                .willReturn(List.of(new PlaceSearchResponseDto(
                        "1",
                        "서울역",
                        "",
                        "",
                        "",
                        "",
                        126.9723,
                        37.5559,
                        "",
                        0
                )));

        mockMvc.perform(get("/api/places/search").param("query", "서울역"))
                .andExpect(status().isOk());
    }
}
