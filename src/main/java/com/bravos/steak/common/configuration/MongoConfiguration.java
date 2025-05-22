package com.bravos.steak.common.configuration;

import com.bravos.steak.common.service.encryption.KeyVaultService;
import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

@Slf4j
@Configuration
public class MongoConfiguration {

    @Bean
    @Profile("dev")
    public MongoClient devMongoClient() {
        try {
            ConnectionString connectionString = new ConnectionString(System.getProperty("MONGO_CONN_STRING"));
            log.info("Using dev mongodb");
            return MongoClients.create(connectionString);
        } catch (Exception e) {
            throw new RuntimeException("Cannot connect to mongodb", e);
        }
    }

    @Bean
    @Profile("prod")
    public MongoClient prodMongoClient(KeyVaultService keyVaultService) {
        try {
            ConnectionString connectionString = new ConnectionString(keyVaultService.getSecretKey("mongo-connection-string"));
            connectionString.getHosts().forEach(host -> log.info("Using mongodb host: {}",host));
            log.info("Using prod mongodb in {}", connectionString.getHosts().getFirst());
            return MongoClients.create(connectionString);
        } catch (Exception e) {
            throw new RuntimeException("Cannot connect to mongodb", e);
        }
    }

    @Bean
    public MongoDatabaseFactory mongoDatabaseFactory(MongoClient mongoClient) {
        return new SimpleMongoClientDatabaseFactory(mongoClient, "steaknosql");
    }

    @Bean
    public MongoTemplate mongoTemplate(MongoDatabaseFactory mongoDatabaseFactory) {
        return new MongoTemplate(mongoDatabaseFactory);
    }

}
