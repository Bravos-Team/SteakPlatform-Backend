package com.bravos.steak.account.controller;

import com.bravos.steak.common.service.encryption.AesEncryptionService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/test")
public class TestController {

    private final RedisTemplate<String, Object> redisTemplate;
    private final AesEncryptionService aesEncryptionService;

    public TestController(RedisTemplate<String, Object> redisTemplate, AesEncryptionService aesEncryptionService) {
        this.redisTemplate = redisTemplate;
        this.aesEncryptionService = aesEncryptionService;
    }

    @GetMapping
    public ResponseEntity<?> test() {
        return ResponseEntity.badRequest().build();
    }

}
