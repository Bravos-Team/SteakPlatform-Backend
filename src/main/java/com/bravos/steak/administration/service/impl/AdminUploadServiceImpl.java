package com.bravos.steak.administration.service.impl;

import com.bravos.steak.administration.service.AdminUploadService;
import com.bravos.steak.common.model.ImageS3Config;
import com.bravos.steak.common.service.snowflake.SnowflakeGenerator;
import com.bravos.steak.common.service.storage.impl.CloudflareS3Service;
import com.bravos.steak.dev.model.request.DeleteImageRequest;
import com.bravos.steak.dev.model.request.ImageUploadPresignedRequest;
import com.bravos.steak.dev.model.response.PresignedUrlResponse;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;

import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class AdminUploadServiceImpl implements AdminUploadService {

    private static final int PRESIGNED_URL_EXPIRY_HOURS = 1;
    private static final int SHUTDOWN_TIMEOUT_SECONDS = 60;

    private final SnowflakeGenerator snowflakeGenerator;
    private final CloudflareS3Service cloudflareS3Service;
    private final ImageS3Config imageS3Config;
    private final ExecutorService executorService;
    private final S3Client cloudflareS3Client;

    public AdminUploadServiceImpl(SnowflakeGenerator snowflakeGenerator, CloudflareS3Service cloudflareS3Service,
                                  ImageS3Config imageS3Config, S3Client cloudflareS3Client) {
        this.snowflakeGenerator = snowflakeGenerator;
        this.cloudflareS3Service = cloudflareS3Service;
        this.imageS3Config = imageS3Config;
        this.executorService = Executors.newVirtualThreadPerTaskExecutor();
        this.cloudflareS3Client = cloudflareS3Client;
    }

    @Override
    public PresignedUrlResponse createAdminPresignedUploadUrl(ImageUploadPresignedRequest imageUploadPresignedRequest) {
        if (imageUploadPresignedRequest == null || imageUploadPresignedRequest.getFileName() == null) {
            throw new IllegalArgumentException("Image upload request and filename cannot be null");
        }

        String fileName = imageUploadPresignedRequest.getFileName();
        if (!fileName.contains(".")) {
            throw new IllegalArgumentException("Filename must contain an extension");
        }

        String extension = fileName.substring(fileName.lastIndexOf("."));
        String objectName = snowflakeGenerator.generateId() + extension;

        try {
            String signedUrl = cloudflareS3Service.generateS3PutSignedUrl(
                    imageS3Config.getBucketName(),
                    objectName,
                    Duration.ofHours(PRESIGNED_URL_EXPIRY_HOURS));

            return PresignedUrlResponse.builder()
                    .fileName(fileName)
                    .signedUrl(signedUrl)
                    .cdnFileName(objectName)
                    .build();
        } catch (Exception e) {
            log.error("Error creating presigned url for file: {}", fileName, e);
            throw new RuntimeException("Failed to create presigned upload URL", e);
        }
    }

    @Override
    public PresignedUrlResponse[] createAdminPresignedUploadUrls(ImageUploadPresignedRequest[] imageUploadPresignedRequests) {
        if (imageUploadPresignedRequests == null || imageUploadPresignedRequests.length == 0) {
            return new PresignedUrlResponse[0];
        }

        return Arrays.stream(imageUploadPresignedRequests)
                .map(this::createAdminPresignedUploadUrl)
                .toArray(PresignedUrlResponse[]::new);
    }

    @Override
    public void deleteAdminFile(DeleteImageRequest deleteImageRequest) {
        if (deleteImageRequest == null || deleteImageRequest.getUrl() == null) {
            log.warn("Delete request or URL is null, skipping deletion");
            return;
        }

        String objectKey = extractObjectKey(deleteImageRequest.getUrl());
        if (objectKey == null) {
            log.warn("Could not extract object key from URL: {}", deleteImageRequest.getUrl());
            return;
        }

        executorService.submit(() -> deleteS3Object(objectKey));
    }

    @Override
    public void deleteAdminFile(DeleteImageRequest[] deleteImageRequests) {
        if (deleteImageRequests == null || deleteImageRequests.length == 0) {
            log.debug("No delete requests provided");
            return;
        }

        executorService.submit(() -> {
            Arrays.stream(deleteImageRequests)
                    .filter(request -> request != null && request.getUrl() != null)
                    .map(request -> extractObjectKey(request.getUrl()))
                    .filter(objectKey -> objectKey != null)
                    .forEach(this::deleteS3Object);
        });
    }

    private String extractObjectKey(String url) {
        try {
            return cloudflareS3Service.getObjectNameFromUrl(url);
        } catch (Exception e) {
            log.error("Failed to extract object key from URL: {}", url, e);
            return null;
        }
    }

    private void deleteS3Object(String objectKey) {
        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(imageS3Config.getBucketName())
                    .key(objectKey)
                    .build();
            cloudflareS3Client.deleteObject(deleteObjectRequest);
            log.debug("Successfully deleted object: {}", objectKey);
        } catch (Exception e) {
            log.error("Failed to delete S3 object: {}", objectKey, e);
        }
    }

    @PreDestroy
    public void shutdownExecutorService() {
        if (executorService != null && !executorService.isShutdown()) {
            log.info("Shutting down executor service...");
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(SHUTDOWN_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                    log.warn("Executor service did not terminate gracefully, forcing shutdown");
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                log.warn("Interrupted while waiting for executor service shutdown", e);
                executorService.shutdownNow();
                Thread.currentThread().interrupt();
            }
            log.info("Executor service shutdown completed");
        }
    }
}
