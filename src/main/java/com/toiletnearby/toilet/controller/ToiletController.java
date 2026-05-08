package com.toiletnearby.toilet.controller;

import com.toiletnearby.toilet.dto.NearbyToiletResponseDto;
import com.toiletnearby.toilet.dto.ToiletResponseDto;
import com.toiletnearby.toilet.service.ToiletService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// 화장실 조회 API를 담당한다.
@RestController
@RequiredArgsConstructor
public class ToiletController {

    private final ToiletService toiletService;

    // 전체 화장실 목록을 조회한다.
    @GetMapping("/api/toilets")
    public List<ToiletResponseDto> getToilets() {
        return toiletService.getToilets().stream()
                .map(ToiletResponseDto::from)
                .toList();
    }

    // 사용자 좌표와 반경을 기준으로 근처 화장실을 조회한다.
    @GetMapping("/api/toiletsNearBy/{user_x}/{user_y}/{R}")
    public List<NearbyToiletResponseDto> getToiletsNearBy(
            @PathVariable("R") int rangeMeters,
            @PathVariable("user_x") double userX,
            @PathVariable("user_y") double userY
    ) {
        return toiletService.getToiletsNearBy(rangeMeters, userX, userY);
    }

    // id로 화장실 하나를 조회한다.
    @GetMapping("/api/toilet/{TOILET_ID}")
    public ToiletResponseDto getToilet(@PathVariable("TOILET_ID") Long toiletId) {
        return ToiletResponseDto.from(toiletService.getToilet(toiletId));
    }
}
