package com.bravos.steak.common.configuration;

import com.bravos.steak.common.service.encryption.KeyVaultService;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

@Slf4j
@Configuration
public class DatabaseConfiguration {

    @Bean
    @Profile("dev")
    public DataSource devDataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName(System.getProperty("DATABASE_DRIVER"));
        dataSource.setJdbcUrl(System.getProperty("DATABASE_JDBC_URL"));
        dataSource.setUsername(System.getProperty("DATABASE_USER"));
        dataSource.setPassword(System.getProperty("DATABASE_PASSWORD"));
        log.info("Using dev datasource");
        return dataSource;
    }

    @Bean
    @Profile("prod")
    public DataSource prodDataSource(KeyVaultService keyVaultService) {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName(System.getProperty("DATABASE_DRIVER"));
        dataSource.setJdbcUrl(keyVaultService.getSecretKey("database-jdbc-url"));
        dataSource.setUsername(keyVaultService.getSecretKey("database-username"));
        dataSource.setPassword(keyVaultService.getSecretKey("database-password"));
        log.info("Using prod datasource");
        return dataSource;
    }

}
