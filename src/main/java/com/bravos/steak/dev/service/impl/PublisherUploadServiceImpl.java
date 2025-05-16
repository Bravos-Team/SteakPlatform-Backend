package com.bravos.steak.dev.service.impl;

import com.bravos.steak.common.security.JwtAuthentication;
import com.bravos.steak.common.service.auth.SessionService;
import com.bravos.steak.common.service.storage.impl.CloudflareS3Service;
import com.bravos.steak.dev.model.request.ImageUploadPresignedRequest;
import com.bravos.steak.dev.model.response.PresignedUrlResponse;
import com.bravos.steak.dev.service.PublisherUploadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Slf4j
@Service
public class PublisherUploadServiceImpl implements PublisherUploadService {

    private final CloudflareS3Service cloudflareS3Service;
    private final SessionService sessionService;

    public PublisherUploadServiceImpl(CloudflareS3Service cloudflareS3Service, SessionService sessionService) {
        this.cloudflareS3Service = cloudflareS3Service;
        this.sessionService = sessionService;
    }

    @Override
    public PresignedUrlResponse createUploadPublisherImagePresignedUrl(ImageUploadPresignedRequest imageUploadPresignedRequest) {
        String fileName = imageUploadPresignedRequest.getFileName();
        long fileSize = imageUploadPresignedRequest.getFileSize();
        String extension = fileName.substring(fileName.lastIndexOf("."));
        JwtAuthentication authentication = sessionService.getAuthentication();
        String folder = "publisher/" + authentication.getName();
        String objectName = folder + "/" + UUID.randomUUID() + extension;
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
