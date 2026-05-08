package com.toiletnearby.place.controller;

import com.toiletnearby.place.dto.PlaceSearchResponseDto;
import com.toiletnearby.place.service.PlaceSearchService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// PlaceSearchController의 요청/응답 형태를 테스트한다.
@WebMvcTest(PlaceSearchController.class)
@AutoConfigureMockMvc(addFilters = false)
class PlaceSearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PlaceSearchService placeSearchService;

    @Test
    @DisplayName("Kakao 장소 검색을 요청한다")
    void searchPlaces() throws Exception {
        // given: Service가 반환할 검색 결과를 준비한다.
        PlaceSearchResponseDto response = new PlaceSearchResponseDto(
                "1",
                "서울역",
                "교통,수송 > 기차역",
                "",
                "서울 중구",
                "서울 중구 한강대로",
                126.9723,
                37.5559,
                "https://place.map.kakao.com/1",
                100
        );

        given(placeSearchService.search("서울역", 1, 10, 126.9723, 37.5559, 1000))
                .willReturn(List.of(response));

        // when & then: query, page, size, x, y, radius가 Service로 전달된다.
        mockMvc.perform(get("/api/places/search")
                        .param("query", "서울역")
                        .param("page", "1")
                        .param("size", "10")
                        .param("x", "126.9723")
                        .param("y", "37.5559")
                        .param("radius", "1000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("서울역"))
                .andExpect(jsonPath("$[0].x").value(126.9723))
                .andExpect(jsonPath("$[0].y").value(37.5559))
                .andExpect(jsonPath("$[0].distance").value(100));

        then(placeSearchService).should().search("서울역", 1, 10, 126.9723, 37.5559, 1000);
    }
}
