package com.bravos.steak.common.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;

@Configuration
public class S3Configuration {

    @Bean
    public AwsCredentialsProvider cloudflareCredentialsProvider() {
        return StaticCredentialsProvider
                .create(AwsBasicCredentials.
                        create(System.getProperty("CF_S3_ACCESS_KEY"),
                                System.getProperty("CF_S3_SECRET_KEY")));
    }

    @Bean
    public S3Client cloudflareS3Client() {
        return S3Client.builder()
                .region(Region.US_EAST_1)
                .endpointOverride(URI.create(System.getProperty("CF_S3_ENDPOINT")))
                .credentialsProvider(cloudflareCredentialsProvider())
                .build();
    }

}
