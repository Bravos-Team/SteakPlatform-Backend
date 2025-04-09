package com.bravos.steak.common.configuration;

import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.redisson.connection.ConnectionListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.net.InetSocketAddress;

@Slf4j
@Configuration
public class RedisConfiguration {

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory();
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        redisTemplate.setKeySerializer(RedisSerializer.string());
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
        return redisTemplate;
    }

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();

        config.setCodec(new JsonJacksonCodec());
        config.useSingleServer()
                .setAddress("redis://127.0.0.1:6379")
                .setDatabase(0)
                .setConnectionMinimumIdleSize(6)
                .setConnectTimeout(10)
                .setConnectionPoolSize(20);
        config.setConnectionListener(new ConnectionListener() {
            @Override
            public void onConnect(InetSocketAddress inetSocketAddress) {
                log.info("Connected to redis {}",inetSocketAddress.getAddress());
            }

            @Override
            public void onDisconnect(InetSocketAddress inetSocketAddress) {
                log.error("Disconnected from redis {}",inetSocketAddress.getAddress());
            }
        });
        config.setLazyInitialization(false);
        config.setNettyThreads(8);

        return Redisson.create(config);
    }

}
