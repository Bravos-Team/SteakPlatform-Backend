package com.bravos.steak.common.configuration;

import com.bravos.steak.common.service.snowflake.SnowflakeGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SnowflakeGeneratorConfiguration {

    @Bean("accountIdGenerator")
    public SnowflakeGenerator accountIdGenerator() {
        return new SnowflakeGenerator(1);
    }

    @Bean("gameIdGenerator")
    public SnowflakeGenerator gameIdGenerator() {
        return new SnowflakeGenerator(2);
    }

    @Bean("generalIdGenerator")
    public SnowflakeGenerator generalIdGenerator() {
        return new SnowflakeGenerator(3);
    }

}
