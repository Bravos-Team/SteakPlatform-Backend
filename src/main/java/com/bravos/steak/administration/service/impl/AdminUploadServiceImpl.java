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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
public class AdminUploadServiceImpl implements AdminUploadService {

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
        String fileName = imageUploadPresignedRequest.getFileName();
        String extension = fileName.substring(fileName.lastIndexOf("."));
        String objectName = snowflakeGenerator.generateId() + extension;
        try {
            String signedUrl = cloudflareS3Service.generateS3PutSignedUrl(
                    imageS3Config.getBucketName(),
                    objectName,
                    Duration.ofHours(1));
            return PresignedUrlResponse.builder()
                    .fileName(fileName)
                    .signedUrl(signedUrl)
                    .cdnFileName(objectName)
                    .build();
        } catch (Exception e) {
            log.error("Error when create presigned url: ", e);
            throw new RuntimeException("Error when create presigned url");
        }
    }

    @Override
    public PresignedUrlResponse[] createAdminPresignedUploadUrls(ImageUploadPresignedRequest[] imageUploadPresignedRequests) {
        if (imageUploadPresignedRequests == null || imageUploadPresignedRequests.length == 0) {
            return new PresignedUrlResponse[0];
        }
        PresignedUrlResponse[] responses = new PresignedUrlResponse[imageUploadPresignedRequests.length];
        for (int i = 0; i < imageUploadPresignedRequests.length; i++) {
            responses[i] = createAdminPresignedUploadUrl(imageUploadPresignedRequests[i]);
        }
        return responses;
    }

    @Override
    public void deleteAdminFile(DeleteImageRequest deleteImageRequest) {
        String objectKey = cloudflareS3Service.getObjectNameFromUrl(deleteImageRequest.getUrl());
        executorService.submit(() -> {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(imageS3Config.getBucketName())
                    .key(objectKey)
                    .build();
            cloudflareS3Client.deleteObject(deleteObjectRequest);
        });
    }

    @Override
    public void deleteAdminFile(DeleteImageRequest[] deleteImageRequests) {
        executorService.submit(() -> {
            for (DeleteImageRequest deleteImageRequest : deleteImageRequests) {
                DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                        .bucket(imageS3Config.getBucketName())
                        .key(cloudflareS3Service.getObjectNameFromUrl(deleteImageRequest.getUrl()))
                        .build();
                cloudflareS3Client.deleteObject(deleteObjectRequest);
            }
        });
    }

    @PreDestroy
    public void shutdownExecutorService() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(60, java.util.concurrent.TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
            }
        }
        log.info("Executor service delete objects shutdown completed.");
    }

}
