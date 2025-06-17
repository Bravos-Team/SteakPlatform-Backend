package com.bravos.steak.common.configuration;

import com.bravos.steak.common.service.encryption.KeyVaultService;
import org.springframework.beans.factory.annotation.Value;
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
    public AwsCredentialsProvider cloudflareCredentialsProvider(KeyVaultService keyVaultService,
                                                                @Value("${spring.profiles.active:dev}") String profile) {
        if ("prod".equalsIgnoreCase(profile)) {
            return prodCloudflareCredentialsProvider(keyVaultService);
        } else {
            return devCloudflareCredentialsProvider();
        }
    }

    private AwsCredentialsProvider prodCloudflareCredentialsProvider(KeyVaultService keyVaultService) {
        String cfs3AccessKey = keyVaultService.getSecretKey(System.getProperty("CF_S3_ACCESS_KEY"));
        String cfs3SecretKey = keyVaultService.getSecretKey(System.getProperty("CF_S3_SECRET_KEY"));
        return StaticCredentialsProvider
                .create(AwsBasicCredentials.
                        create(cfs3AccessKey, cfs3SecretKey));
    }

    private AwsCredentialsProvider devCloudflareCredentialsProvider() {
        String cfs3AccessKey = System.getProperty("CF_S3_ACCESS_KEY");
        String cfs3SecretKey = System.getProperty("CF_S3_SECRET_KEY");
        return StaticCredentialsProvider
                .create(AwsBasicCredentials.
                        create(cfs3AccessKey, cfs3SecretKey));
    }

    @Bean
    public S3Client cloudflareS3Client(AwsCredentialsProvider awsCredentialsProvider,
                                       KeyVaultService keyVaultService,
                                       @Value("${spring.profiles.active:dev}") String profile) {

        if ("prod".equalsIgnoreCase(profile)) {
            return prodCloudflareS3Client(awsCredentialsProvider, keyVaultService);
        } else {
            return devCloudflareS3Client(awsCredentialsProvider);
        }

    }

    private S3Client prodCloudflareS3Client(AwsCredentialsProvider awsCredentialsProvider,
                                       KeyVaultService keyVaultService) {
        return S3Client.builder()
                .region(Region.US_EAST_1)
                .endpointOverride(URI.create(keyVaultService.getSecretKey(System.getProperty("CF_S3_ENDPOINT"))))
                .credentialsProvider(awsCredentialsProvider)
                .build();
    }

    private S3Client devCloudflareS3Client(AwsCredentialsProvider awsCredentialsProvider) {
        return S3Client.builder()
                .region(Region.US_EAST_1)
                .endpointOverride(URI.create(System.getProperty("CF_S3_ENDPOINT")))
                .credentialsProvider(awsCredentialsProvider)
                .build();
    }

    @Bean
    public S3Presigner cloudflareS3Presigner(AwsCredentialsProvider awsCredentialsProvider,
                                             KeyVaultService keyVaultService,
                                             @Value("${spring.profiles.active:dev}") String profile) {

        if ("prod".equalsIgnoreCase(profile)) {
            return prodCloudflareS3Presigner(awsCredentialsProvider, keyVaultService);
        } else {
            return devCloudflareS3Presigner(awsCredentialsProvider);
        }

    }

    private S3Presigner prodCloudflareS3Presigner(AwsCredentialsProvider awsCredentialsProvider,
                                             KeyVaultService keyVaultService) {
        return S3Presigner.builder()
                .region(Region.US_EAST_1)
                .endpointOverride(URI.create(keyVaultService.getSecretKey(System.getProperty("CF_S3_ENDPOINT"))))
                .credentialsProvider(awsCredentialsProvider)
                .build();
    }

    private S3Presigner devCloudflareS3Presigner(AwsCredentialsProvider awsCredentialsProvider) {
        return S3Presigner.builder()
                .region(Region.US_EAST_1)
                .endpointOverride(URI.create(System.getProperty("CF_S3_ENDPOINT")))
                .credentialsProvider(awsCredentialsProvider)
                .build();
    }

    @Bean
    public String cloudflareS3BucketName(@Value("${spring.profiles.active:dev}") String profile, KeyVaultService keyVaultService) {
        if ("prod".equalsIgnoreCase(profile)) {
            return keyVaultService.getSecretKey(System.getProperty("CF_S3_BUCKET_NAME"));
        } else {
            return System.getProperty("CF_S3_BUCKET_NAME");
        }
    }

}
