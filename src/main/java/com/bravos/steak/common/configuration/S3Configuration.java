package com.bravos.steak.common.configuration;

import com.bravos.steak.common.service.encryption.KeyVaultService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
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
    @Profile("prod")
    public AwsCredentialsProvider cloudflareCredentialsProvider(KeyVaultService keyVaultService) {
        String cfs3AccessKey = keyVaultService.getSecretKey(System.getProperty("CF_S3_ACCESS_KEY"));
        String cfs3SecretKey = keyVaultService.getSecretKey(System.getProperty("CF_S3_SECRET_KEY"));
        return StaticCredentialsProvider
                .create(AwsBasicCredentials.
                        create(cfs3AccessKey,cfs3SecretKey));
    }

    @Bean
    @Profile("prod")
    public S3Client cloudflareS3Client(AwsCredentialsProvider awsCredentialsProvider,
                                       KeyVaultService keyVaultService) {
        return S3Client.builder()
                .region(Region.US_EAST_1)
                .endpointOverride(URI.create(keyVaultService.getSecretKey(System.getProperty("CF_S3_ENDPOINT"))))
                .credentialsProvider(awsCredentialsProvider)
                .build();
    }

    @Bean
    @Profile("prod")
    public S3Presigner cloudflareS3Presigner(AwsCredentialsProvider awsCredentialsProvider,
                                             KeyVaultService keyVaultService) {
        return S3Presigner.builder()
                .region(Region.US_EAST_1)
                .endpointOverride(URI.create(keyVaultService.getSecretKey(System.getProperty("CF_S3_ENDPOINT"))))
                .credentialsProvider(awsCredentialsProvider)
                .build();
    }

    @Bean
    @Profile("dev")
    public AwsCredentialsProvider devCloudflareCredentialsProvider() {
        String cfs3AccessKey = System.getProperty("CF_S3_ACCESS_KEY");
        String cfs3SecretKey = System.getProperty("CF_S3_SECRET_KEY");
        return StaticCredentialsProvider
                .create(AwsBasicCredentials.
                        create(cfs3AccessKey,cfs3SecretKey));
    }

    @Bean
    @Profile("dev")
    public S3Client devCloudflareS3Client(@Qualifier("devCloudflareCredentialsProvider") AwsCredentialsProvider awsCredentialsProvider) {
        return S3Client.builder()
                .region(Region.US_EAST_1)
                .endpointOverride(URI.create(System.getProperty("CF_S3_ENDPOINT")))
                .credentialsProvider(awsCredentialsProvider)
                .build();
    }

    @Bean
    @Profile("dev")
    public S3Presigner devCloudflareS3Presigner(@Qualifier("devCloudflareCredentialsProvider") AwsCredentialsProvider awsCredentialsProvider) {
        return S3Presigner.builder()
                .region(Region.US_EAST_1)
                .endpointOverride(URI.create(System.getProperty("CF_S3_ENDPOINT")))
                .credentialsProvider(awsCredentialsProvider)
                .build();
    }

}
