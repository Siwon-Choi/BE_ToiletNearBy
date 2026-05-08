package com.toiletnearby.toilet.controller;

import com.toiletnearby.toilet.domain.Toilet;
import com.toiletnearby.toilet.dto.NearbyToiletResponseDto;
import com.toiletnearby.toilet.service.ToiletService;
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

// ToiletController의 요청/응답 형태를 테스트한다.
@WebMvcTest(ToiletController.class)
@AutoConfigureMockMvc(addFilters = false)
class ToiletControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ToiletService toiletService;

    @Test
    @DisplayName("전체 화장실 목록을 조회한다")
    void getToilets() throws Exception {
        Toilet toilet = Toilet.create("서울역 화장실", 126.9723, 37.5559);

        given(toiletService.getToilets()).willReturn(List.of(toilet));

        mockMvc.perform(get("/api/toilets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("서울역 화장실"))
                .andExpect(jsonPath("$[0].x_wgs").value(126.9723))
                .andExpect(jsonPath("$[0].y_wgs").value(37.5559))
                .andExpect(jsonPath("$[0].grade").value(0.0));

        then(toiletService).should().getToilets();
    }

    @Test
    @DisplayName("근처 화장실 목록을 조회한다")
    void getToiletsNearBy() throws Exception {
        Toilet toilet = Toilet.create("서울역 화장실", 126.9723, 37.5559);
        NearbyToiletResponseDto response = NearbyToiletResponseDto.from(toilet, 0);

        given(toiletService.getToiletsNearBy(2000, 126.9723, 37.5559))
                .willReturn(List.of(response));

        mockMvc.perform(get("/api/toiletsNearBy/{user_x}/{user_y}/{R}", 126.9723, 37.5559, 2000))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("서울역 화장실"))
                .andExpect(jsonPath("$[0].distanceMeters").value(0));

        then(toiletService).should().getToiletsNearBy(2000, 126.9723, 37.5559);
    }

    @Test
    @DisplayName("id로 화장실 하나를 조회한다")
    void getToilet() throws Exception {
        Toilet toilet = Toilet.create("서울역 화장실", 126.9723, 37.5559);

        given(toiletService.getToilet(1L)).willReturn(toilet);

        mockMvc.perform(get("/api/toilet/{TOILET_ID}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("서울역 화장실"))
                .andExpect(jsonPath("$.x_wgs").value(126.9723))
                .andExpect(jsonPath("$.y_wgs").value(37.5559));

        then(toiletService).should().getToilet(1L);
    }
}
