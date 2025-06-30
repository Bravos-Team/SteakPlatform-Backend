package com.bravos.steak.common.service.storage;

import com.bravos.steak.dev.model.PartInfo;
import com.bravos.steak.dev.model.response.PartUploadPresignedResponse;
import com.bravos.steak.dev.model.PartUploadPresignedUrl;
import com.bravos.steak.exceptions.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedUploadPartRequest;

import java.time.Duration;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
public abstract class S3Service {

    public String generateS3PutSignedUrl(String bucket, String objectKey, Duration duration) {
        return generateS3PutSignedUrl(bucket, objectKey, duration, null);
    }

    public String generateS3PutSignedUrl(String bucket, String objectKey,
                                         Duration duration, Map<String,String> metadata) {
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

    public PartUploadPresignedResponse generateS3MultipartUpload(String bucket, String objectKey,
                                                                 Map<String, String> metadata,
                                                                 long totalParts) {

        if(totalParts <= 0 || totalParts > 10000) {
            throw new IllegalArgumentException("Total parts must be between 1 and 10000");
        }

        CreateMultipartUploadRequest createRequest = CreateMultipartUploadRequest.builder()
                .bucket(bucket)
                .key(objectKey)
                .metadata(metadata)
                .checksumType(ChecksumType.FULL_OBJECT)
                .checksumAlgorithm(ChecksumAlgorithm.CRC32_C)
                .build();

        CreateMultipartUploadResponse createResponse;
        try {
            createResponse = getS3Client()
                    .createMultipartUpload(createRequest);
        } catch (AwsServiceException | SdkClientException e) {
            log.error("Failed to create multipart upload: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create multipart upload: " + e.getMessage());
        }

        String uploadId = createResponse.uploadId();

        PartUploadPresignedUrl[] presignedUrls;
        try {
            presignedUrls = new PartUploadPresignedUrl[Math.toIntExact(totalParts)];
            for (int i = 0; i < totalParts; i++) {
                UploadPartRequest uploadPartRequest = UploadPartRequest.builder()
                        .bucket(bucket)
                        .key(objectKey)
                        .uploadId(uploadId)
                        .partNumber(i + 1)
                        .build();

                PresignedUploadPartRequest presignedUploadRequest = getS3Presigned()
                        .presignUploadPart(p -> {
                            p.uploadPartRequest(uploadPartRequest);
                            p.signatureDuration(Duration.ofHours(12));
                        });

                presignedUrls[i] = new PartUploadPresignedUrl(i + 1, presignedUploadRequest.url().toExternalForm());
            }
        } catch (Exception e) {
            log.error("Failed to generate presigned URLs for multipart upload: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate presigned URLs for multipart upload: " + e.getMessage());
        }

        return PartUploadPresignedResponse.builder()
                .uploadId(uploadId)
                .objectKey(objectKey)
                .presignedUrls(presignedUrls)
                .build();

    }

    public String completeS3MultipartUpload(String bucket, String objectKey,
                                            String uploadId, PartInfo[] parts,
                                            String checksum) {

        if(parts.length < 1 || parts.length > 10000) {
            throw new IllegalArgumentException("Parts must be between 1 and 10000");
        }

        Arrays.sort(parts, Comparator.comparingInt(PartInfo::getPartNumber));

        if(parts.length == 1 && parts[0].getPartNumber() < 1 || parts[0].getPartNumber() > 10000) {
            throw new BadRequestException("Part number must be between 1 and 10000");
        }

        for(int i = 1; i < parts.length; i++) {
            if(Objects.equals(parts[i].getPartNumber(), parts[i - 1].getPartNumber()) ||
                    parts[i].getPartNumber() < 1 || parts[i].getPartNumber() > 10000) {
                throw new BadRequestException("Part numbers must be unique");
            }
        }

        CompletedPart[] completedParts = new CompletedPart[parts.length];
        for (int i = 0; i < parts.length; i++) {
            PartInfo partInfo = parts[i];
            completedParts[i] = CompletedPart.builder()
                    .partNumber(partInfo.getPartNumber())
                    .eTag(partInfo.getETag())
                    .build();
        }

        CompleteMultipartUploadRequest completeRequest = CompleteMultipartUploadRequest.builder()
                .bucket(bucket)
                .key(objectKey)
                .uploadId(uploadId)
                .multipartUpload(CompletedMultipartUpload.builder()
                        .parts(completedParts)
                        .build())
                .build();

        CompleteMultipartUploadResponse completeResponse;

        try {
            completeResponse = getS3Client()
                    .completeMultipartUpload(completeRequest);

            if(completeResponse.checksumCRC32C() != null && !completeResponse.checksumCRC32C().equals(checksum)) {
                deleteObject(bucket, objectKey);
                throw new BadRequestException("Checksum mismatch: expected " + checksum + ", got " + completeResponse.checksumCRC32C());
            }

        } catch (AwsServiceException | SdkClientException e) {
            log.error("Failed to complete multipart upload: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to complete multipart upload: " + e.getMessage());
        }

        return completeResponse.location();

    }

    public void deleteObject(String bucket, String objectKey) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(objectKey)
                .build();

        Thread.startVirtualThread(() -> {
            try {
                getS3Client().deleteObject(deleteObjectRequest);
            } catch (AwsServiceException | SdkClientException e) {
                log.error("Failed to delete object {} from bucket {}: {}", objectKey, bucket, e.getMessage(), e);
            }
        });
    }

    public PartUploadPresignedUrl[] regenerateS3MultipartUploadUrls(String bucket, String objectKey,
                                                                 String uploadId, Integer[] partsNumbers) {

        for (int partNumber : partsNumbers) {
            if (partNumber <= 0 || partNumber > 10000) {
                throw new IllegalArgumentException("Part number must be between 1 and 10000");
            }
        }

        PartUploadPresignedUrl[] presignedUrls = new PartUploadPresignedUrl[Math.toIntExact(partsNumbers.length)];

        try {
            for(int i = 0; i < partsNumbers.length; i++) {
                int partNumber = partsNumbers[i];
                UploadPartRequest uploadPartRequest = UploadPartRequest.builder()
                        .bucket(bucket)
                        .key(objectKey)
                        .uploadId(uploadId)
                        .partNumber(partNumber)
                        .build();

                PresignedUploadPartRequest presignedUploadRequest = getS3Presigned()
                        .presignUploadPart(p -> {
                            p.uploadPartRequest(uploadPartRequest);
                            p.signatureDuration(Duration.ofHours(12));
                        });

                presignedUrls[i] = new PartUploadPresignedUrl(partNumber, presignedUploadRequest.url().toExternalForm());
            }
        } catch (Exception e) {
            log.error("Failed to regenerate presigned URLs for multipart upload: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to regenerate presigned URLs for multipart upload: " + e.getMessage());
        }

        return presignedUrls;
    }

    public abstract S3Presigner getS3Presigned();

    public abstract S3Client getS3Client();

}
