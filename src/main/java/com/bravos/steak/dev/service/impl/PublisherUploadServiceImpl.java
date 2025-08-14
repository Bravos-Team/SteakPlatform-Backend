package com.bravos.steak.dev.service.impl;

import com.bravos.steak.common.model.GameS3Config;
import com.bravos.steak.common.model.ImageS3Config;
import com.bravos.steak.common.security.JwtTokenClaims;
import com.bravos.steak.common.service.snowflake.SnowflakeGenerator;
import com.bravos.steak.common.service.storage.impl.AwsS3Service;
import com.bravos.steak.common.service.storage.impl.CloudflareS3Service;
import com.bravos.steak.dev.model.PartUploadPresignedUrl;
import com.bravos.steak.dev.model.request.*;
import com.bravos.steak.dev.model.response.CompleteUploadResponse;
import com.bravos.steak.dev.model.response.PartUploadPresignedResponse;
import com.bravos.steak.dev.model.response.PresignedUrlResponse;
import com.bravos.steak.dev.service.PublisherUploadService;
import com.bravos.steak.exceptions.BadRequestException;
import com.bravos.steak.exceptions.ForbiddenException;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
public class PublisherUploadServiceImpl implements PublisherUploadService {

    private final CloudflareS3Service cloudflareS3Service;
    private final SnowflakeGenerator snowflakeGenerator;
    private final ExecutorService executorService;
    private final ImageS3Config imageS3Config;
    private final S3Client cloudflareS3Client;
    private final AwsS3Service awsS3Service;
    private final GameS3Config gameS3Config;

    public PublisherUploadServiceImpl(CloudflareS3Service cloudflareS3Service, SnowflakeGenerator snowflakeGenerator,
                                      ImageS3Config imageS3Config, S3Client cloudflareS3Client,
                                      AwsS3Service awsS3Service, GameS3Config gameS3Config) {
        this.cloudflareS3Service = cloudflareS3Service;
        this.snowflakeGenerator = snowflakeGenerator;
        this.cloudflareS3Client = cloudflareS3Client;
        this.executorService = Executors.newVirtualThreadPerTaskExecutor();
        this.imageS3Config = imageS3Config;
        this.awsS3Service = awsS3Service;
        this.gameS3Config = gameS3Config;
    }

    private Duration getDurationBySize(long fileSize) {
        if (fileSize < 10 * 1024 * 1024) {
            return Duration.ofMinutes(2).plusSeconds(30);
        }
        return Duration.ofMinutes(5);
    }

    @Override
    public PresignedUrlResponse createPublisherPresignedImageUrl(ImageUploadPresignedRequest imageUploadPresignedRequest) {
        String fileName = imageUploadPresignedRequest.getFileName();
        long fileSize = imageUploadPresignedRequest.getFileSize();
        int extensionIndex = fileName.lastIndexOf(".");
        if (extensionIndex < 0) {
            throw new BadRequestException("File name must have an extension");
        }
        String extension = fileName.substring(extensionIndex);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtTokenClaims jwtTokenClaims = (JwtTokenClaims) authentication.getDetails();
        long publisherId = (long) jwtTokenClaims.getOtherClaims().get("publisherId");
        String objectName = "publisher/" + publisherId + "/images/" + snowflakeGenerator.generateId() + extension;
        try {
            String signedUrl = cloudflareS3Service.generateS3PutSignedUrl(
                    imageS3Config.getBucketName(),
                    objectName,
                    getDurationBySize(fileSize));
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
    public PresignedUrlResponse[] createPublisherPresignedImageUrls(ImageUploadPresignedRequest[] imageUploadPresignedRequests) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtTokenClaims jwtTokenClaims = (JwtTokenClaims) authentication.getDetails();
        long publisherId = (long) jwtTokenClaims.getOtherClaims().get("publisherId");
        PresignedUrlResponse[] responses = new PresignedUrlResponse[imageUploadPresignedRequests.length];
        String publisherIdStr = String.valueOf(publisherId);

        for (int i = 0; i < imageUploadPresignedRequests.length; i++) {
            ImageUploadPresignedRequest request = imageUploadPresignedRequests[i];
            String fileName = request.getFileName();
            long fileSize = request.getFileSize();
            int extensionIndex = fileName.lastIndexOf(".");
            if (extensionIndex < 0) {
                throw new BadRequestException("File name must have an extension");
            }
            String extension = fileName.substring(extensionIndex);
            String objectName = "publisher/" + publisherIdStr + "/images/" + snowflakeGenerator.generateId() + extension;
            try {
                String signedUrl = cloudflareS3Service.generateS3PutSignedUrl(
                        imageS3Config.getBucketName(),
                        objectName,
                        getDurationBySize(fileSize));
                responses[i] = PresignedUrlResponse.builder()
                        .fileName(fileName)
                        .signedUrl(signedUrl)
                        .cdnFileName(objectName)
                        .build();
            } catch (Exception e) {
                log.error("Error when create presigned urls: {}", e.getMessage(), e);
                throw new RuntimeException("Error when create presigned url");
            }
        }

        return responses;
    }

    @Override
    public void deletePublisherImage(DeleteImageRequest deleteImageRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtTokenClaims jwtTokenClaims = (JwtTokenClaims) authentication.getDetails();
        long publisherId = (long) jwtTokenClaims.getOtherClaims().get("publisherId");

        if (!deleteImageRequest.getUrl().contains("publisher/" + publisherId)) {
            throw new ForbiddenException("Unauthorized attempt to delete image");
        }

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
                        .bucket(imageS3Config.getBucketName())
                        .key(cloudflareS3Service.getObjectNameFromUrl(deleteImageRequest.getUrl()))
                        .build();
                cloudflareS3Client.deleteObject(deleteObjectRequest);
            }
        });
    }

    @Override
    public PartUploadPresignedResponse createPublisherPartUploadPresignedUrl(
            GameUploadPresignedRequest gameUploadPresignedRequest) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtTokenClaims jwtTokenClaims = (JwtTokenClaims) authentication.getDetails();
        long publisherId = (long) jwtTokenClaims.getOtherClaims().get("publisherId");
        String uploaderId = String.valueOf(jwtTokenClaims.getId());
        String publisherIdStr = String.valueOf(publisherId);

        if(!gameUploadPresignedRequest.getFileName().endsWith(".tar.zst")) {
            throw new BadRequestException("Only .tar.zst files are allowed for game uploads");
        }

        String objectName = "publisher/" + publisherIdStr + "/games/" +
                snowflakeGenerator.generateId() + ".tar.zst";

        return awsS3Service.generateS3MultipartUpload(
                gameS3Config.getBucketName(),
                objectName,
                Map.of(
                        "Uploader-Id", uploaderId,
                        "Publisher-Id", publisherIdStr
                ),
                calculatePartSize(gameUploadPresignedRequest.getFileSize()));
    }

    @Override
    public PartUploadPresignedUrl[] recreatePresignedUploadUrl(RecreatePresignedUrlRequest request) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtTokenClaims jwtTokenClaims = (JwtTokenClaims) authentication.getDetails();
        long publisherId = (long) jwtTokenClaims.getOtherClaims().get("publisherId");

        if (!request.getObjectKey().startsWith("publisher/" + publisherId + "/games/")) {
            throw new ForbiddenException("Unauthorized attempt to recreate presigned upload URL");
        }

        return awsS3Service.regenerateS3MultipartUploadUrls(
                gameS3Config.getBucketName(),
                request.getObjectKey(),
                request.getUploadId(),
                request.getPartNumbers()
        );

    }

    @Override
    public CompleteUploadResponse completeMultipartUpload(CompleteMultipartRequest completeMultipartRequest) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtTokenClaims jwtTokenClaims = (JwtTokenClaims) authentication.getDetails();
        long publisherId = (long) jwtTokenClaims.getOtherClaims().get("publisherId");

        if (!completeMultipartRequest.getObjectKey().startsWith("publisher/" + publisherId + "/games/")) {
            throw new ForbiddenException("Unauthorized attempt to recreate presigned upload URL");
        }

        String objectUrl = awsS3Service.completeS3MultipartUpload(
                gameS3Config.getBucketName(),
                completeMultipartRequest.getObjectKey(),
                completeMultipartRequest.getUploadId(),
                completeMultipartRequest.getParts(),
                completeMultipartRequest.getChecksum()
        );

        return new CompleteUploadResponse(objectUrl,completeMultipartRequest.getChecksum());

    }

    private long calculatePartSize(long fileSize) {
        final long maxPartSize = 256 * 1024 * 1024;
        return Math.ceilDiv(fileSize, maxPartSize);
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
