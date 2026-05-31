package com.medicinelocator.auth.infrastructure.debug;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/debug/redis")
public class RedisDebugController {

    private final StringRedisTemplate redisTemplate;

    public RedisDebugController(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @GetMapping("/keys")
    public Set<String> getAllKeys() {
        return redisTemplate.keys("*");
    }

    @GetMapping("/get")
    public String getValue(@RequestParam String key) {
        return redisTemplate.opsForValue().get(key);
    }
}