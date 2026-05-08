package com.toiletnearby.toilet.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

// ToiletSearchGrid 좌표 보정 규칙을 테스트한다.
class ToiletSearchGridTest {

    @Test
    @DisplayName("비슷한 좌표는 같은 grid 중심값으로 보정된다")
    void snapSimilarCoordinates() {
        double first = ToiletSearchGrid.snap(37.555912);
        double second = ToiletSearchGrid.snap(37.555918);

        assertThat(first).isEqualTo(second);
    }

    @Test
    @DisplayName("좌표는 해당 grid의 중심값으로 보정된다")
    void snapToGridCenter() {
        double snapped = ToiletSearchGrid.snap(37.555912);

        assertThat(snapped).isEqualTo(37.5575);
    }
}
