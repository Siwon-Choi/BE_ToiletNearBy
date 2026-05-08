package com.toiletnearby.toilet.service;

// 근처 검색 캐시 키를 안정적으로 만들기 위해 좌표를 격자 중심값으로 보정한다.
public final class ToiletSearchGrid {

    private static final double GRID_DEGREES = 0.005;

    // grid 중심과 실제 사용자 위치 사이의 오차를 보완하기 위해 후보 조회 반경에 더한다.
    static final double GRID_DIAGONAL_METERS = 800.0;

    private ToiletSearchGrid() {
    }

    // 좌표를 해당 격자의 중심값으로 보정한다.
    public static double snap(double coordinate) {
        // 좌표 / 격자 크기 = 좌표 번호, 좌표 번호 * 격자 크기 = 좌표 시작, 좌표 시작 + 격자 크기 절반 = 좌표 격자의 중앙점
        return Math.floor(coordinate / GRID_DEGREES) * GRID_DEGREES + (GRID_DEGREES / 2);
    }
}
