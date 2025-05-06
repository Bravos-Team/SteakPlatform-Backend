package com.bravos.steak.common.service.storage;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

import java.time.Duration;
import java.util.Map;

public abstract class S3Service {

    private final S3Presigner s3Presigner = createPresigner();

    protected abstract S3Client s3Client();

    public final String generateS3PutSignedUrl(String bucket, String objectKey, Map<String,String> metadata, Duration duration) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(objectKey)
                .metadata(metadata)
                .build();
        PresignedPutObjectRequest presignedPutObjectRequest = s3Presigner
                .presignPutObject(p -> {
                    p.putObjectRequest(putObjectRequest);
                    p.signatureDuration(duration);
                });
        return presignedPutObjectRequest.url().toExternalForm();
    }

    public final String generateS3GetSignedUrl(String bucket, String objectKey, Duration duration) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(objectKey)
                .build();
        PresignedGetObjectRequest presignedGetObjectRequest = s3Presigner
                .presignGetObject(p -> {
                    p.getObjectRequest(getObjectRequest);
                    p.signatureDuration(duration);
                });
        return presignedGetObjectRequest.url().toExternalForm();
    }

    private S3Presigner createPresigner() {
        return S3Presigner.builder()
                .s3Client(s3Client())
                .region(Region.US_EAST_1)
                .build();
    }

}
