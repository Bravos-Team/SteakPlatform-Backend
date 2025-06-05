package com.bravos.steak.dev.service.impl;

import com.bravos.steak.common.service.snowflake.SnowflakeGenerator;
import com.bravos.steak.common.service.storage.impl.CloudflareS3Service;
import com.bravos.steak.dev.model.request.ImageUploadPresignedRequest;
import com.bravos.steak.dev.model.response.PresignedUrlResponse;
import com.bravos.steak.dev.service.PublisherUploadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
public class PublisherUploadServiceImpl implements PublisherUploadService {

    private final CloudflareS3Service cloudflareS3Service;
    private final SnowflakeGenerator snowflakeGenerator;

    public PublisherUploadServiceImpl(CloudflareS3Service cloudflareS3Service, SnowflakeGenerator snowflakeGenerator) {
        this.cloudflareS3Service = cloudflareS3Service;
        this.snowflakeGenerator = snowflakeGenerator;
    }

    @Override
    public PresignedUrlResponse createUploadPublisherImagePresignedUrl(ImageUploadPresignedRequest imageUploadPresignedRequest) {
        String fileName = imageUploadPresignedRequest.getFileName();
        long fileSize = imageUploadPresignedRequest.getFileSize();
        String extension = fileName.substring(fileName.lastIndexOf("."));
        String objectName = snowflakeGenerator.generateId() + extension;
        try {
            String signedUrl = cloudflareS3Service.generateS3PutSignedUrl(
                    System.getProperty("CF_S3_BUCKET_NAME"),
                    objectName,imageUploadPresignedRequest.getMetadata(),
                    getDurationBySize(fileSize));
            return PresignedUrlResponse.builder()
                    .fileName(fileName)
                    .signedUrl(signedUrl)
                    .build();
        } catch (Exception e) {
            log.error("Error when create presigned url: ",e);
            throw new RuntimeException("Error when create presigned url");
        }
    }

    @Override
    public PresignedUrlResponse createUploadGameImagePresignedUrl(Long gameId, ImageUploadPresignedRequest imageUploadPresignedRequest) {

        return null;
    }

    private Duration getDurationBySize(long fileSize) {
        if(fileSize < 10 * 1024 * 1024) {
            return Duration.ofMinutes(2).plusSeconds(30);
        }
        return Duration.ofMinutes(5);
    }

}
