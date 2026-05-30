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
    private static final String MEDICINE_CACHE_PREFIX = "medicine:";
    private static final String INVENTORY_CACHE_PREFIX = "inventory:pharmacy:";

    private final StringRedisTemplate redisTemplate;
    private final long medicineTtlSeconds;
    private final long inventoryTtlSeconds;

    public InventoryCacheService(
            StringRedisTemplate redisTemplate,
            @Value("${inventory.cache.medicine-ttl-seconds:300}") long medicineTtlSeconds,
            @Value("${inventory.cache.inventory-ttl-seconds:120}") long inventoryTtlSeconds) {
        this.redisTemplate = redisTemplate;
        this.medicineTtlSeconds = medicineTtlSeconds;
        this.inventoryTtlSeconds = inventoryTtlSeconds;
    }

    public void cacheMedicine(UUID medicineId, String json) {
        String key = MEDICINE_CACHE_PREFIX + medicineId;
        redisTemplate.opsForValue().set(key, json, medicineTtlSeconds, TimeUnit.SECONDS);
        log.debug("Cached medicine: id={}", medicineId);
    }

    public String getCachedMedicine(UUID medicineId) {
        return redisTemplate.opsForValue().get(MEDICINE_CACHE_PREFIX + medicineId);
    }

    public void evictMedicineCache(UUID medicineId) {
        redisTemplate.delete(MEDICINE_CACHE_PREFIX + medicineId);
        log.debug("Evicted medicine cache: id={}", medicineId);
    }

    public void cacheInventory(UUID pharmacyId, String json) {
        String key = INVENTORY_CACHE_PREFIX + pharmacyId;
        redisTemplate.opsForValue().set(key, json, inventoryTtlSeconds, TimeUnit.SECONDS);
        log.debug("Cached inventory: pharmacyId={}", pharmacyId);
    }

    public String getCachedInventory(UUID pharmacyId) {
        return redisTemplate.opsForValue().get(INVENTORY_CACHE_PREFIX + pharmacyId);
    }

    public void evictInventoryCache(UUID pharmacyId) {
        redisTemplate.delete(INVENTORY_CACHE_PREFIX + pharmacyId);
        log.debug("Evicted inventory cache: pharmacyId={}", pharmacyId);
    }
}