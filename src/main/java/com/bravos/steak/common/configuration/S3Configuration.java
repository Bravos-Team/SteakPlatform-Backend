package com.bravos.steak.common.configuration;

import com.bravos.steak.common.model.GameS3Config;
import com.bravos.steak.common.model.ImageS3Config;
import com.bravos.steak.common.service.encryption.KeyVaultService;
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
    public ImageS3Config prodImageS3Config(KeyVaultService keyVaultService) {
        String s3AccessKey = keyVaultService.getSecretKey(System.getProperty("CF_S3_ACCESS_KEY"));
        String s3SecretKey = keyVaultService.getSecretKey(System.getProperty("CF_S3_SECRET_KEY"));
        String s3Endpoint = keyVaultService.getSecretKey(System.getProperty("CF_S3_ENDPOINT"));
        String bucketName = keyVaultService.getSecretKey(System.getProperty("CF_S3_BUCKET_NAME"));
        return new ImageS3Config(s3AccessKey, s3SecretKey,s3Endpoint,bucketName);
    }

    @Bean
    @Profile("dev")
    public ImageS3Config devImageS3Config() {
        String s3AccessKey = System.getProperty("CF_S3_ACCESS_KEY");
        String s3SecretKey = System.getProperty("CF_S3_SECRET_KEY");
        String s3Endpoint = System.getProperty("CF_S3_ENDPOINT");
        String bucketName = System.getProperty("CF_S3_BUCKET_NAME");
        return new ImageS3Config(s3AccessKey, s3SecretKey,s3Endpoint,bucketName);
    }

    @Bean
    @Profile("prod")
    public GameS3Config prodGameS3Config(KeyVaultService keyVaultService) {
        String s3AccessKey = keyVaultService.getSecretKey(System.getProperty("GAME_S3_ACCESS_KEY"));
        String s3SecretKey = keyVaultService.getSecretKey(System.getProperty("GAME_S3_SECRET_KEY"));
        String s3Endpoint = keyVaultService.getSecretKey(System.getProperty("GAME_S3_ENDPOINT"));
        String bucketName = keyVaultService.getSecretKey(System.getProperty("GAME_S3_BUCKET_NAME"));
        String region = keyVaultService.getSecretKey(System.getProperty("GAME_S3_REGION"));
        return new GameS3Config(s3AccessKey, s3SecretKey, s3Endpoint, bucketName, region);
    }

    @Bean
    @Profile("dev")
    public GameS3Config devGameS3Config() {
        String s3AccessKey = System.getProperty("GAME_S3_ACCESS_KEY");
        String s3SecretKey = System.getProperty("GAME_S3_SECRET_KEY");
        String s3Endpoint = System.getProperty("GAME_S3_ENDPOINT");
        String bucketName = System.getProperty("GAME_S3_BUCKET_NAME");
        String region = System.getProperty("GAME_S3_REGION");
        return new GameS3Config(s3AccessKey, s3SecretKey, s3Endpoint, bucketName, region);
    }

    @Bean
    public AwsCredentialsProvider cloudflareCredentialsProvider(ImageS3Config imageS3Config) {
        return StaticCredentialsProvider
                .create(AwsBasicCredentials.
                        create(imageS3Config.getAccessKey(), imageS3Config.getSecretKey()));
    }

    @Bean
    public AwsCredentialsProvider gameS3CredentialsProvider(GameS3Config gameS3Config) {
        return StaticCredentialsProvider
                .create(AwsBasicCredentials.
                        create(gameS3Config.getAccessKey(), gameS3Config.getSecretKey()));
    }

    @Bean
    public S3Client cloudflareS3Client(AwsCredentialsProvider cloudflareCredentialsProvider, ImageS3Config imageS3Config) {
        return S3Client.builder()
                .region(Region.US_EAST_1)
                .endpointOverride(URI.create(imageS3Config.getS3Endpoint()))
                .credentialsProvider(cloudflareCredentialsProvider)
                .build();
    }

    @Bean
    public S3Client gameS3Client(AwsCredentialsProvider gameS3CredentialsProvider, GameS3Config gameS3Config) {
        return S3Client.builder()
                .region(Region.of(gameS3Config.getRegion()))
                .credentialsProvider(gameS3CredentialsProvider)
                .build();
    }

    @Bean
    public S3Presigner cloudflareS3Presigner(AwsCredentialsProvider cloudflareCredentialsProvider, ImageS3Config imageS3Config) {
        return S3Presigner.builder()
                .region(Region.US_EAST_1)
                .endpointOverride(URI.create(imageS3Config.getS3Endpoint()))
                .credentialsProvider(cloudflareCredentialsProvider)
                .build();
    }

    @Bean
    public S3Presigner gameS3Presigner(AwsCredentialsProvider gameS3CredentialsProvider, GameS3Config gameS3Config) {
        return S3Presigner.builder()
                .region(Region.of(gameS3Config.getRegion()))
                .credentialsProvider(gameS3CredentialsProvider)
                .build();
    }

}
