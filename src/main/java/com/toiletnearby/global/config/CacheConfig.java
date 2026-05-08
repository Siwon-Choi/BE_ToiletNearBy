package com.toiletnearby.global.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

// 캐시 설정을 담당한다.
// Caffeine은 로컬 메모리 기반 캐시 라이브러리다.
@Configuration
@EnableCaching
public class CacheConfig {

    public static final String NEARBY_TOILET_CANDIDATES = "nearbyToiletCandidates";

    // nearbyToiletCandidates 캐시에 최대 개수와 만료 시간을 설정한다.
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(NEARBY_TOILET_CANDIDATES);

        cacheManager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(5000)
                .expireAfterWrite(Duration.ofMinutes(5)));

        return cacheManager;
    }
}
