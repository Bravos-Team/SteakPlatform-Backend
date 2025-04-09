package com.bravos.steak.account.controller;

import com.bravos.steak.account.model.request.RegistrationRequest;
import com.bravos.steak.common.service.redis.RedisService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/test")
public class TestController {

    private final RedisService redisService;

    public TestController(RedisService redisService) {
        this.redisService = redisService;
    }

    @GetMapping
    public void test() {
        redisService.multiGet(List.of("abc","abc","abc"), RegistrationRequest.class).forEach(req -> {
            System.out.println(req.getUsername());
            System.out.println(req.getEmail());
            System.out.println(req.getPassword());
        });
    }

}
