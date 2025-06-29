package com.bravos.steak.common.service.storage.impl;

import com.bravos.steak.common.service.storage.S3Service;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Service("cloudflareS3Service")
public class CloudflareS3Service extends S3Service {

    private final S3Presigner cloudflareS3Presigner;
    private final S3Client cloudflareS3Client;

    public CloudflareS3Service(S3Presigner cloudflareS3Presigner, S3Client cloudflareS3Client) {
        super();
        this.cloudflareS3Presigner = cloudflareS3Presigner;
        this.cloudflareS3Client = cloudflareS3Client;
    }

    @Override
    public S3Presigner getS3Presigned() {
        return cloudflareS3Presigner;
    }

    @Override
    public S3Client getS3Client() {
        return cloudflareS3Client;
    }

}
