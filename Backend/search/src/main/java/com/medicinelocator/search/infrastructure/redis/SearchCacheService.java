package com.medicinelocator.search.infrastructure.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class SearchCacheService {

    private static final Logger log = LoggerFactory.getLogger(SearchCacheService.class);

    private static final String MEDICINE_SEARCH_PREFIX = "search:medicine:";
    private static final String NEARBY_SEARCH_PREFIX   = "search:nearby:";

    private final StringRedisTemplate redisTemplate;
    private final long medicineSearchTtlSeconds;
    private final long nearbySearchTtlSeconds;

    public SearchCacheService(
            StringRedisTemplate redisTemplate,
            @Value("${search.cache.medicine-search-ttl-seconds:60}") long medicineSearchTtlSeconds,
            @Value("${search.cache.nearby-search-ttl-seconds:30}") long nearbySearchTtlSeconds) {
        this.redisTemplate = redisTemplate;
        this.medicineSearchTtlSeconds = medicineSearchTtlSeconds;
        this.nearbySearchTtlSeconds = nearbySearchTtlSeconds;
    }

    // ─── Medicine Search Cache ────────────────────────────────────────────────

    public void cacheMedicineSearch(String cacheKey, String json) {
        String key = MEDICINE_SEARCH_PREFIX + cacheKey;
        redisTemplate.opsForValue().set(key, json, medicineSearchTtlSeconds, TimeUnit.SECONDS);
        log.debug("Cached medicine search: key={}", cacheKey);
    }

    public String getCachedMedicineSearch(String cacheKey) {
        return redisTemplate.opsForValue().get(MEDICINE_SEARCH_PREFIX + cacheKey);
    }

    public void evictMedicineSearch(String cacheKey) {
        redisTemplate.delete(MEDICINE_SEARCH_PREFIX + cacheKey);
        log.debug("Evicted medicine search cache: key={}", cacheKey);
    }

    // ─── Nearby Search Cache ──────────────────────────────────────────────────

    public void cacheNearbySearch(String cacheKey, String json) {
        String key = NEARBY_SEARCH_PREFIX + cacheKey;
        redisTemplate.opsForValue().set(key, json, nearbySearchTtlSeconds, TimeUnit.SECONDS);
        log.debug("Cached nearby search: key={}", cacheKey);
    }

    public String getCachedNearbySearch(String cacheKey) {
        return redisTemplate.opsForValue().get(NEARBY_SEARCH_PREFIX + cacheKey);
    }

    public void evictNearbySearch(String cacheKey) {
        redisTemplate.delete(NEARBY_SEARCH_PREFIX + cacheKey);
        log.debug("Evicted nearby search cache: key={}", cacheKey);
    }

    // ─── Cache Key Builder ────────────────────────────────────────────────────

    /**
     * Builds a stable cache key from search parameters.
     * Normalises the search term to lower-case and trims whitespace.
     */
    public String buildMedicineCacheKey(String searchTerm, Double lat, Double lng,
                                        Double radiusKm, Boolean rx, String category,
                                        int page, int size) {
        return String.format("%s:%.4f:%.4f:%.1f:%s:%s:%d:%d",
                searchTerm.toLowerCase().trim(),
                lat != null ? lat : 0.0,
                lng != null ? lng : 0.0,
                radiusKm != null ? radiusKm : 0.0,
                rx != null ? rx : "any",
                category != null ? category.toLowerCase() : "any",
                page, size
        );
    }

    public String buildNearbyCacheKey(double lat, double lng, double radiusKm,
                                      int page, int size) {
        return String.format("%.4f:%.4f:%.1f:%d:%d", lat, lng, radiusKm, page, size);
    }
}