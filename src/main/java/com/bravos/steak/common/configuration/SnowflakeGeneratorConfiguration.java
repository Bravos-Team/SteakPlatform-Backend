package com.bravos.steak.common.configuration;

import com.bravos.steak.common.service.snowflake.SnowflakeGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SnowflakeGeneratorConfiguration {

    @Bean
    public SnowflakeGenerator snowflakeGenerator() {
        return new SnowflakeGenerator(Long.parseLong(System.getProperty("MACHINE_ID")));
    }

}
