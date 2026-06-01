package com.medicinelocator.inventory.infrastructure.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class InventoryCacheService {

    private static final Logger log = LoggerFactory.getLogger(InventoryCacheService.class);

    private static final String PHARMACY_INVENTORY_PREFIX = "pharmacy:inventory:";
    private static final String MEDICINE_SEARCH_PREFIX    = "search:medicines:";

    private final StringRedisTemplate redisTemplate;
    private final long pharmacyInventoryTtlSeconds;
    private final long searchTtlSeconds;

    public InventoryCacheService(
            StringRedisTemplate redisTemplate,
            @Value("${inventory.cache.pharmacy-medicines-ttl-seconds:120}") long pharmacyInventoryTtlSeconds,
            @Value("${inventory.cache.search-ttl-seconds:60}") long searchTtlSeconds) {
        this.redisTemplate = redisTemplate;
        this.pharmacyInventoryTtlSeconds = pharmacyInventoryTtlSeconds;
        this.searchTtlSeconds = searchTtlSeconds;
    }

    public void cachePharmacyInventory(UUID pharmacyId, String json) {
        String key = PHARMACY_INVENTORY_PREFIX + pharmacyId;
        redisTemplate.opsForValue().set(key, json, pharmacyInventoryTtlSeconds, TimeUnit.SECONDS);
        log.debug("Cached pharmacy inventory: pharmacyId={}", pharmacyId);
    }

    public String getCachedPharmacyInventory(UUID pharmacyId) {
        return redisTemplate.opsForValue().get(PHARMACY_INVENTORY_PREFIX + pharmacyId);
    }

    public void evictPharmacyInventoryCache(UUID pharmacyId) {
        Boolean deleted = redisTemplate.delete(PHARMACY_INVENTORY_PREFIX + pharmacyId);
        log.debug("Evicted pharmacy inventory cache: pharmacyId={} deleted={}", pharmacyId, deleted);
    }

    public void cacheSearchResult(String cacheKey, String json) {
        String key = MEDICINE_SEARCH_PREFIX + cacheKey;
        redisTemplate.opsForValue().set(key, json, searchTtlSeconds, TimeUnit.SECONDS);
        log.debug("Cached search result: key={}", cacheKey);
    }

    public String getCachedSearchResult(String cacheKey) {
        return redisTemplate.opsForValue().get(MEDICINE_SEARCH_PREFIX + cacheKey);
    }

    public void evictAllSearchCaches() {
        var keys = redisTemplate.keys(MEDICINE_SEARCH_PREFIX + "*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
            log.debug("Evicted all search caches: count={}", keys.size());
        }
    }
}