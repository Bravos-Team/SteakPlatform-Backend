package com.bravos.steak.dev.service.impl;

import com.bravos.steak.common.security.JwtTokenClaims;
import com.bravos.steak.common.service.snowflake.SnowflakeGenerator;
import com.bravos.steak.common.service.storage.impl.CloudflareS3Service;
import com.bravos.steak.dev.model.request.DeleteImageRequest;
import com.bravos.steak.dev.model.request.ImageUploadPresignedRequest;
import com.bravos.steak.dev.model.response.PresignedUrlResponse;
import com.bravos.steak.dev.service.PublisherUploadService;
import com.bravos.steak.exceptions.ForbiddenException;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
public class PublisherUploadServiceImpl implements PublisherUploadService {

    private final CloudflareS3Service cloudflareS3Service;
    private final SnowflakeGenerator snowflakeGenerator;
    private final S3Client s3Client;
    private final String cloudflareS3BucketName;
    private final ExecutorService executorService;

    public PublisherUploadServiceImpl(CloudflareS3Service cloudflareS3Service, SnowflakeGenerator snowflakeGenerator,
                                      S3Client s3Client, String cloudflareS3BucketName) {
        this.cloudflareS3Service = cloudflareS3Service;
        this.snowflakeGenerator = snowflakeGenerator;
        this.s3Client = s3Client;
        this.cloudflareS3BucketName = cloudflareS3BucketName;
        this.executorService = Executors.newVirtualThreadPerTaskExecutor();
    }

    private Duration getDurationBySize(long fileSize) {
        if (fileSize < 10 * 1024 * 1024) {
            return Duration.ofMinutes(2).plusSeconds(30);
        }
        return Duration.ofMinutes(5);
    }

    @Override
    public PresignedUrlResponse createPublisherPresignedImageUrls(ImageUploadPresignedRequest imageUploadPresignedRequest) {
        String fileName = imageUploadPresignedRequest.getFileName();
        long fileSize = imageUploadPresignedRequest.getFileSize();
        String extension = fileName.substring(fileName.lastIndexOf("."));
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtTokenClaims jwtTokenClaims = (JwtTokenClaims) authentication.getDetails();
        long publisherId = (long) jwtTokenClaims.getOtherClaims().get("publisherId");
        StringBuilder objectName = new StringBuilder();
        objectName.append("publisher/")
                .append(publisherId)
                .append("/images/")
                .append(snowflakeGenerator.generateId())
                .append(extension);
        try {
            String signedUrl = cloudflareS3Service.generateS3PutSignedUrl(
                    cloudflareS3BucketName,
                    objectName.toString(),
                    getDurationBySize(fileSize));
            return PresignedUrlResponse.builder()
                    .fileName(fileName)
                    .signedUrl(signedUrl)
                    .build();
        } catch (Exception e) {
            log.error("Error when create presigned url: ", e);
            throw new RuntimeException("Error when create presigned url");
        }
    }

    @Override
    public PresignedUrlResponse[] createPublisherPresignedImageUrls(ImageUploadPresignedRequest[] imageUploadPresignedRequests) {
        PresignedUrlResponse[] responses = new PresignedUrlResponse[imageUploadPresignedRequests.length];
        for (int i = 0; i < imageUploadPresignedRequests.length; i++) {
            responses[i] = createPublisherPresignedImageUrls(imageUploadPresignedRequests[i]);
        }
        return responses;
    }

    @Override
    public void deletePublisherImage(DeleteImageRequest deleteImageRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtTokenClaims jwtTokenClaims = (JwtTokenClaims) authentication.getDetails();
        long publisherId = (long) jwtTokenClaims.getOtherClaims().get("publisherId");

        if (!deleteImageRequest.getUrl().contains("publisher/" + publisherId + "/images/")) {
            throw new ForbiddenException("Unauthorized attempt to delete image");
        }

        executorService.submit(() -> {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(cloudflareS3BucketName)
                    .key(getFileNameFromUrl(deleteImageRequest.getUrl()))
                    .build();
            s3Client.deleteObject(deleteObjectRequest);
        });
    }

    @Override
    public void deletePublisherImages(DeleteImageRequest[] deleteImageRequests) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtTokenClaims jwtTokenClaims = (JwtTokenClaims) authentication.getDetails();
        long publisherId = (long) jwtTokenClaims.getOtherClaims().get("publisherId");

        for (DeleteImageRequest deleteImageRequest : deleteImageRequests) {
            if (!deleteImageRequest.getUrl().contains("publisher/" + publisherId + "/images/")) {
                throw new ForbiddenException("Unauthorized attempt to delete image");
            }
        }

        executorService.submit(() -> {
            for (DeleteImageRequest deleteImageRequest : deleteImageRequests) {
                DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                        .bucket(cloudflareS3BucketName)
                        .key(getFileNameFromUrl(deleteImageRequest.getUrl()))
                        .build();
                s3Client.deleteObject(deleteObjectRequest);
            }
        });
    }

    private String getFileNameFromUrl(String url) {
        int idx = url.indexOf("/", url.indexOf("//") + 2);
        return idx != -1 ? url.substring(idx + 1) : url;
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
