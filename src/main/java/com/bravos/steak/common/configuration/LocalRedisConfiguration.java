package com.bravos.steak.common.configuration;

import com.bravos.steak.common.service.encryption.KeyVaultService;
import io.lettuce.core.ClientOptions;
import io.lettuce.core.TimeoutOptions;
import io.lettuce.core.protocol.ProtocolVersion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.time.Duration;

@Slf4j
@Configuration
public class LocalRedisConfiguration {

    public final KeyVaultService keyVaultService;

    public LocalRedisConfiguration(KeyVaultService keyVaultService) {
        this.keyVaultService = keyVaultService;
    }

    @Bean
    @Profile("prod")
    public RedisConnectionFactory prodRedisConnectionFactory() {
        ClientOptions options = ClientOptions.builder()
                .protocolVersion(ProtocolVersion.RESP2)
                .pingBeforeActivateConnection(true)
                .autoReconnect(false)
                .timeoutOptions(TimeoutOptions.builder()
                        .fixedTimeout(Duration.ofMinutes(1))
                        .build())
                .build();
        LettuceClientConfiguration clientConfiguration = LettuceClientConfiguration.builder()
                .clientOptions(options)
                .build();
        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration();
        redisConfig.setHostName(System.getProperty("REDIS_HOST"));
        redisConfig.setPort(Integer.parseInt(System.getProperty("REDIS_PORT")));
        redisConfig.setPassword(keyVaultService.getSecretKey("redis-password"));
        log.info("Using product redis config");
        return new LettuceConnectionFactory(redisConfig,clientConfiguration);
    }

    @Bean
    @Profile("dev")
    public RedisConnectionFactory devRedisConnectionFactory() {
        ClientOptions options = ClientOptions.builder()
                .protocolVersion(ProtocolVersion.RESP2)
                .pingBeforeActivateConnection(true)
                .autoReconnect(false)
                .build();
        LettuceClientConfiguration clientConfiguration = LettuceClientConfiguration.builder()
                .clientOptions(options)
                .build();
        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration();
        redisConfig.setHostName(System.getProperty("REDIS_HOST"));
        redisConfig.setPort(Integer.parseInt(System.getProperty("REDIS_PORT")));
        redisConfig.setPassword(System.getProperty("REDIS_PASSWORD"));
        log.info("Using dev redis config");
        return new LettuceConnectionFactory(redisConfig,clientConfiguration);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(RedisSerializer.string());
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
        return redisTemplate;
    }

}
