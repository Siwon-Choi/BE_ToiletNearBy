package com.toiletnearby.global.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import static org.assertj.core.api.Assertions.assertThat;

// CacheConfig 설정을 테스트한다.
class CacheConfigTest {

    @Test
    @DisplayName("nearbyToiletCandidates 캐시를 생성한다")
    void createNearbyToiletCandidatesCache() {
        CacheConfig cacheConfig = new CacheConfig();

        CacheManager cacheManager = cacheConfig.cacheManager();

        Cache cache = cacheManager.getCache(CacheConfig.NEARBY_TOILET_CANDIDATES);

        assertThat(cache).isNotNull();
    }
}
