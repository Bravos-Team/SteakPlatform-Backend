package com.bravos.steak.common.service.storage;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

import java.time.Duration;
import java.util.Map;

@Service
public abstract class S3Service {

    public String generateS3PutSignedUrl(String bucket, String objectKey, Map<String,String> metadata, Duration duration) {
        S3Presigner presigner = getS3Presigned();
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(objectKey)
                .metadata(metadata)
                .build();
        PresignedPutObjectRequest presignedPutObjectRequest = presigner
                .presignPutObject(p -> {
                    p.putObjectRequest(putObjectRequest);
                    p.signatureDuration(duration);
                });
        return presignedPutObjectRequest.url().toExternalForm();
    }

    public String generateS3GetSignedUrl(String bucket, String objectKey, Duration duration) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(objectKey)
                .build();
        PresignedGetObjectRequest presignedGetObjectRequest = getS3Presigned()
                .presignGetObject(p -> {
                    p.getObjectRequest(getObjectRequest);
                    p.signatureDuration(duration);
                });
        return presignedGetObjectRequest.url().toExternalForm();
    }

    public abstract S3Presigner getS3Presigned();

}
