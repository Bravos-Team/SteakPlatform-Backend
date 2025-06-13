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
import org.springframework.data.mongodb.core.convert.DbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

@Slf4j
@Configuration
public class MongoConfiguration {

    @Bean
    @Profile("dev")
    public MongoClient devMongoClient() {
        try {
            ConnectionString connectionString = new ConnectionString(System.getProperty("MONGO_CONNECTION_STRING"));
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
            ConnectionString connectionString = new ConnectionString(
                    keyVaultService.getSecretKey(System.getProperty("MONGO_CONNECTION_STRING")));
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
    public DbRefResolver dbRefResolver(MongoDatabaseFactory mongoDatabaseFactory) {
        return new DefaultDbRefResolver(mongoDatabaseFactory);
    }

    @Bean
    public MappingMongoConverter mappingMongoConverter(DbRefResolver dbRefResolver) {
        return new MappingMongoConverter(dbRefResolver,new MongoMappingContext());
    }

    @Bean
    public MongoTemplate mongoTemplate(MongoDatabaseFactory mongoDatabaseFactory, MappingMongoConverter mappingMongoConverter) {
        mappingMongoConverter.setTypeMapper(new DefaultMongoTypeMapper(null));
        return new MongoTemplate(mongoDatabaseFactory,mappingMongoConverter);
    }

}
