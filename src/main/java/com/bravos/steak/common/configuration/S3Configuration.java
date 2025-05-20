package com.bravos.steak.common.configuration;

import com.bravos.steak.common.service.encryption.KeyVaultService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;

@Configuration
public class S3Configuration {

    @Bean
    public AwsCredentialsProvider cloudflareCredentialsProvider(KeyVaultService keyVaultService) {
        String cfs3AccessKey = keyVaultService.getSecretKey("cf-s3-access-key");
        String cfs3SecretKey = keyVaultService.getSecretKey("cf-s3-secret-key");
        return StaticCredentialsProvider
                .create(AwsBasicCredentials.
                        create(cfs3AccessKey,cfs3SecretKey));
    }

    @Bean("cloudflareS3Client")
    public S3Client cloudflareS3Client(@Qualifier("cloudflareCredentialsProvider") AwsCredentialsProvider awsCredentialsProvider) {
        return S3Client.builder()
                .region(Region.US_EAST_1)
                .endpointOverride(URI.create(System.getProperty("CF_S3_ENDPOINT")))
                .credentialsProvider(awsCredentialsProvider)
                .build();
    }

    @Bean("cloudflareS3Presigner")
    public S3Presigner cloudflareS3Presigner(@Qualifier("cloudflareCredentialsProvider") AwsCredentialsProvider awsCredentialsProvider) {
        return S3Presigner.builder()
                .region(Region.US_EAST_1)
                .endpointOverride(URI.create(System.getProperty("CF_S3_ENDPOINT")))
                .credentialsProvider(awsCredentialsProvider)
                .build();
    }

}
